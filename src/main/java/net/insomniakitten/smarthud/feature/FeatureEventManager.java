package net.insomniakitten.smarthud.feature;
 
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

import com.google.common.collect.ImmutableList;
import net.insomniakitten.smarthud.SmartHUD;
import net.insomniakitten.smarthud.feature.hotbar.HotbarFeature;
import net.insomniakitten.smarthud.feature.pickup.PickupFeature;
import net.insomniakitten.smarthud.util.RenderContext;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(modid = SmartHUD.ID, value = Side.CLIENT)
public final class FeatureEventManager {

    private static final ImmutableList<ISmartHUDFeature> FEATURES = ImmutableList.of(
            new HotbarFeature(),
            new PickupFeature()
    );

    private FeatureEventManager() {}

    @SubscribeEvent
    public static void onRenderGameOverlayPre(RenderGameOverlayEvent.Pre event) {
        if (!event.isCanceled()) {
            RenderContext ctx = new RenderContext(FMLClientHandler.instance().getClient(), event);
            for (ISmartHUDFeature feature : FEATURES) {
                if (isFeatureEnabled(feature, event)) {
                    feature.onRenderTickPre(ctx);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onRenderGameOverlayPost(RenderGameOverlayEvent.Post event) {
        if (!event.isCanceled()) {
            RenderContext ctx = new RenderContext(FMLClientHandler.instance().getClient(), event);
            for (ISmartHUDFeature feature : FEATURES) {
                if (isFeatureEnabled(feature, event)) {
                    feature.onRenderTickPost(ctx);
                }
            }
        }
    }

    public static boolean isFeatureEnabled(ISmartHUDFeature feature, RenderGameOverlayEvent event) {
        return feature.isEnabled() && event.getType() == feature.getType();
    }

}
