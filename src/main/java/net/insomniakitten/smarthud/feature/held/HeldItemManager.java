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
 *   limitations under the License.
 */

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

import static net.insomniakitten.smarthud.config.GeneralConfig.configHeld;

public class HeldItemManager {

    public static boolean canRender(RenderGameOverlayEvent event) {
        return !event.isCanceled() && event.getType().equals(ElementType.TEXT) && configHeld.isEnabled;
    }

    protected static boolean isPlayerHoldingItem(EntityPlayer player, ItemStack stack) {
        boolean main = player.getHeldItemMainhand().isItemEqualIgnoreDurability(stack);
        boolean off = player.getHeldItemOffhand().isItemEqualIgnoreDurability(stack);
        return main || off;
    }

}
