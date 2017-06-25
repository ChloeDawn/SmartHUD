package net.insomniakitten.smarthud.event;

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

import net.insomniakitten.smarthud.SmartHUD;
import net.insomniakitten.smarthud.asm.SmartHUDHooks;
import net.insomniakitten.smarthud.lib.LibConfig;
import net.insomniakitten.smarthud.lib.LibInfo;
import net.insomniakitten.smarthud.util.StackHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@SuppressWarnings({"MethodCallSideOnly", "LocalVariableDeclarationSideOnly"})
@Mod.EventBusSubscriber(Side.CLIENT)
public class RenderHandler {

    static {
        if (SmartHUD.DEOBF)
            SmartHUD.LOGGER.info("Registering RenderHandler to the Event Bus");
    }

    private static final ResourceLocation HUD_ELEMENTS = new ResourceLocation(
            LibInfo.MOD_ID + ":textures/hud/elements.png");

    private static int
            hotbarPadding = 182, itemSize = 16,
            outerSlotPadding = 3, innerSlotPadding = 2,
            slotHeight = itemSize + (outerSlotPadding * 2),
            outerSlotWidth = itemSize + (outerSlotPadding * 2),
            innerSlotWidth = itemSize + (innerSlotPadding * 2),
            uvBegin = 0, uvEnd = 11,
            uvSec1 = 32, uvSec2 = 22,
            uvRounded = 0, uvSharp = 22;
    // Blame tterrag1098 for hating on my usage "magic numbers"

    @SubscribeEvent
    public static void onHUDRender(RenderGameOverlayEvent.Pre event) {
        NonNullList<ItemStack> items = InventoryHandler.getPlayerItems();
        if (LibConfig.isEnabled && event.getType().equals(ElementType.HOTBAR)) {
            Minecraft mc = Minecraft.getMinecraft();

            int w = event.getResolution().getScaledWidth();
            int h = event.getResolution().getScaledHeight();
            int uvY = LibConfig.hudStyle > 0 ? uvSharp : uvRounded;
            int offsetX = (hotbarPadding / 2) + itemSize - (outerSlotPadding * 3);
            int slots = items.size() < LibConfig.slotLimit ? items.size() : LibConfig.slotLimit;
            int x = (w / 2) + handleVariableOffset(offsetX,
                    outerSlotWidth * 2 + (innerSlotWidth * (items.size() - 2)) - (outerSlotPadding - 1));

            if (items.size() > 0) {

                mc.mcProfiler.startSection(LibInfo.PROFILE_RENDER_SLOTS);

                if (LibConfig.hudStyle != 2) {

                    mc.renderEngine.bindTexture(HUD_ELEMENTS);

                    mc.ingameGUI.drawTexturedModalRect(
                            x, h - slotHeight, uvBegin, uvY,
                            (outerSlotWidth / 2), slotHeight); // Begin component

                    for (int i = 0; i < ((slots - 1) * 2); ++i) {
                        int newX = x + ((outerSlotWidth / 2) + ((innerSlotWidth / 2) * i));
                        mc.ingameGUI.drawTexturedModalRect(
                                newX, h - slotHeight,
                                i % 2 == 0 ? uvSec1 : uvSec2, uvY,
                                (innerSlotWidth / 2), slotHeight); // Inner components
                    }

                    mc.ingameGUI.drawTexturedModalRect(
                            x + ((innerSlotWidth * slots) - innerSlotWidth / 2)
                                    + ((outerSlotWidth - innerSlotWidth) / 2),
                            h - slotHeight, uvEnd, uvY,
                            (outerSlotWidth / 2), slotHeight); // End component

                }

                mc.mcProfiler.endStartSection(LibInfo.PROFILE_RENDER_SLOTS);

                mc.mcProfiler.startSection(LibInfo.PROFILE_RENDER_ITEMS);

                for (int i = 0; i < slots; i++) {
                    ItemStack stack = items.get(i);
                    int stringWidth = mc.fontRenderer.getStringWidth(String.valueOf(stack.getCount()));

                    int stackX = (w / 2) + handleVariableOffset(
                            offsetX + outerSlotPadding
                                    + (innerSlotWidth * i), itemSize);

                    int overlayX = (w / 2) + handleVariableOffset(
                            offsetX + outerSlotPadding
                                    + (innerSlotWidth * i)
                                    + (itemSize - stringWidth), itemSize);

                    RenderHelper.enableGUIStandardItemLighting();
                    mc.getRenderItem().renderItemAndEffectIntoGUI(stack, stackX, h - itemSize - outerSlotPadding);

                    boolean renderOverlay = !stack.isStackable() && LibConfig.renderOverlays;
                    boolean showStackSize = (stack.getCount() > 1 && LibConfig.showStackSize);

                    if (renderOverlay)
                        mc.getRenderItem().renderItemOverlays(mc.fontRenderer, stack, stackX, h - itemSize - outerSlotPadding);
                    if (showStackSize)
                        renderTotalCount(stack, overlayX + 1, h - 1);

                    RenderHelper.disableStandardItemLighting();
                }

                mc.mcProfiler.endStartSection(LibInfo.PROFILE_RENDER_ITEMS);

            } else if (LibConfig.alwaysShow && LibConfig.hudStyle != 2) {

                int x2 = (w / 2) + handleVariableOffset(offsetX, outerSlotWidth - innerSlotPadding);

                mc.mcProfiler.startSection(LibInfo.PROFILE_RENDER_SLOTS);

                mc.renderEngine.bindTexture(HUD_ELEMENTS);

                mc.ingameGUI.drawTexturedModalRect(
                        x2, h - slotHeight, uvBegin, uvY,
                        (outerSlotWidth / 2), slotHeight);

                mc.ingameGUI.drawTexturedModalRect(
                        x2 + ((innerSlotWidth) - innerSlotWidth / 2)
                                + ((outerSlotWidth - innerSlotWidth) / 2),
                        h - slotHeight, uvEnd, uvY,
                        (outerSlotWidth / 2), slotHeight);

                mc.mcProfiler.endStartSection(LibInfo.PROFILE_RENDER_SLOTS);

            }
        }
    }

