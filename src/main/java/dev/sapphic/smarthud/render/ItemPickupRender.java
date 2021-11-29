package dev.sapphic.smarthud.render;

import com.google.common.collect.EvictingQueue;
import dev.sapphic.smarthud.config.ModulesConfig;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import dev.sapphic.smarthud.SmartHUD;
import dev.sapphic.smarthud.event.ItemPickupQueue;
import dev.sapphic.smarthud.util.CachedItem;
import dev.sapphic.smarthud.util.HandHelper;
import dev.sapphic.smarthud.util.IRenderEvent;
import dev.sapphic.smarthud.util.RenderContext;
import dev.sapphic.smarthud.util.StringHelper;
import dev.sapphic.smarthud.util.interpolation.CubicBezierInterpolator;

import java.util.Iterator;

public final class ItemPickupRender implements IRenderEvent {

    private static final CubicBezierInterpolator ANIMATION = new CubicBezierInterpolator(0.42D, 0.0D, 0.58D, 1.0D);
    private static final float ANIMATION_DURATION = 10.0F;

    public ItemPickupRender() {}

    @Override
    public boolean canRender() {
        return ModulesConfig.ITEM_PICKUP_HUD.isEnabled;
    }

    @Override
    public RenderGameOverlayEvent.ElementType getType() {
        return RenderGameOverlayEvent.ElementType.TEXT;
    }

    @Override
    public void onRenderTickPre(RenderContext ctx) {
        EvictingQueue<CachedItem> items = ItemPickupQueue.getItems();
        if (items.isEmpty()) return;
        int x = ModulesConfig.ITEM_PICKUP_HUD.hudStyle.hasItemIcon() ? 17 : 4;
        int y = ctx.getScreenHeight() - (ctx.getFontHeight() * items.size()) - (2 * items.size());
        Iterator<CachedItem> iterator = items.iterator();
        for (int i = 0; iterator.hasNext(); ++i) {
            CachedItem cachedItem = iterator.next();
            int y1 = y + (ctx.getFontHeight() * i) + (2 * i);
            if (renderLabel(ctx, x, y1, cachedItem)) {
                iterator.remove();
            }
        }
    }

    private boolean renderLabel(RenderContext ctx, float renderX, float renderY, CachedItem item) {
        boolean name = ModulesConfig.ITEM_PICKUP_HUD.hudStyle.hasItemName();
        String key = "label." + SmartHUD.ID + ".pickup." + (name ? "long" : "short");
        String count = StringHelper.getAbbreviatedValue(item.getCount());
        String label = I18n.format(key, count, item.getName());

        int color = 0xFFFFFF;
        int labelWidth = ctx.getStringWidth(label);
        float labelX = HandHelper.handleVariableOffset(renderX, labelWidth);
        float iconX = HandHelper.handleVariableOffset(renderX - 14.0F, 10.72F);

        if (HandHelper.isLeftHanded()) {
            labelX += ctx.getScreenWidth();
            iconX += ctx.getScreenHeight();
        }

        long remaining = item.getRemainingTicks(ModulesConfig.ITEM_PICKUP_HUD.displayTime);

        if (remaining < 0) {
            float time = Math.abs(remaining) + ctx.getPartialTicks();
            if (time > ANIMATION_DURATION) return true;
//            switch (ITEM_PICKUP_HUD.animationStyle) {
//                case FADE:
//                    float alpha = (float) (1.0F * Math.sin(time));
//                    color |= (int) alpha << 24; // FIXME
//                    break;
//                case GLIDE:
                    float end = renderX + labelWidth;
                    float interpolation = ANIMATION.interpolate(0, ANIMATION_DURATION, time) * end;
                    labelX += HandHelper.isLeftHanded() ? interpolation : -interpolation;
                    iconX += HandHelper.isLeftHanded() ? interpolation : -interpolation;
//                    break;
//            }

        }

        ctx.drawString(label, labelX, renderY, color);

        if (ModulesConfig.ITEM_PICKUP_HUD.hudStyle.hasItemIcon()) {
            GlStateManager.enableAlpha();
            RenderHelper.enableGUIStandardItemLighting();
            GlStateManager.pushMatrix();
            GlStateManager.translate(iconX, renderY - 1.5D, 0.0D);
            GlStateManager.scale(0.67D, 0.67D, 0.67D);
            ctx.renderItem(item.getStack(), 0, 0, true); // TODO: Support AnimationStyle#FADE
            GlStateManager.popMatrix();
            RenderHelper.disableStandardItemLighting();
        }

        return false;
    }

}
