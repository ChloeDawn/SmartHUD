package net.insomniakitten.smarthud.feature.held; 
 
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
 *   limitations under he License.
 */

import net.insomniakitten.smarthud.util.CalendarHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public class HeldItemRenderer {

    public static void onRenderHeldItemHUD(RenderGameOverlayEvent.Pre event) {
        FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
        EntityPlayer player = Minecraft.getMinecraft().player;
        if (player != null && player.world != null) {
            if (HeldItemManager.isPlayerHoldingItem(player, new ItemStack(Items.CLOCK))) {
                String label = CalendarHelper.getRealTime(player.world);
                fontRenderer.drawStringWithShadow(label, 8, 8, -1);
            }
        }
    }

}