    /** Method used to calculate the required offset of the attack indicator
     *  @see SmartHUDHooks#transformAttackIndicator(int) for the ASM hook
     * @return An int used to offset the element
     */
    public static int getAttackIndicatorOffset() {
        NonNullList<ItemStack> items = InventoryHandler.getPlayerItems();
        boolean hasItems = items.size() > 0;
        boolean alwaysShow = LibConfig.alwaysShow;
        int limit = LibConfig.slotLimit;
        int slots = items.size() < limit ? items.size() : limit;
        int paddingSlot = hasItems ? itemSize * slots : alwaysShow ? itemSize : 0;
        int paddingFixed = hasItems || alwaysShow ? outerSlotPadding : 0;
        int paddingIn = hasItems? (innerSlotPadding * slots) * 2 : alwaysShow ? innerSlotPadding * 2 : 0;
        int paddingOut = hasItems || alwaysShow ? outerSlotPadding * 2 : 0;

        return (paddingIn + paddingSlot + paddingOut) + paddingFixed;
    }


    /** Used to render the total count of an ItemStack calculated from the player's inventory
     *  Values above a certain threshold will be abbreviated
     *  @see StackHandler#getAbbreviatedValue(Number)
     *
     * @param stack The ItemStack you want to render a count for
     * @param x The initial x offset of the count
     * @param y The initial y offset of the count
     */
    private static void renderTotalCount(ItemStack stack, int x, int y) {
        FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
        EntityPlayer player = Minecraft.getMinecraft().player;
        InventoryPlayer inv = player.inventory;
        String stackCount = String.valueOf(stack.getCount());
        int totalCount = 0;

        if (LibConfig.avoidDuplicates)
            for (int i = 9; i < inv.getSizeInventory() - 1; ++i) {
                ItemStack stack1 = inv.getStackInSlot(i);
                if (ItemStack.areItemsEqual(stack, stack1)) {
                    totalCount += stack1.getCount();
                }
            }

        String formattedCount = StackHandler.getAbbreviatedValue(totalCount);
        if (!LibConfig.avoidDuplicates) formattedCount = stackCount;

        GlStateManager.disableDepth();
        GlStateManager.disableBlend();
        fr.drawStringWithShadow(formattedCount, x, y - fr.FONT_HEIGHT, -1);
        GlStateManager.enableDepth();
        GlStateManager.enableBlend();

    }


    /** Used to automatically adjust the HUDs offset on the screen depending on
     *  the current game setting for the player's main hand. This aids in supporting
     *  left-handed mode, avoiding conflicts with the vanilla HUD elements.
     *
     * @param currentOffset The current offset of the HUD element (averaged from the screen center)
     * @param objectWidth The current width of the element, used when inverting the position to the negative
     * @return The new offset depending on the current game setting
     */
    private static int handleVariableOffset(int currentOffset, int objectWidth) {
        GameSettings config = Minecraft.getMinecraft().gameSettings;
        int newOffset = 0;
        if (config.mainHand.equals(EnumHandSide.LEFT)) {
            currentOffset = -currentOffset;
            newOffset = -objectWidth;
        }
        return currentOffset + newOffset;
    }

}
