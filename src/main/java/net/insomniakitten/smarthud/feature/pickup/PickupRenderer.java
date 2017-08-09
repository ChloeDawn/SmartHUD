package net.insomniakitten.smarthud.feature.pickup;

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
import net.insomniakitten.smarthud.util.CachedItem;
import net.insomniakitten.smarthud.util.HandHelper;
import net.insomniakitten.smarthud.util.Profiler;
import net.insomniakitten.smarthud.util.Profiler.Section;
import net.insomniakitten.smarthud.util.StackHelper;
import net.insomniakitten.smarthud.util.interpolation.CubicBezierInterpolator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.Iterator;

import static net.insomniakitten.smarthud.config.GeneralConfig.configPickup;
import static net.insomniakitten.smarthud.feature.pickup.PickupManager.items;

@Mod.EventBusSubscriber(modid = SmartHUD.MOD_ID, value = Side.CLIENT)
public class PickupRenderer {

    // TODO: Smooth movement during list offsets

    private static final CubicBezierInterpolator ANIMATION = new CubicBezierInterpolator(0.42, 0, 0.58, 1);
    private static final float ANIMATION_DURATION = 10;

    @SubscribeEvent
    public static void onRenderGameOverlay(RenderGameOverlayEvent.Pre event) {
        if (!PickupManager.canRender(event) || items.isEmpty()) return;

        Profiler.start(Section.RENDER_PICKUP);

        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
        RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();

        int x = configPickup.hudStyle.hasItemIcon() ? 17 : 4;
        int h = event.getResolution().getScaledHeight();
        int y = h - (fontRenderer.FONT_HEIGHT * items.size()) - (2 * items.size());

        Iterator<CachedItem> iterator = items.iterator();

        for (int i = 0; iterator.hasNext(); ++i) {
            CachedItem cachedItem = iterator.next();
            int y1 = y + (fontRenderer.FONT_HEIGHT * i) + (2 * i);
            if (renderLabel(fontRenderer, renderItem, x, y1, cachedItem, event)) {
                iterator.remove();
            }
        }
        Profiler.end();
    }

    public static boolean renderLabel(
            FontRenderer fontRenderer, RenderItem renderItem,
            float renderX, float renderY,
            CachedItem cachedItem, RenderGameOverlayEvent event) {
        boolean hasItemName = configPickup.hudStyle.hasItemName();
        String key = "label.smarthud.pickup." + (hasItemName ? "long" : "short");
        String count = StackHelper.getAbbreviatedValue(cachedItem.getCount());
        String label = I18n.format(key, count, cachedItem.getName());

        int labelWidth = fontRenderer.getStringWidth(label);
        float labelX = HandHelper.handleVariableOffset(renderX, labelWidth);
        float iconX = HandHelper.handleVariableOffset(renderX - 14, 10.72f);

        if (HandHelper.isLeftHanded()) {
            labelX += event.getResolution().getScaledWidth();
            iconX += event.getResolution().getScaledWidth();
        }

        float end = renderX + fontRenderer.getStringWidth(label);
        long remaining = cachedItem.getRemainingTicks();
        if (remaining < 0) {
            float time = Math.abs(remaining) + event.getPartialTicks();
            if (time > ANIMATION_DURATION) {
                return true;
            }
            float interpolation = ANIMATION.interpolate(0, ANIMATION_DURATION, time) * end;
            labelX += HandHelper.isLeftHanded() ? interpolation : -interpolation;
            iconX += HandHelper.isLeftHanded() ? interpolation : -interpolation;
        }

        fontRenderer.drawStringWithShadow(label, labelX, renderY, 0xFFFFFFFF);// | (int) alpha << 24);

        if (configPickup.hudStyle.hasItemIcon()) {
            GlStateManager.enableAlpha();
            RenderHelper.enableGUIStandardItemLighting();
            GlStateManager.pushMatrix();
            GlStateManager.translate(iconX, renderY - 1.5, 0);
            GlStateManager.scale(0.67, 0.67, 0.67);
            renderItem.renderItemAndEffectIntoGUI(cachedItem.getStack(), 0, 0);
            GlStateManager.popMatrix();
            RenderHelper.disableStandardItemLighting();
        }
        return false;
    }

}
