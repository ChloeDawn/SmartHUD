package dev.sapphic.smarthud.item.cache;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import dev.sapphic.smarthud.SmartHud;
import dev.sapphic.smarthud.item.SlotItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.items.IItemHandler;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

@Mod.EventBusSubscriber(value = Side.CLIENT, modid = SmartHud.MOD_ID)
public final class InventoryCache {
  @CapabilityInject(IItemHandler.class)
  private static @MonotonicNonNull Capability<IItemHandler> itemCapability;

  private static ImmutableCollection<SlotItem> cache = ImmutableList.of();

  private InventoryCache() {}

  public static ImmutableCollection<SlotItem> get() {
    return cache;
  }

  @SubscribeEvent(priority = EventPriority.LOWEST)
  public static void tick(final TickEvent.PlayerTickEvent event) {
    if (event.side == Side.SERVER) {
      return;
    }

    final IItemHandler handler = getInventory(event.player);
    final Collection<SlotItem> cache = new ArrayList<>(0);

    for (int slot = InventoryPlayer.getHotbarSize(); slot < handler.getSlots(); ++slot) {
      SlotItem.account(cache, handler.getStackInSlot(slot), event.player.dimension);
    }

    if (BaubleCache.exists()) {
      cache.addAll(BaubleCache.get());
    }

    if (QuarkBackpackCache.exists()) {
      cache.addAll(QuarkBackpackCache.get());
    }

    InventoryCache.cache = ImmutableList.copyOf(cache);
  }

  private static IItemHandler getInventory(final EntityPlayer player) {
    //noinspection ConstantConditions What the hell, IDEA?
    return Objects.requireNonNull(player.getCapability(itemCapability, EnumFacing.UP));
  }
}
