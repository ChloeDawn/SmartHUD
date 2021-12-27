package dev.sapphic.smarthud.render;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.UnmodifiableListIterator;
import dev.sapphic.smarthud.SmartHud;
import dev.sapphic.smarthud.config.SlotsConfig;
import dev.sapphic.smarthud.item.SlotItem;
import dev.sapphic.smarthud.item.cache.InventoryCache;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(value = Side.CLIENT, modid = SmartHud.MOD_ID)
public final class SlotRenderer {
  private static final ResourceLocation SLOTS_TEXTURE_SHEET =
      new ResourceLocation(SmartHud.MOD_ID, "textures/hud/elements.png");

  private SlotRenderer() {}

  @SubscribeEvent
  public static void draw(final RenderGameOverlayEvent.Pre event) {
    if (!SlotsConfig.enabled || (event.getType() != ElementType.HOTBAR)) {
      return;
    }

    final Minecraft minecraft = Minecraft.getMinecraft();
    final boolean left = minecraft.player.getPrimaryHand() == EnumHandSide.LEFT;
    final ImmutableCollection<SlotItem> cache = InventoryCache.get();
    final int slots = Math.max(1, Math.min(SlotsConfig.size, cache.size()));
    final ScaledResolution resolution = event.getResolution();
    final int height = resolution.getScaledHeight();
    final int center = resolution.getScaledWidth() / 2;
    final int origin = 98;

    if (SlotsConfig.style.isTextured() && (!cache.isEmpty() || SlotsConfig.drawEmpty)) {
      final int x = center + (left ? (-origin - ((44 + (20 * (slots - 2))) - 2)) : origin);
      final int v = SlotsConfig.style.getTextureV();

      minecraft.getTextureManager().bindTexture(SLOTS_TEXTURE_SHEET);
      minecraft.ingameGUI.drawTexturedModalRect(x, height - 22, 0, v, 11, 22);

      for (int i = 0; i < ((slots - 1) * 2); ++i) {
        final int slotX = x + 11 + (10 * i);
        final int slotU = ((i % 2) == 0) ? 32 : 22;

        minecraft.ingameGUI.drawTexturedModalRect(slotX, height - 22, slotU, v, 10, 22);
      }

      minecraft.ingameGUI.drawTexturedModalRect((x + (20 * slots)) - 9, height - 22, 11, v, 11, 22);
    }

    final UnmodifiableListIterator<SlotItem> it =
        cache.asList().listIterator(left ? cache.size() : 0);

    for (int slot = 0; (left ? it.hasPrevious() : it.hasNext()) && (slot < slots); ++slot) {
      final SlotItem item = left ? it.previous() : it.next();
      final ItemStack stack = item.stack();
      final int stackOffset = origin + 3 + (20 * slot);
      final int stackX = left ? (center - stackOffset - 16) : (center + stackOffset);
      final int stackY = height - (16 + 3);

      RenderHelper.enableGUIStandardItemLighting();
      minecraft.getRenderItem().renderItemAndEffectIntoGUI(stack, stackX, stackY);

      if (!stack.isStackable() && SlotsConfig.drawOverlays) {
        minecraft.getRenderItem().renderItemOverlays(minecraft.fontRenderer, stack, stackX, stackY);
      }

      RenderHelper.disableStandardItemLighting();

      if ((item.count() > 1) && SlotsConfig.drawCounts) {
        final String label = item.abbreviatedCount();
        final int width = minecraft.fontRenderer.getStringWidth(label);
        final int labelOrigin = origin + (20 - width) + (20 * slot);
        final int labelX = left ? ((-labelOrigin - width) + (18 - width)) : labelOrigin;
        final int labelY = height - minecraft.fontRenderer.FONT_HEIGHT - 1;

        GlStateManager.disableDepth();
        minecraft.fontRenderer.drawString(label, center + labelX, labelY, 0xFFFFFFFF, true);
      }
    }

    if (minecraft.gameSettings.attackIndicator == 2) {
      minecraft.gameSettings.attackIndicator = Integer.MIN_VALUE;
    }
  }

  @SubscribeEvent
  public static void draw(final RenderGameOverlayEvent.Post event) {
    if (!SlotsConfig.enabled || (event.getType() != ElementType.HOTBAR)) {
      return;
    }

    final Minecraft minecraft = Minecraft.getMinecraft();

    if (minecraft.gameSettings.attackIndicator == Integer.MIN_VALUE) {
      if (minecraft.getRenderViewEntity() instanceof EntityPlayer) {
        final EntityPlayer player = (EntityPlayer) minecraft.getRenderViewEntity();
        final float strength = player.getCooledAttackStrength(0.0F);

        if (strength < 1.0F) {
          final ScaledResolution resolution = event.getResolution();
          final ImmutableCollection<SlotItem> cache = InventoryCache.get();
          final int offset;

          if (!cache.isEmpty() || SlotsConfig.drawEmpty) {
            offset = 91 + (20 * Math.max(1, Math.min(cache.size(), SlotsConfig.size))) + 9;
          } else {
            offset = 91;
          }

          final int center = resolution.getScaledWidth() / 2;
          final boolean left = player.getPrimaryHand() == EnumHandSide.LEFT;
          final int offsetX = left ? (center - offset - 22) : (center + offset + 6);
          final int offsetY = resolution.getScaledHeight() - 20;
          final int strip = MathHelper.floor(strength * 19.0F);

          GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
          GlStateManager.enableRescaleNormal();
          GlStateManager.enableBlend();
          GlStateManager.tryBlendFuncSeparate(
              SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA,
              SourceFactor.ONE, DestFactor.ZERO);
          RenderHelper.enableGUIStandardItemLighting();
          minecraft.getTextureManager().bindTexture(Gui.ICONS);
          minecraft.ingameGUI.drawTexturedModalRect(offsetX, offsetY, 0, 94, 18, 18);
          minecraft.ingameGUI.drawTexturedModalRect(
              offsetX, (offsetY + 18) - strip, 18, 112 - strip, 18, strip);
          RenderHelper.disableStandardItemLighting();
          GlStateManager.disableRescaleNormal();
          GlStateManager.disableBlend();
        }
      }

      minecraft.gameSettings.attackIndicator = 2;
    }
  }
}
