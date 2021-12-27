package dev.sapphic.smarthud.item.cache;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import dev.sapphic.smarthud.SmartHud;
import dev.sapphic.smarthud.item.SlotItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.items.IItemHandler;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

@Mod.EventBusSubscriber(value = Side.CLIENT, modid = SmartHud.MOD_ID)
public final class QuarkBackpackCache {
  @CapabilityInject(IItemHandler.class)
  private static @MonotonicNonNull Capability<IItemHandler> itemCapability;

  private static ImmutableCollection<SlotItem> cache = ImmutableList.of();

  @ObjectHolder("quark:backpack")
  private static @Nullable Item backpack;

  private QuarkBackpackCache() {}

  public static boolean exists() {
    return backpack != null;
  }

  public static ImmutableCollection<SlotItem> get() {
    return cache;
  }

  @SubscribeEvent
  public static void tick(final TickEvent.PlayerTickEvent event) {
    if ((backpack == null) || (event.side == Side.SERVER)) {
      return;
    }

    final ItemStack backpack = getBackpack(event.player);

    if (backpack.getItem() != QuarkBackpackCache.backpack) {
      cache = ImmutableList.of();
      return;
    }

    final IItemHandler handler = getContents(backpack);
    final Collection<SlotItem> cache = new ArrayList<>(0);

    for (int slot = 0; slot < handler.getSlots(); ++slot) {
      SlotItem.account(cache, handler.getStackInSlot(slot), event.player.dimension);
    }

    QuarkBackpackCache.cache = ImmutableList.copyOf(cache);
  }

  private static ItemStack getBackpack(final EntityPlayer player) {
    return player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
  }

  private static IItemHandler getContents(final ItemStack stack) {
    //noinspection ConstantConditions What the hell, IDEA?
    return Objects.requireNonNull(stack.getCapability(itemCapability, null));
  }
}
