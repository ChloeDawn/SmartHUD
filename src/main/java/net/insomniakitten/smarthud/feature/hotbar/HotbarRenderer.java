package net.insomniakitten.smarthud.feature.hotbar;

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
import net.insomniakitten.smarthud.inventory.InventoryManager;
import net.insomniakitten.smarthud.util.CachedItem;
import net.insomniakitten.smarthud.util.HandHelper;
import net.insomniakitten.smarthud.util.Profiler;
import net.insomniakitten.smarthud.util.Profiler.Section;
import net.insomniakitten.smarthud.util.StackHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static net.insomniakitten.smarthud.config.GeneralConfig.configHotbar;

@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(Side.CLIENT)
public class HotbarRenderer {

    private static final ResourceLocation HUD_ELEMENTS
            = new ResourceLocation(SmartHUD.MOD_ID, "textures/hud/elements.png");

    @SubscribeEvent
    public static void onRenderGameOverlay(RenderGameOverlayEvent.Pre event) {
        if (!HotbarManager.canRender(event)) return;

        Profiler.start(Section.RENDER_HOTBAR);

        NonNullList<CachedItem> cachedItems = InventoryManager.getInventory();
        int slots = cachedItems.size() < configHotbar.slotLimit ? cachedItems.size() : configHotbar.slotLimit;

        int baseOffset = 98;
        int displayWidth = event.getResolution().getScaledWidth();
        int displayHeight = event.getResolution().getScaledHeight();
        int center = displayWidth / 2;

        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
        RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();

        if (cachedItems.size() > 0) {

            if (!configHotbar.hudStyle.equals(HotbarConfig.HotbarStyle.INVISIBLE)) {
                int width = 44 + (20 * (cachedItems.size() - 2)) - 2;
                int offset = HandHelper.handleVariableOffset(baseOffset, width);
                renderHotbarBackground(center + offset, displayHeight - 22, slots);
            }

            for (int i = 0; i < slots; i++) {
                CachedItem cachedItem = cachedItems.get(i);
                ItemStack stack = cachedItem.getStack();
                int stackOffset = baseOffset + 3 + (20 * i);
                int stackX = center + HandHelper.handleVariableOffset(stackOffset, 16);
                int stackY = displayHeight - (16 + 3);

                GlStateManager.pushMatrix();
                RenderHelper.enableGUIStandardItemLighting();
                renderItem.renderItemAndEffectIntoGUI(stack, stackX, stackY);
                RenderHelper.disableStandardItemLighting();
                GlStateManager.popMatrix();

                boolean renderOverlay = !stack.isStackable() && configHotbar.renderOverlays;
                boolean showStackSize = cachedItem.getCount() > 1 && configHotbar.showStackSize;

                if (renderOverlay) {
                    GlStateManager.pushMatrix();
                    GlStateManager.disableDepth();
                    renderItem.renderItemOverlays(fontRenderer, stack, stackX, stackY);
                    GlStateManager.popMatrix();
                }

                if (showStackSize) {
                    int count = configHotbar.mergeDuplicates ? cachedItem.getCount() : cachedItem.getActualCount();
                    int stringWidth = fontRenderer.getStringWidth(Integer.toString(count));
                    int labelOffset = baseOffset + (20 - stringWidth) + (20 * i);
                    int labelX =  center + HandHelper.handleVariableOffset(labelOffset, stringWidth);
                    int labelY = displayHeight - fontRenderer.FONT_HEIGHT - 1;

                    if (labelX < center) labelX += 18 - stringWidth;
                    // Keeps string to right edge of slot in left-handed mode

                    GlStateManager.pushMatrix();
                    GlStateManager.disableDepth();
                    fontRenderer.drawStringWithShadow(StackHelper.getAbbreviatedValue(count), labelX, labelY, -1);
                    GlStateManager.popMatrix();
                }
            }

        } else {
            if (configHotbar.alwaysShow) {
                int offset = HandHelper.handleVariableOffset(baseOffset, 20);
                renderHotbarBackground(center + offset, displayHeight - 22, 1);
            }
        }

        Profiler.end();
    }

    public static void renderHotbarBackground(int x, int y, int slots) {
        GuiIngame gui = Minecraft.getMinecraft().ingameGUI;
        int textureY = configHotbar.hudStyle.getTextureY();
        GlStateManager.pushMatrix();
        Minecraft.getMinecraft().renderEngine.bindTexture(HUD_ELEMENTS);
        gui.drawTexturedModalRect(x, y, 0, textureY, 11, 22);
        for (int i = 0; i < ((slots - 1) * 2); ++i) {
            int textureX = i % 2 == 0 ? 32 : 22;
            gui.drawTexturedModalRect(x + (11 + (10 * i)), y, textureX, textureY, 10, 22);
        }
        gui.drawTexturedModalRect(x + (20 * slots) - 9, y, 11, textureY, 11, 22);
        GlStateManager.popMatrix();
    }

}
