package net.insomniakitten.smarthud.asm;

/*
 *  Boilerplate code taken with love from Vazkii's Quark mod
 *  Quark is distributed at https://github.com/Vazkii/Quark
 */

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.common.FMLLog;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

public class SmartHUDTransformer implements IClassTransformer, Opcodes {

    public static final String CLASS_ASM_HOOKS = "net/insomniakitten/smarthud/asm/SmartHUDHooks";

    private static final Map<String, Transformer> transformers = new HashMap<>();

    private static final String CLASS_SCALED_RESOLUTION = "net/minecraft/client/gui/ScaledResolution";
    private static final String CLASS_RESOURCE_LOCATION = "net/minecraft/util/ResourceLocation";

    static {
        transformers.put("net.minecraft.client.gui.GuiIngame", SmartHUDTransformer::transformGuiIngame);
    }

    private static byte[] transformGuiIngame(byte[] basicClass) {

        MethodSignature sig0= new MethodSignature(
                "renderHotbar",
                "func_180479_a",
                "(L" + CLASS_SCALED_RESOLUTION + ";F)V");

        MethodSignature sig1 = new MethodSignature(
                "bindTexture",
                "func_110577_a",
                "(L" + CLASS_RESOURCE_LOCATION+ ";)V");

        return transform(basicClass, sig0, "attack indicator rendering hook",
                combine((AbstractInsnNode node) ->
                                node.getOpcode() == INVOKEVIRTUAL
                                        && sig1.matches((MethodInsnNode) node)
                                        && node.getPrevious().getOpcode() == GETSTATIC
                                        && (((FieldInsnNode) node.getPrevious()).name.equals("field_110324_m")
                                        || ((FieldInsnNode) node.getPrevious()).name.equals("ICONS")),
                        (MethodNode method, AbstractInsnNode node) -> {
                            InsnList newInstructions = new InsnList();
                            newInstructions.add(new VarInsnNode(ILOAD, 12));
                            newInstructions.add(new MethodInsnNode(
                                    INVOKESTATIC, CLASS_ASM_HOOKS,
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

        log("Applying transformation to method " + sig);
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

    public static void log(String str) {
        FMLLog.log.info("[Smart HUD ASM] {}", str);
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
        private final String funcName, srgName, funcDesc;

        public MethodSignature(String funcName, String srgName, String funcDesc) {
            this.funcName = funcName;
            this.srgName = srgName;
            this.funcDesc = funcDesc;
        }

        @Override
        public String toString() {
            return "[" + funcName + ", " + srgName + "] | " + funcDesc;
        }

        public boolean matches(String methodName, String methodDesc) {
            return (methodName.equals(funcName) || methodName.equals(srgName)) && (methodDesc.equals(funcDesc));
        }

        public boolean matches(MethodNode method) {
            return matches(method.name, method.desc);
        }

        public boolean matches(MethodInsnNode method) {
            return matches(method.name, method.desc);
        }

    }

}
