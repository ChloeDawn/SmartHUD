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
import net.insomniakitten.smarthud.util.Profiler;
import net.insomniakitten.smarthud.util.Profiler.Section;
import net.insomniakitten.smarthud.util.StackHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Iterator;

import static net.insomniakitten.smarthud.config.GeneralConfig.configPickup;
import static net.insomniakitten.smarthud.feature.pickup.PickupManager.items;

@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(Side.CLIENT)
public class PickupRenderer {

    // TODO: Smooth movement during list offsets

    private static final CubicBezierInterpolator expiredInterpolator = new CubicBezierInterpolator(.32, -0.5, .41, .62);
    private static final float                   expiredAnimateTime  = 1000;

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
            if (renderLabel(fontRenderer, renderItem, x, y1, cachedItem, event.getPartialTicks())) {
                iterator.remove();
            }
        }
        Profiler.end();
    }

    public static boolean renderLabel(FontRenderer fontRenderer, RenderItem renderItem,
                                   float renderX, float renderY, CachedItem cachedItem, float partialTicks) {
        boolean hasItemName = configPickup.hudStyle.hasItemName();
        String key = "label.smarthud.pickup." + (hasItemName ? "long" : "short");
        String count = StackHelper.getAbbreviatedValue(cachedItem.getCount());
        String label = I18n.format(key, count, cachedItem.getName());
        float end = renderX + fontRenderer.getStringWidth(label);
        long remaining = cachedItem.getRemainingTicks();
        if (remaining < 0) {
            float time = Math.abs(remaining) + partialTicks;
            if (time > expiredAnimateTime) {
                return true;
            }
            renderX -= expiredInterpolator.interpolate(0, expiredAnimateTime, time) * end;
        }
        GlStateManager.pushMatrix();
        fontRenderer.drawStringWithShadow(label, renderX, renderY, 0xFFFFFFFF);// | (int) alpha << 24);
        GlStateManager.popMatrix();
        if (configPickup.hudStyle.hasItemIcon()) {
            GlStateManager.pushMatrix();
            GlStateManager.enableAlpha();
            RenderHelper.enableGUIStandardItemLighting();
            GlStateManager.translate(renderX - 14, renderY - 1.5, 0);
            GlStateManager.scale(0.67, 0.67, 0.67);
            renderItem.renderItemAndEffectIntoGUI(cachedItem.getStack(), 0, 0);
            RenderHelper.disableStandardItemLighting();
            GlStateManager.popMatrix();
        }
        return false;
    }

    private static float linearInterpolate(float f1, float f2, float mu) {
        return (f1 * (1 - mu) + f2 * mu);
    }

}
