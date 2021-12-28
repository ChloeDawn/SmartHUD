package dev.sapphic.smarthud.item;

import com.google.common.collect.EvictingQueue;
import com.google.common.collect.ForwardingQueue;
import dev.sapphic.smarthud.config.TickerConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleItemPickup;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;

import javax.annotation.Nullable;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.util.Queue;

import static net.minecraftforge.fml.common.ObfuscationReflectionHelper.findField;

public final class TickerQueue {
  private static final MethodHandle ITEM_FIELD_GETTER;
  private static final MethodHandle TARGET_FIELD_GETTER;

  private static @MonotonicNonNull EvictingQueue<TickerItem> queue;

  static {
    try {
      final Field itemField = findField(ParticleItemPickup.class, "field_174840_a");
      final Field targetField = findField(ParticleItemPickup.class, "field_174843_ax");
      final MethodHandles.Lookup lookup = MethodHandles.lookup();

      ITEM_FIELD_GETTER = lookup.unreflectGetter(itemField);
      TARGET_FIELD_GETTER = lookup.unreflectGetter(targetField);
    } catch (final IllegalAccessException e) {
      throw new IllegalStateException(e);
    }
  }

  private TickerQueue() {}

  public static EvictingQueue<TickerItem> get() {
    return queue;
  }

  public static void initialize() {
    queue = EvictingQueue.create(TickerConfig.size);

    try {
      final ParticleManager particles = Minecraft.getMinecraft().effectRenderer;
      final Field queueField = findField(ParticleManager.class, "field_187241_h");
      @SuppressWarnings("unchecked")
      final Queue<Particle> delegate = (Queue<Particle>) queueField.get(particles);

      queueField.set(particles, forwardingQueue(delegate));
    } catch (final ReflectiveOperationException e) {
      throw new IllegalStateException(e);
    }
  }

  public static void rebuild() {
    final EvictingQueue<TickerItem> queue = EvictingQueue.create(TickerConfig.size);

    queue.addAll(TickerQueue.queue);
    TickerQueue.queue = queue;
  }

  private static Queue<Particle> forwardingQueue(final Queue<Particle> delegate) {
    return new ForwardingQueue<Particle>() {
      @Override
      public boolean add(@Nullable final Particle element) {
        if ((element != null) && !super.add(element)) {
          return false;
        }

        if ((element != null) && (element.getClass() == ParticleItemPickup.class)) {
          final Entity item = getItem((ParticleItemPickup) element);
          final Entity target = getTarget((ParticleItemPickup) element);

          if ((item instanceof EntityItem) && (target instanceof EntityPlayerSP)) {
            queueItem(((EntityItem) item).getItem());
          }
        }

        return true;
      }

      @Override
      protected Queue<Particle> delegate() {
        return delegate;
      }
    };
  }

  private static void queueItem(final ItemStack stack) {
    if (!stack.isEmpty()) {
      boolean unique = true;

      for (final TickerItem item : queue) {
        if (item.stack().isItemEqual(stack)) {
          item.renewTimestamp();
          item.count(stack);

          if (TickerConfig.behavior == 0) {
            queue.remove(item);
            queue.add(item);
          }

          unique = false;
          break;
        }
      }

      if (unique) {
        queue.add(new TickerItem(stack));
      }
    }
  }

  private static Entity getItem(final ParticleItemPickup particle) {
    try {
      return (Entity) ITEM_FIELD_GETTER.invokeExact(particle);
    } catch (final Throwable e) {
      throw new IllegalStateException(e);
    }
  }

  private static Entity getTarget(final ParticleItemPickup particle) {
    try {
      return (Entity) TARGET_FIELD_GETTER.invokeExact(particle);
    } catch (final Throwable e) {
      throw new IllegalStateException(e);
    }
  }
}
