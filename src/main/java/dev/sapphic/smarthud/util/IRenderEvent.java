package dev.sapphic.smarthud.util;

import net.minecraftforge.client.event.RenderGameOverlayEvent;

public interface IRenderEvent {

    boolean canRender();

    RenderGameOverlayEvent.ElementType getType();

    default void onRenderTickPre(RenderContext ctx) {}

    default void onRenderTickPost(RenderContext ctx) {}

}
