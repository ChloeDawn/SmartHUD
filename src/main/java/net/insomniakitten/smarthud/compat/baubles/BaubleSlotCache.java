package net.insomniakitten.smarthud.compat.baubles; 
 
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

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import net.insomniakitten.smarthud.SmartHUD;
import net.insomniakitten.smarthud.feature.hotbar.InventoryCache;
import net.insomniakitten.smarthud.util.CachedItem;
import net.insomniakitten.smarthud.util.ModProfiler;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(modid = SmartHUD.ID, value = Side.CLIENT)
public final class BaubleSlotCache {

    private static NonNullList<CachedItem> baubles = NonNullList.create();

    private BaubleSlotCache() {}

    public static NonNullList<CachedItem> getBaubles() {
        return baubles;
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player == null || mc.player.world == null) return;
        ModProfiler.start(ModProfiler.Section.CACHE_BAUBLES);
        IBaublesItemHandler handler = BaublesApi.getBaublesHandler(mc.player);
        NonNullList<CachedItem> cache = NonNullList.create();
        for (int slot = 0; slot < handler.getSlots(); ++slot) {
            ItemStack bauble = handler.getStackInSlot(slot);
            InventoryCache.processItemStack(cache, bauble);
        }
        baubles = cache;
        ModProfiler.end();
    }

}
