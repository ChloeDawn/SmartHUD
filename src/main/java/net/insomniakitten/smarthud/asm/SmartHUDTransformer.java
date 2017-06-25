package net.insomniakitten.smarthud.asm;

/*
 *  Copyright 2017 InsomniaKitten
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

import net.insomniakitten.smarthud.lib.LibInfo;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.common.FMLLog;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;
import org.objectweb.asm.util.Printer;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceMethodVisitor;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

// Boilerplate code taken with love from Vazkii's Quark mod
// Quark is distributed at https://github.com/Vazkii/Quark

public class SmartHUDTransformer implements IClassTransformer, Opcodes {

    private static final String MC_VERSION = "%mc_version%";
    private static final Map<String, Transformer> transformers = new HashMap<>();

    private static final ClassNameHashMap MAPPINGS_1_11_2 = new ClassNameHashMap(
            "net/minecraft/client/gui/GuiIngame", "bff",
            "net/minecraft/client/gui/ScaledResolution", "bfi",
            "net/minecraft/util/ResourceLocation", "kq",
            "net/minecraft/client/renderer/texture/TextureManager", "byx"
    );
    private static final ClassNameHashMap MAPPINGS_1_12 = new ClassNameHashMap(
            "net/minecraft/client/gui/GuiIngame", "bio",
            "net/minecraft/client/gui/ScaledResolution", "bir",
            "net/minecraft/util/ResourceLocation", "nd",
            "net/minecraft/client/renderer/texture/TextureManager", "cdp"
    );

    public static ClassNameHashMap classMappings;

    static {
        if (Objects.equals(MC_VERSION, "1.11.2"))
            classMappings = MAPPINGS_1_11_2;
        else classMappings = MAPPINGS_1_12;
        transformers.put("net.minecraft.client.gui.GuiIngame",
                SmartHUDTransformer::transformGuiIngame);
    }

    private static byte[] transformGuiIngame(byte[] basicClass) {

        MethodSignature sig0 = new MethodSignature(
                "renderHotbar", "func_180479_a", "a",
                "(Lnet/minecraft/client/gui/ScaledResolution;F)V");
        MethodSignature sig1 = new MethodSignature(
                "bindTexture", "func_110577_a", "a",
                "(Lnet/minecraft/util/ResourceLocation;)V");

        return transform(basicClass, sig0, "attack indicator rendering hook",
                combine((AbstractInsnNode node) ->
                                node.getOpcode() == INVOKEVIRTUAL
                                        && sig1.matches((MethodInsnNode) node)
                                        && node.getPrevious().getOpcode() == GETSTATIC
                                        && (((FieldInsnNode) node.getPrevious()).name.equals("d")
                                        || ((FieldInsnNode) node.getPrevious()).name.equals("field_110324_m")
                                        || ((FieldInsnNode) node.getPrevious()).name.equals("ICONS")),
                        (MethodNode method, AbstractInsnNode node) -> {
                            InsnList newInstructions = new InsnList();
                            newInstructions.add(new VarInsnNode(ILOAD, 12));
                            newInstructions.add(new MethodInsnNode(
                                    INVOKESTATIC, LibInfo.CLASS_ASM_HOOKS,
                                    "transformAttackIndicator", "(I)I", false));
                            newInstructions.add(new VarInsnNode(ISTORE, 12));
                            method.instructions.insert(node, newInstructions);
                            return true;
                        }));

    }

    // BOILERPLATE

    public static byte[] transform(byte[] basicClass, MethodSignature sig, String simpleDesc, MethodAction action) {
        ClassReader reader = new ClassReader(basicClass);
        ClassNode node = new ClassNode();
        reader.accept(node, 0);

        log("Applying transformation to method (" + sig + ")");
        log("Attempting to insert " + simpleDesc);
        boolean didAnything = findMethodAndTransform(node, sig, action);

        if (didAnything) {
            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            node.accept(writer);
            return writer.toByteArray();
        }

        return basicClass;
    }

    public static boolean findMethodAndTransform(ClassNode node, MethodSignature sig, MethodAction pred) {
        for (MethodNode method : node.methods) {
            if (sig.matches(method)) {
                boolean finish = pred.test(method);
                log("Patch result: " + (finish ? "SUCCESS" : "!!! FAILED !!!"));
                return finish;
            }
        }

        log("Patch result: !!! METHOD NOT FOUND !!!");

        return false;
    }

    public static MethodAction combine(NodeFilter filter, NodeAction action) {
        return (MethodNode node) -> applyOnNode(node, filter, action);
    }

    public static boolean applyOnNode(MethodNode method, NodeFilter filter, NodeAction action) {
        AbstractInsnNode[] nodes = method.instructions.toArray();
        Iterator<AbstractInsnNode> iterator = new InsnArrayIterator(nodes);

        boolean didAny = false;
        while (iterator.hasNext()) {
            AbstractInsnNode anode = iterator.next();
            if (filter.test(anode)) {
                didAny = true;
                if (action.test(method, anode))
                    break;
            }
        }

        return didAny;
    }

    public static MethodAction combineByLast(NodeFilter filter, NodeAction action) {
        return (MethodNode node) -> applyOnNodeByLast(node, filter, action);
    }

    public static boolean applyOnNodeByLast(MethodNode method, NodeFilter filter, NodeAction action) {
        AbstractInsnNode[] nodes = method.instructions.toArray();
        ListIterator<AbstractInsnNode> iterator = new InsnArrayIterator(nodes, method.instructions.size());

        boolean didAny = false;
        while (iterator.hasPrevious()) {
            AbstractInsnNode anode = iterator.previous();
            if (filter.test(anode)) {
                didAny = true;
                if (action.test(method, anode))
                    break;
            }
        }

        return didAny;
    }

    public static MethodAction combineFrontPivot(NodeFilter pivot, NodeFilter filter, NodeAction action) {
        return (MethodNode node) -> applyOnNodeFrontPivot(node, pivot, filter, action);
    }

    public static boolean applyOnNodeFrontPivot(MethodNode method, NodeFilter pivot, NodeFilter filter, NodeAction action) {
        AbstractInsnNode[] nodes = method.instructions.toArray();
        ListIterator<AbstractInsnNode> iterator = new InsnArrayIterator(nodes);

        int pos = 0;

        boolean didAny = false;
        while (iterator.hasNext()) {
            pos++;
            AbstractInsnNode pivotTest = iterator.next();
            if (pivot.test(pivotTest)) {
                ListIterator<AbstractInsnNode> internal = new InsnArrayIterator(nodes, pos);
                while (internal.hasPrevious()) {
                    AbstractInsnNode anode = internal.previous();
                    if (filter.test(anode)) {
                        didAny = true;
                        if (action.test(method, anode))
                            break;
                    }
                }
            }
        }

        return didAny;
    }

    public static MethodAction combineBackPivot(NodeFilter pivot, NodeFilter filter, NodeAction action) {
        return (MethodNode node) -> applyOnNodeBackPivot(node, pivot, filter, action);
    }

    public static boolean applyOnNodeBackPivot(MethodNode method, NodeFilter pivot, NodeFilter filter, NodeAction action) {
        AbstractInsnNode[] nodes = method.instructions.toArray();
        ListIterator<AbstractInsnNode> iterator = new InsnArrayIterator(nodes, method.instructions.size());

        int pos = method.instructions.size();

        boolean didAny = false;
        while (iterator.hasPrevious()) {
            pos--;
            AbstractInsnNode pivotTest = iterator.previous();
            if (pivot.test(pivotTest)) {
                ListIterator<AbstractInsnNode> internal = new InsnArrayIterator(nodes, pos);
                while (internal.hasNext()) {
                    AbstractInsnNode anode = internal.next();
                    if (filter.test(anode)) {
                        didAny = true;
                        if (action.test(method, anode))
                            break;
                    }
                }
            }
        }

        return didAny;
    }

    public static MethodAction combineFrontFocus(NodeFilter focus, NodeFilter filter, NodeAction action) {
        return (MethodNode node) -> applyOnNodeFrontFocus(node, focus, filter, action);
    }

    public static boolean applyOnNodeFrontFocus(MethodNode method, NodeFilter focus, NodeFilter filter, NodeAction action) {
        AbstractInsnNode[] nodes = method.instructions.toArray();
        ListIterator<AbstractInsnNode> iterator = new InsnArrayIterator(nodes);

        int pos = method.instructions.size();

        boolean didAny = false;
        while (iterator.hasNext()) {
            pos++;
            AbstractInsnNode focusTest = iterator.next();
            if (focus.test(focusTest)) {
                ListIterator<AbstractInsnNode> internal = new InsnArrayIterator(nodes, pos);
                while (internal.hasNext()) {
                    AbstractInsnNode anode = internal.next();
                    if (filter.test(anode)) {
                        didAny = true;
                        if (action.test(method, anode))
                            break;
                    }
                }
            }
        }

        return didAny;
    }

    public static MethodAction combineBackFocus(NodeFilter focus, NodeFilter filter, NodeAction action) {
        return (MethodNode node) -> applyOnNodeBackFocus(node, focus, filter, action);
    }

    public static boolean applyOnNodeBackFocus(MethodNode method, NodeFilter focus, NodeFilter filter, NodeAction action) {
        AbstractInsnNode[] nodes = method.instructions.toArray();
        ListIterator<AbstractInsnNode> iterator = new InsnArrayIterator(nodes, method.instructions.size());

        int pos = method.instructions.size();

        boolean didAny = false;
        while (iterator.hasPrevious()) {
            pos--;
            AbstractInsnNode focusTest = iterator.previous();
            if (focus.test(focusTest)) {
                ListIterator<AbstractInsnNode> internal = new InsnArrayIterator(nodes, pos);
                while (internal.hasPrevious()) {
                    AbstractInsnNode anode = internal.previous();
                    if (filter.test(anode)) {
                        didAny = true;
                        if (action.test(method, anode))
                            break;
                    }
                }
            }
        }

        return didAny;
    }

    public static void log(String str) {
        FMLLog.info("[Smart HUD ASM] %s", str);
    }

    public static void prettyPrint(MethodNode node) {
        Printer printer = new Textifier();

        TraceMethodVisitor visitor = new TraceMethodVisitor(printer);
        node.accept(visitor);

        StringWriter sw = new StringWriter();
        printer.print(new PrintWriter(sw));
        printer.getText().clear();

        log(sw.toString());
    }

    public static void prettyPrint(AbstractInsnNode node) {
        Printer printer = new Textifier();

        TraceMethodVisitor visitor = new TraceMethodVisitor(printer);
        node.accept(visitor);

        StringWriter sw = new StringWriter();
        printer.print(new PrintWriter(sw));
        printer.getText().clear();

        log(sw.toString());
    }

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (transformers.containsKey(transformedName)) {
            String[] arr = transformedName.split("\\.");
            log("Preparing to transform " + arr[arr.length - 1]);
            return transformers.get(transformedName).apply(basicClass);
        }

        return basicClass;
    }

    public interface Transformer extends Function<byte[], byte[]> {
        // NO-OP
    }

    public interface MethodAction extends Predicate<MethodNode> {
        // NO-OP
    }

    // Basic interface aliases to not have to clutter up the code with generics over and over again

    public interface NodeFilter extends Predicate<AbstractInsnNode> {
        // NO-OP
    }

    public interface NodeAction extends BiPredicate<MethodNode, AbstractInsnNode> {
        // NO-OP
    }

    private static class InsnArrayIterator implements ListIterator<AbstractInsnNode> {

        private final AbstractInsnNode[] array;
        private int index;

        public InsnArrayIterator(AbstractInsnNode[] array) {
            this(array, 0);
        }

        public InsnArrayIterator(AbstractInsnNode[] array, int index) {
            this.array = array;
            this.index = index;
        }

        @Override
        public boolean hasNext() {
            return array.length > index + 1 && index >= 0;
        }

        @Override
        public AbstractInsnNode next() {
            if (hasNext())
                return array[++index];
            return null;
        }

        @Override
        public boolean hasPrevious() {
            return index > 0 && index <= array.length;
        }

        @Override
        public AbstractInsnNode previous() {
            if (hasPrevious())
                return array[--index];
            return null;
        }

        @Override
        public int nextIndex() {
            return hasNext() ? index + 1 : array.length;
        }

        @Override
        public int previousIndex() {
            return hasPrevious() ? index - 1 : 0;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Unimplemented");
        }

        @Override
        public void set(AbstractInsnNode e) {
            throw new UnsupportedOperationException("Unimplemented");
        }

        @Override
        public void add(AbstractInsnNode e) {
            throw new UnsupportedOperationException("Unimplemented");
        }
    }

    public static class MethodSignature {
        private final String funcName, srgName, obfName, funcDesc, obfDesc;

        public MethodSignature(String funcName, String srgName, String obfName, String funcDesc) {
            this.funcName = funcName;
            this.srgName = srgName;
            this.obfName = obfName;
            this.funcDesc = funcDesc;
            this.obfDesc = obfuscate(funcDesc);
        }

        private static String obfuscate(String desc) {
            for (String s : classMappings.keySet())
                if (desc.contains(s))
                    desc = desc.replaceAll(s, classMappings.get(s));

            return desc;
        }

        @Override
        public String toString() {
            return "Names [" + funcName + ", " + srgName + ", " + obfName + "] Descriptor " + funcDesc + " / " + obfDesc;
        }

        public boolean matches(String methodName, String methodDesc) {
            return (methodName.equals(funcName) || methodName.equals(obfName) || methodName.equals(srgName))
                    && (methodDesc.equals(funcDesc) || methodDesc.equals(obfDesc));
        }

        public boolean matches(MethodNode method) {
            return matches(method.name, method.desc);
        }

        public boolean matches(MethodInsnNode method) {
            return matches(method.name, method.desc);
        }

    }

}
