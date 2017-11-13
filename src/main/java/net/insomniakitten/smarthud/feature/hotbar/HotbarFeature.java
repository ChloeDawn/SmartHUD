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

import com.google.common.collect.ImmutableList;
import net.insomniakitten.smarthud.SmartHUD;
import net.insomniakitten.smarthud.SmartHUDConfig;
import net.insomniakitten.smarthud.feature.ISmartHUDFeature;
import net.insomniakitten.smarthud.feature.RenderContext;
import net.insomniakitten.smarthud.util.CachedItem;
import net.insomniakitten.smarthud.util.HandHelper;
import net.insomniakitten.smarthud.util.ModProfiler;
import net.insomniakitten.smarthud.util.StackHelper;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import static net.insomniakitten.smarthud.SmartHUDConfig.HOTBAR;

public class HotbarFeature implements ISmartHUDFeature {

    private static final ResourceLocation HUD_ELEMENTS = new ResourceLocation(SmartHUD.ID, "textures/hud/elements.png");

    private static final int ATTACK_INDICATOR_HOTBAR = 2;
    private static final int ATTACK_INDICATOR_VOID = -1304094787;

    public HotbarFeature() {}

    @Override
    public boolean isEnabled() {
        return SmartHUDConfig.HOTBAR.isEnabled;
    }

    @Override
    public RenderGameOverlayEvent.ElementType getType() {
        return RenderGameOverlayEvent.ElementType.HOTBAR;
    }

    @Override
    public void onRenderTickPre(RenderContext ctx) {
        ModProfiler.start(ModProfiler.Section.RENDER_HOTBAR);

        ImmutableList<CachedItem> cachedItems = InventoryCache.getInventory();
        int slots = cachedItems.size() < HOTBAR.slotLimit ? cachedItems.size() : HOTBAR.slotLimit;

        int baseOffset = 98;
        int center = ctx.getScreenWidth() / 2;

        if (cachedItems.size() > 0) {
            if (!HOTBAR.hudStyle.isInvisible()) {
                int width = 44 + (20 * (cachedItems.size() - 2)) - 2;
                int offset = (int) HandHelper.handleVariableOffset(baseOffset, width);
                renderHotbarBackground(ctx, center + offset, ctx.getScreenHeight() - 22, slots);
            }

            for (int i = 0; i < slots; i++) {
                CachedItem cachedItem = cachedItems.get(i);
                ItemStack stack = cachedItem.getStack();
                int stackOffset = baseOffset + 3 + (20 * i);
                int stackX = center + (int) HandHelper.handleVariableOffset(stackOffset, 16);
                int stackY = ctx.getScreenHeight() - (16 + 3);

                RenderHelper.enableGUIStandardItemLighting();
                ctx.renderItem(stack, stackX, stackY, true);
                RenderHelper.disableStandardItemLighting();

                boolean renderOverlay = !stack.isStackable() && HOTBAR.renderOverlays;
                boolean showStackSize = cachedItem.getCount() > 1 && HOTBAR.showStackSize;

                if (renderOverlay) {
                    ctx.renderItemOverlays(stack, stackX, stackY);
                }

                if (showStackSize) {
                    int count = HOTBAR.mergeDuplicates ? cachedItem.getCount() : cachedItem.getActualCount();
                    int stringWidth = ctx.getFontRenderer().getStringWidth(Integer.toString(count));
                    int labelOffset = baseOffset + (20 - stringWidth) + (20 * i);
                    int labelX = center + (int) HandHelper.handleVariableOffset(labelOffset, stringWidth);
                    int labelY = ctx.getScreenHeight() - ctx.getFontHeight() - 1;

                    if (labelX < center) labelX += 18 - stringWidth;
                    // Keeps string to right edge of slot in left-handed mode

                    GlStateManager.disableDepth();
                    ctx.drawString(StackHelper.getAbbreviatedValue(count), labelX, labelY, -1);
                }
            }

        } else if (HOTBAR.alwaysShow && !HOTBAR.hudStyle.isInvisible()) {
            int offset = (int) HandHelper.handleVariableOffset(baseOffset, 20);
            renderHotbarBackground(ctx, center + offset, ctx.getScreenHeight() - 22, 1);
        }

        ModProfiler.end();

        GameSettings cfg = ctx.getGameSettings();
        if (cfg.attackIndicator == ATTACK_INDICATOR_HOTBAR) {
            cfg.attackIndicator = ATTACK_INDICATOR_VOID;
        }
    }

    @Override
    public void onRenderTickPost(RenderContext ctx) {
        GameSettings cfg = ctx.getGameSettings();
        if (cfg.attackIndicator == ATTACK_INDICATOR_VOID) {
            cfg.attackIndicator = ATTACK_INDICATOR_HOTBAR;
            if (ctx.getRenderViewEntity() instanceof EntityPlayer) {
                EntityPlayer player = (EntityPlayer) ctx.getRenderViewEntity();
                EnumHandSide side = player.getPrimaryHand().opposite();
                float strength = ctx.getPlayer().getCooledAttackStrength(0);
                if (strength < 1) {
                    int halfWidth = ctx.getScreenWidth() / 2;
                    int y = ctx.getScreenHeight() - 20;
                    int offset = 91 + getAttackIndicatorOffset();
                    int x = halfWidth + (side == EnumHandSide.RIGHT ? -offset - 22 : offset + 6);
                    int strPixel = (int) (strength * 19);
                    GlStateManager.color(1, 1, 1, 1);
                    GlStateManager.enableRescaleNormal();
                    GlStateManager.enableBlend();
                    GlStateManager.tryBlendFuncSeparate(
                            SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA,
                            SourceFactor.ONE, DestFactor.ZERO
                    );
                    RenderHelper.enableGUIStandardItemLighting();
                    ctx.bindTexture(Gui.ICONS);
                    ctx.drawTexturedModalRect(x, y, 0, 94, 18, 18);
                    ctx.drawTexturedModalRect(x, y + 18 - strPixel, 18, 112 - strPixel, 18, strPixel);
                    RenderHelper.disableStandardItemLighting();
                    GlStateManager.disableRescaleNormal();
                    GlStateManager.disableBlend();
                }
            }
        }
    }

    public static int getAttackIndicatorOffset() {
        ImmutableList<CachedItem> cachedItems = InventoryCache.getInventory();
        int slot = 20, padding = 9;
        if (cachedItems.size() > 0) {
            int slots = cachedItems.size() < HOTBAR.slotLimit
                        ? cachedItems.size()
                        : HOTBAR.slotLimit;
            return (slot * slots) + padding;
        } else if (HOTBAR.alwaysShow) {
            return slot + padding;
        } else return 0;
    }

    private void renderHotbarBackground(RenderContext ctx, int x, int y, int slots) {
        ctx.bindTexture(HUD_ELEMENTS);
        int textureY = HOTBAR.hudStyle.getTextureY();
        ctx.drawTexturedModalRect(x, y, 0, textureY, 11, 22);
        for (int i = 0; i < ((slots - 1) * 2); ++i) {
            int textureX = i % 2 == 0 ? 32 : 22;
            ctx.drawTexturedModalRect(x + (11 + (10 * i)), y, textureX, textureY, 10, 22);
        }
        ctx.drawTexturedModalRect(x + (20 * slots) - 9, y, 11, textureY, 11, 22);
    }

}
