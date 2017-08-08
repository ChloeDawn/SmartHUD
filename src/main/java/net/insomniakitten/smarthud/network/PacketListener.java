package net.insomniakitten.smarthud.network;

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

import com.mojang.authlib.GameProfile;
import net.insomniakitten.smarthud.SmartHUD;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(Side.CLIENT)
public class PacketListener {

    @SubscribeEvent
    public static void onClientConnectedToServer(FMLNetworkEvent.ClientConnectedToServerEvent event) {
        if (event.getHandler() instanceof NetHandlerPlayClient) {
            SmartHUD.LOGGER.info("Injecting SPacketCollectItem listener into {}",
                    event.getHandler().getClass().getCanonicalName());
            NetHandlerPlayClient handler = (NetHandlerPlayClient) event.getHandler();
            GuiScreen screen = getValue(handler, "field_147307_j", "guiScreenServer");
            GameProfile profile = getValue(handler, "field_175107_d", "profile");
            ExtendedClientNetHandler extendedHandler = new ExtendedClientNetHandler(
                    Minecraft.getMinecraft(), screen, event.getManager(), profile);
            event.getManager().setNetHandler(extendedHandler);
        }
    }

    private static <T> T getValue(NetHandlerPlayClient handler, String... names) {
        return ReflectionHelper.getPrivateValue(NetHandlerPlayClient.class, handler, names);
    }

}
