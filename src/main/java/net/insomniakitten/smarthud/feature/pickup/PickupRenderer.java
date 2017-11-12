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

import net.insomniakitten.smarthud.util.CachedItem;
import net.insomniakitten.smarthud.util.HandHelper;
import net.insomniakitten.smarthud.util.ModProfiler;
import net.insomniakitten.smarthud.util.ModProfiler.Section;
import net.insomniakitten.smarthud.util.StackHelper;
import net.insomniakitten.smarthud.util.interpolation.CubicBezierInterpolator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import java.util.Iterator;

import static net.insomniakitten.smarthud.SmartHUDConfig.PICKUP;
import static net.insomniakitten.smarthud.feature.pickup.PickupManager.items;

public final class PickupRenderer {

    // TODO: Smooth movement during list offsets

    private static final CubicBezierInterpolator ANIMATION = new CubicBezierInterpolator(0.42, 0, 0.58, 1);
    private static final float ANIMATION_DURATION = 10;

    private PickupRenderer() {}

    public static void renderPickupHUD(RenderGameOverlayEvent.Pre event) {
        if (items.isEmpty()) return;

        ModProfiler.start(Section.RENDER_PICKUP);

        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
        RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();

        int h = event.getResolution().getScaledHeight();
        int x = PICKUP.hudStyle.hasItemIcon() ? 17 : 4;
        int y = h - (fontRenderer.FONT_HEIGHT * items.size()) - ((PICKUP.showModId ? fontRenderer.FONT_HEIGHT + 2 : 2) * items.size());

        Iterator<CachedItem> iterator = items.iterator();

        for (int i = 0; iterator.hasNext(); ++i) {
            CachedItem cachedItem = iterator.next();
            int y1 = y + ((PICKUP.showModId ? fontRenderer.FONT_HEIGHT * 2 : fontRenderer.FONT_HEIGHT) * i) + ((PICKUP.showModId ? 4 : 2) * i);
            if (renderLabel(fontRenderer, renderItem, x, y1, cachedItem, event)) {
                iterator.remove();
            }
        }

        ModProfiler.end();
    }

    public static boolean renderLabel(FontRenderer fr, RenderItem ri, float renderX, float renderY, CachedItem item, RenderGameOverlayEvent event) {
        String key = "label.smarthud.pickup." + (PICKUP.hudStyle.hasItemName() ? "long" : "short");
        String count = StackHelper.getAbbreviatedValue(item.getCount());
        String label = I18n.format(key, count, item.getName());

        int labelWidth = fr.getStringWidth(label);
        float labelX = HandHelper.handleVariableOffset(renderX, labelWidth);
        float iconX = HandHelper.handleVariableOffset(renderX - 14, 10.72f);

        if (HandHelper.isLeftHanded()) {
            labelX += event.getResolution().getScaledWidth();
            iconX += event.getResolution().getScaledWidth();
        }

        if (item.getRemainingTicks() < 0) {
            float time = Math.abs(item.getRemainingTicks()) + event.getPartialTicks();
            if (time > ANIMATION_DURATION) {
                return true;
            }
            float end = renderX + fr.getStringWidth(label);
            float interpolation = ANIMATION.interpolate(0, ANIMATION_DURATION, time) * end;
            labelX += HandHelper.isLeftHanded() ? interpolation : -interpolation;
            iconX += HandHelper.isLeftHanded() ? interpolation : -interpolation;
        }

        if (PICKUP.showModId) renderY -= fr.FONT_HEIGHT / 2;

        float labelY = renderY - (PICKUP.showModId ? fr.FONT_HEIGHT / 2 : 0);
        fr.drawStringWithShadow(label, labelX, labelY, 0xFFFFFFFF);// | (int) alpha << 24);

        if (PICKUP.showModId) {
            String modName = item.getModName();
            int blue = 0x4241FC;
            float modNameY = labelY + fr.FONT_HEIGHT;
            fr.drawStringWithShadow(modName, labelX, modNameY, blue);
        }

        if (PICKUP.hudStyle.hasItemIcon()) {
            GlStateManager.enableAlpha();
            RenderHelper.enableGUIStandardItemLighting();
            GlStateManager.pushMatrix();
            GlStateManager.translate(iconX, renderY - 1.5, 0);
            GlStateManager.scale(0.67, 0.67, 0.67);
            ri.renderItemAndEffectIntoGUI(item.getStack(), 0, 0);
            GlStateManager.popMatrix();
            RenderHelper.disableStandardItemLighting();
        }
        return false;
    }

}
