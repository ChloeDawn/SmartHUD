package dev.sapphic.smarthud.event;

import com.google.common.collect.Lists;
import dev.sapphic.smarthud.render.HotbarRender;
import dev.sapphic.smarthud.render.ItemPickupRender;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import dev.sapphic.smarthud.SmartHUD;
import dev.sapphic.smarthud.util.IRenderEvent;
import dev.sapphic.smarthud.util.RenderContext;

import java.util.List;

@Mod.EventBusSubscriber(modid = SmartHUD.ID, value = Side.CLIENT)
public final class RenderManager {

    private static final List<IRenderEvent> RENDER_EVENTS = Lists.newArrayList(
            new HotbarRender(), new ItemPickupRender()
    );

    private RenderManager() {}

    @SubscribeEvent
    protected static void onRenderGameOverlayPre(RenderGameOverlayEvent.Pre event) {
        Minecraft mc = FMLClientHandler.instance().getClient();
        RenderContext ctx = new RenderContext(mc, event);
        for (IRenderEvent render : RENDER_EVENTS) {
            if (canRender(render, event)) {
                render.onRenderTickPre(ctx);
            }
        }
    }

    @SubscribeEvent
    protected static void onRenderGameOverlayPost(RenderGameOverlayEvent.Post event) {
        Minecraft mc = FMLClientHandler.instance().getClient();
        RenderContext ctx = new RenderContext(mc, event);
        for (IRenderEvent render : RENDER_EVENTS) {
            if (canRender(render, event)) {
                render.onRenderTickPost(ctx);
            }
        }
    }

    private static boolean canRender(IRenderEvent render, RenderGameOverlayEvent event) {
        return render.canRender() && render.getType() == event.getType();
    }

}
