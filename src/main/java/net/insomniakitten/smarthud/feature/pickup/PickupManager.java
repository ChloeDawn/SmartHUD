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

import com.google.common.base.Throwables;
import com.google.common.collect.EvictingQueue;
import com.google.common.collect.ForwardingQueue;
import net.insomniakitten.smarthud.config.GeneralConfig;
import net.insomniakitten.smarthud.util.CachedItem;
import net.insomniakitten.smarthud.util.Profiler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleItemPickup;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.util.Queue;
import javax.annotation.Nullable;

import static net.insomniakitten.smarthud.config.GeneralConfig.configPickup;

public class PickupManager {

    protected static EvictingQueue<CachedItem> items = EvictingQueue.create(configPickup.itemLimit);

    public static void initialize() {
        regeneratePickupCache();
        initializeParticleQueue();
    }

    public static void regeneratePickupCache() {
        EvictingQueue<CachedItem> newQueue = EvictingQueue.create(configPickup.itemLimit);
        newQueue.addAll(items);
        items = newQueue;
    }

    protected static boolean canRender(RenderGameOverlayEvent.Pre event) {
        return !event.isCanceled() && event.getType().equals(ElementType.CHAT) && configPickup.isEnabled;
    }

    public static int getDisplayTimeTicks() {
        return GeneralConfig.configPickup.displayTime / 50;
    }

    private static void handleItemCollection(ItemStack stack) {
        Profiler.start(Profiler.Section.HANDLE_COLLECTION);

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

    private static void initializeParticleQueue() {
        try {
            Field queueEntityFXField = ReflectionHelper.findField(ParticleManager.class, "field_187241_h", "queueEntityFX");
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            MethodHandle itemGetter = getParticleItemPickupGetter(lookup, "field_174840_a", "item");
            MethodHandle targetGetter = getParticleItemPickupGetter(lookup, "field_174843_ax", "target");
            ParticleManager particleMgr = Minecraft.getMinecraft().effectRenderer;
            @SuppressWarnings("unchecked")
            Queue<Particle> queueEntityFX = (Queue<Particle>) queueEntityFXField.get(particleMgr);
            queueEntityFXField.set(particleMgr, createForwardingParticleQueue(queueEntityFX, itemGetter, targetGetter));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static MethodHandle getParticleItemPickupGetter(MethodHandles.Lookup lookup, String... fieldNames) throws IllegalAccessException {
        return lookup.unreflectGetter(ReflectionHelper.findField(ParticleItemPickup.class, fieldNames));
    }

    private static Queue<Particle> createForwardingParticleQueue(Queue<Particle> delegate, MethodHandle itemGetter, MethodHandle targetGetter) {
        return new ForwardingQueue<Particle>() {
            @Override
            protected Queue<Particle> delegate() {
                return delegate;
            }

            @Override
            public boolean add(@Nullable Particle element) {
                if (super.add(element)) {
                    if (element != null && element.getClass() == ParticleItemPickup.class) {
                        Entity item, target;
                        try {
                            item = (Entity) itemGetter.invoke(element);
                            target = (Entity) targetGetter.invoke(element);
                        } catch (Throwable e) {
                            Throwables.throwIfUnchecked(e);
                            throw new RuntimeException(e);
                        }
                        if (item instanceof EntityItem && target instanceof EntityPlayerSP) {
                            handleItemCollection(((EntityItem) item).getItem());
                        }
                    }
                    return true;
                }
                return false;
            }
        };
    }

}
