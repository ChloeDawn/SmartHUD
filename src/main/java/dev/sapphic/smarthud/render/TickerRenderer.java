package dev.sapphic.smarthud.render;

import com.google.common.collect.EvictingQueue;
import dev.sapphic.smarthud.SmartHud;
import dev.sapphic.smarthud.config.TickerConfig;
import dev.sapphic.smarthud.item.TickerItem;
import dev.sapphic.smarthud.item.TickerQueue;
import dev.sapphic.smarthud.render.interpolation.CubicBezierInterpolator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumHandSide;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

import java.util.Iterator;

@Mod.EventBusSubscriber(value = Side.CLIENT, modid = SmartHud.MOD_ID)
public final class TickerRenderer {
  private static final CubicBezierInterpolator ANIMATION =
      new CubicBezierInterpolator(0.42F, 0.0F, 0.58F, 1.0F);
  private static final float ANIMATION_DURATION = 10.0F;

  private TickerRenderer() {}

  @SubscribeEvent
  public static void draw(final RenderGameOverlayEvent.Pre event) {
    if (!TickerConfig.enabled || (event.getType() != ElementType.TEXT)) {
      return;
    }

    final EvictingQueue<TickerItem> queue = TickerQueue.get();

    if (queue.isEmpty()) {
      return;
    }

    final Minecraft minecraft = Minecraft.getMinecraft();
    final boolean left = minecraft.player.getPrimaryHand() == EnumHandSide.LEFT;
    final int x = TickerConfig.style.hasIcons() ? 17 : 4;
    final ScaledResolution resolution = event.getResolution();
    final int fontHeight = minecraft.fontRenderer.FONT_HEIGHT;
    final int screenHeight = resolution.getScaledHeight();
    final int y = screenHeight - (fontHeight * queue.size()) - (2 * queue.size());
    final Iterator<TickerItem> iterator = queue.iterator();

    for (int index = 0; iterator.hasNext(); ++index) {
      final TickerItem item = iterator.next();
      final int oy = y + (fontHeight * index) + (2 * index);
      final boolean named = TickerConfig.style.hasLabels();
      final String locale = SmartHud.MOD_ID + ".ticker.label." + (named ? "long" : "short");
      final String itemCount = item.abbreviatedCount();
      final String label = I18n.format(locale, itemCount, item.stack().getDisplayName());
      final int labelWidth = minecraft.fontRenderer.getStringWidth(label);
      float labelX = left ? (resolution.getScaledWidth() + (-x - labelWidth)) : x;
      float iconX = left ? (screenHeight + (-(x - 14.0F) - 10.72F)) : (x - 14.0F);
      final long ticks = item.remainingTime();

      if (ticks < 0) {
        final float time = Math.abs(ticks) + event.getPartialTicks();

        if (time > ANIMATION_DURATION) {
          iterator.remove();
          return;
        }

        final float endX = (float) x + labelWidth;
        final float interpolation = ANIMATION.interpolate(0, ANIMATION_DURATION, time) * endX;

        labelX += left ? interpolation : -interpolation;
        iconX += left ? interpolation : -interpolation;
      }

      minecraft.fontRenderer.drawString(label, labelX, oy, 0xFFFFFF, true);

      if (TickerConfig.style.hasIcons()) {
        GlStateManager.enableAlpha();
        RenderHelper.enableGUIStandardItemLighting();
        GlStateManager.pushMatrix();
        GlStateManager.translate(iconX, (float) oy - 1.5D, 0.0D);
        GlStateManager.scale(0.67, 0.67, 0.67);

        minecraft.getRenderItem().renderItemAndEffectIntoGUI(item.stack(), 0, 0);

        GlStateManager.popMatrix();
        RenderHelper.disableStandardItemLighting();
      }
    }
  }
}
