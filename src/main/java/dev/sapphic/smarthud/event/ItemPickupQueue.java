package dev.sapphic.smarthud.event;

import com.google.common.base.Throwables;
import com.google.common.collect.EvictingQueue;
import com.google.common.collect.ForwardingQueue;
import dev.sapphic.smarthud.config.ModulesConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleItemPickup;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import dev.sapphic.smarthud.SmartHUD;
import dev.sapphic.smarthud.util.CachedItem;

import javax.annotation.Nullable;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.util.Queue;

@Mod.EventBusSubscriber(modid = SmartHUD.ID, value = Side.CLIENT)
public final class ItemPickupQueue {

    private static EvictingQueue<CachedItem> items = EvictingQueue.create(ModulesConfig.ITEM_PICKUP_HUD.itemLimit);

    private static boolean init = false;

    private ItemPickupQueue() {}

    public static void initialize() {
        if (!init) {
            initializeParticleQueue();
            init = true;
        }
        reloadQueue();
    }

    private static void reloadQueue() {
        EvictingQueue<CachedItem> newQueue = EvictingQueue.create(ModulesConfig.ITEM_PICKUP_HUD.itemLimit);
        newQueue.addAll(items);
        items = newQueue;
    }

    @SubscribeEvent
    protected static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (SmartHUD.ID.equals(event.getModID())) {
            reloadQueue();
        }
    }

    public static EvictingQueue<CachedItem> getItems() {
        return items;
    }

    private static void initializeParticleQueue() {
        try {
            Field field = ReflectionHelper.findField(ParticleManager.class, "field_187241_h", "queue");
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            MethodHandle itemGetter = getParticleItemPickupGetter(lookup, "field_174840_a", "item");
            MethodHandle targetGetter = getParticleItemPickupGetter(lookup, "field_174843_ax", "target");
            ParticleManager particleManager = Minecraft.getMinecraft().effectRenderer;
            Queue<Particle> newQueue = (Queue<Particle>) field.get(particleManager);
            field.set(particleManager, createForwardingParticleQueue(newQueue, itemGetter, targetGetter));
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
                if (!super.add(element)) return false;
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
                        handleItemCollection(((EntityItem) item).getItem());
                    }
                }
                return true;
            }
        };
    }

    private static void handleItemCollection(ItemStack stack) {
        if (!stack.isEmpty()) {
            EvictingQueue<CachedItem> newItems = EvictingQueue.create(ModulesConfig.ITEM_PICKUP_HUD.itemLimit);
            newItems.addAll(items);
            if (!items.isEmpty()) {
                boolean shouldCache = true;
                for (CachedItem cachedItem : items) {
                    if (cachedItem.matchesStack(stack, true)) {
                        int count = cachedItem.getCount() + stack.getCount();
                        if (ModulesConfig.ITEM_PICKUP_HUD.priorityMode == 0) {
                            newItems.remove(cachedItem);
                            newItems.add(new CachedItem(stack, count));
                            shouldCache = false;
                        } else if (ModulesConfig.ITEM_PICKUP_HUD.priorityMode == 1) {
                            cachedItem.setCount(count);
                            cachedItem.renewTimestamp();
                            shouldCache = false;
                        }
                        break;
                    }
                }
                if (shouldCache) {
                    newItems.add(new CachedItem(stack, stack.getCount()));
                }
            } else {
                newItems.add(new CachedItem(stack, stack.getCount()));
            }
            items = newItems;
        }
    }

}
