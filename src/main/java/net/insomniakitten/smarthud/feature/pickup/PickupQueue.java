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
import com.google.common.collect.ForwardingQueue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleItemPickup;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import javax.annotation.Nullable;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.util.Queue;

public class PickupQueue {

    protected static void initializeParticleQueue() {
        try {
            Field field = ReflectionHelper.findField(ParticleManager.class, "field_187241_h", "queueEntityFX");
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            MethodHandle itemGetter = getParticleItemPickupGetter(lookup, "field_174840_a", "item");
            MethodHandle targetGetter = getParticleItemPickupGetter(lookup, "field_174843_ax", "target");
            ParticleManager particleManager = Minecraft.getMinecraft().effectRenderer;
            @SuppressWarnings("unchecked")
            Queue<Particle> newQueue = (Queue<Particle>) field.get(particleManager);
            field.set(particleManager, createForwardingParticleQueue(newQueue, itemGetter, targetGetter));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static MethodHandle getParticleItemPickupGetter(
            MethodHandles.Lookup lookup, String... fieldNames) throws IllegalAccessException {
        return lookup.unreflectGetter(ReflectionHelper.findField(ParticleItemPickup.class, fieldNames));
    }

    private static Queue<Particle> createForwardingParticleQueue(
            Queue<Particle> delegate, MethodHandle itemGetter, MethodHandle targetGetter) {
        return new ForwardingQueue<Particle>() {

            @Override
            protected Queue<Particle> delegate() {
                return delegate;
            }

            @Override
            public boolean add(@Nullable Particle element) {
                if (super.add(element)) {
                    if (element != null && ParticleItemPickup.class.equals(element.getClass())) {
                        Entity item, target;
                        try {
                            item = (Entity) itemGetter.invoke(element);
                            target = (Entity) targetGetter.invoke(element);
                        } catch (Throwable e) {
                            Throwables.throwIfUnchecked(e);
                            throw new RuntimeException(e);
                        }
                        if (item instanceof EntityItem && target instanceof EntityPlayerSP) {
                            PickupManager.handleItemCollection(((EntityItem) item).getItem());
                        }
                    }
                    return true;
                }
                return false;
            }

        };
    }

}
