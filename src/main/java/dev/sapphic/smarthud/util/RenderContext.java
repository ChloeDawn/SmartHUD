package dev.sapphic.smarthud.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class RenderContext {

    private final Minecraft minecraft;

    private final int screenWidth;
    private final int screenHeight;

    private final float partialTicks;

    public RenderContext(Minecraft mc, RenderGameOverlayEvent event) {
        minecraft = mc;
        screenWidth = event.getResolution().getScaledWidth();
        screenHeight = event.getResolution().getScaledHeight();
        partialTicks = event.getPartialTicks();
    }

    public GameSettings getGameSettings() {
        return minecraft.gameSettings;
    }

    public EntityPlayer getPlayer() {
        return minecraft.player;
    }

    public Entity getRenderViewEntity() {
        return minecraft.getRenderViewEntity();
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public int getScreenHeight() {
        return screenHeight;
    }

    public float getPartialTicks() {
        return partialTicks;
    }

    public int getStringWidth(String text) {
        return minecraft.fontRenderer.getStringWidth(text);
    }

    public int getFontHeight() {
        return minecraft.fontRenderer.FONT_HEIGHT;
    }

    public void bindTexture(ResourceLocation texture) {
        minecraft.getTextureManager().bindTexture(texture);
    }

    public void drawTexturedModalRect(int x, int y, int textureX, int textureY, int width, int height) {
        minecraft.ingameGUI.drawTexturedModalRect(x, y, textureX, textureY, width, height);
    }

    public void drawString(String text, float x, float y, int color) {
        minecraft.fontRenderer.drawString(text, x, y, color, true);
    }

    public void drawString(String text, float x, float y) {
        minecraft.fontRenderer.drawString(text, x, y, 0xFFFFFFFF, true);
    }

    public void renderItem(ItemStack stack, int x, int y, boolean includeEffect) {
        if (includeEffect) {
            minecraft.getRenderItem().renderItemAndEffectIntoGUI(stack, x, y);
        } else minecraft.getRenderItem().renderItemIntoGUI(stack, x, y);
    }

    public void renderItemOverlays(ItemStack stack, int x, int y) {
        minecraft.getRenderItem().renderItemOverlays(minecraft.fontRenderer, stack, x, y);
    }

}
