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

import com.google.common.collect.EvictingQueue;
import net.insomniakitten.smarthud.SmartHUDConfig;
import net.insomniakitten.smarthud.feature.ISmartHUDFeature;
import net.insomniakitten.smarthud.util.RenderContext;
import net.insomniakitten.smarthud.util.CachedItem;
import net.insomniakitten.smarthud.util.HandHelper;
import net.insomniakitten.smarthud.util.ModProfiler;
import net.insomniakitten.smarthud.util.StackHelper;
import net.insomniakitten.smarthud.util.interpolation.CubicBezierInterpolator;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import java.util.Iterator;

import static net.insomniakitten.smarthud.SmartHUDConfig.PICKUP;

public final class PickupFeature implements ISmartHUDFeature {

    private static final CubicBezierInterpolator ANIMATION = new CubicBezierInterpolator(0.42D, 0.0D, 0.58D, 1.0D);
    private static final float ANIMATION_DURATION = 10.0F;

    public PickupFeature() {}

    @Override
    public boolean isEnabled() {
        return SmartHUDConfig.PICKUP.isEnabled;
    }

    @Override
    public RenderGameOverlayEvent.ElementType getType() {
        return RenderGameOverlayEvent.ElementType.CHAT;
    }

    @Override
    public void onRenderTickPre(RenderContext ctx) {
        EvictingQueue<CachedItem> items = PickupQueue.getItems();
        if (items.isEmpty()) return;
        ModProfiler.start(ModProfiler.Section.RENDER_PICKUP);
        int x = PICKUP.hudStyle.hasItemIcon() ? 17 : 4;
        int y = ctx.getScreenHeight() - (ctx.getFontHeight() * items.size()) - (2 * items.size());
        Iterator<CachedItem> iterator = items.iterator();
        for (int i = 0; iterator.hasNext(); ++i) {
            CachedItem cachedItem = iterator.next();
            int y1 = y + (ctx.getFontHeight() * i) + (2 * i);
            if (renderLabel(ctx, x, y1, cachedItem)) {
                iterator.remove();
            }
        }
        ModProfiler.end();
    }

    private boolean renderLabel(RenderContext ctx, float renderX, float renderY, CachedItem item) {
        String key = "label.smarthud.pickup." + (PICKUP.hudStyle.hasItemName() ? "long" : "short");
        String count = StackHelper.getAbbreviatedValue(item.getCount());
        String label = I18n.format(key, count, item.getName());

        int labelWidth = ctx.getStringWidth(label);
        float labelX = HandHelper.handleVariableOffset(renderX, labelWidth);
        float iconX = HandHelper.handleVariableOffset(renderX - 14.0F, 10.72F);

        if (HandHelper.isLeftHanded()) {
            labelX += ctx.getScreenWidth();
            iconX += ctx.getScreenHeight();
        }

        if (item.getRemainingTicks() < 0) {
            float time = Math.abs(item.getRemainingTicks()) + ctx.getPartialTicks();
            if (time > ANIMATION_DURATION) {
                return true;
            }
            float end = renderX + labelWidth;
            float interpolation = ANIMATION.interpolate(0, ANIMATION_DURATION, time) * end;
            labelX += HandHelper.isLeftHanded() ? interpolation : -interpolation;
            iconX += HandHelper.isLeftHanded() ? interpolation : -interpolation;
        }

        ctx.drawString(label, labelX, renderY, 0xFFFFFFFF); // | (int) alpha << 24);

        if (PICKUP.hudStyle.hasItemIcon()) {
            GlStateManager.enableAlpha();
            RenderHelper.enableGUIStandardItemLighting();
            GlStateManager.pushMatrix();
            GlStateManager.translate(iconX, renderY - 1.5D, 0.0D);
            GlStateManager.scale(0.67D, 0.67D, 0.67D);
            ctx.renderItem(item.getStack(), 0, 0, true);
            GlStateManager.popMatrix();
            RenderHelper.disableStandardItemLighting();
        }

        return false;
    }

}
