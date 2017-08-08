package net.insomniakitten.smarthud.feature.pickup; 
 
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

import com.google.common.collect.EvictingQueue;
import net.insomniakitten.smarthud.util.CachedItem;
import net.insomniakitten.smarthud.util.Profiler;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketCollectItem;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static net.insomniakitten.smarthud.config.GeneralConfig.configPickup;

public class PickupManager {

    protected static EvictingQueue<CachedItem> items = EvictingQueue.create(configPickup.itemLimit);

    public static void regeneratePickupCache() {
        EvictingQueue<CachedItem> newQueue = EvictingQueue.create(configPickup.itemLimit);
        newQueue.addAll(items);
        items = newQueue;
    }

    protected static boolean canRender(RenderGameOverlayEvent.Pre event) {
        return !event.isCanceled() && event.getType().equals(ElementType.CHAT) && configPickup.isEnabled;
    }

    @SideOnly(Side.CLIENT)
    public static void handleItemCollectionPacket(SPacketCollectItem packet) {
        World world = Minecraft.getMinecraft().world;
        if (world == null) return;

        Entity entity = world.getEntityByID(packet.getCollectedItemEntityID());
        EntityLivingBase living = (EntityLivingBase) world.getEntityByID(packet.getEntityID());
        if (entity == null || living == null) return;

        if (entity instanceof EntityItem && living instanceof EntityPlayer) {
            Profiler.start(Profiler.Section.HANDLE_NETWORK_PACKET);

            ItemStack stack = ((EntityItem) entity).getItem();
            stack.setCount(packet.getAmount());

            if (!stack.isEmpty()) {
                EvictingQueue<CachedItem> internal = EvictingQueue.create(configPickup.itemLimit);
                internal.addAll(items);

                if (items.isEmpty()) {
                    internal.add(new CachedItem(stack, stack.getCount()));
                } else {
                    boolean shouldCache = true;
                    for (CachedItem cachedItem : items) {
                        if (cachedItem.matches(stack)) {
                            int count = cachedItem.getCount() + stack.getCount();
                            if (configPickup.priorityMode == 0) {
                                internal.remove(cachedItem);
                                internal.add(new CachedItem(stack, count));
                                shouldCache = false;
                            } else if (configPickup.priorityMode == 1) {
                                cachedItem.setCount(count);
                                cachedItem.renewTimestamp();
                                shouldCache = false;
                            }
                            break;
                        }
                    }
                    if (shouldCache) {
                        internal.add(new CachedItem(stack, stack.getCount()));
                    }
                }
                items = internal;
            }
            Profiler.end();
        }
    }

}
