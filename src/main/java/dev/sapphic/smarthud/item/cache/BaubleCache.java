package dev.sapphic.smarthud.item.cache;

import baubles.api.cap.IBaublesItemHandler;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import dev.sapphic.smarthud.SmartHud;
import dev.sapphic.smarthud.item.SlotItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.items.IItemHandler;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

@Mod.EventBusSubscriber(value = Side.CLIENT, modid = SmartHud.MOD_ID)
public final class BaubleCache {
  @CapabilityInject(IBaublesItemHandler.class)
  private static @Nullable Capability<IItemHandler> baublesCapability;

  private static ImmutableCollection<SlotItem> cache = ImmutableList.of();

  private BaubleCache() {}

  public static boolean exists() {
    return baublesCapability != null;
  }

  public static ImmutableCollection<SlotItem> get() {
    return cache;
  }

  @SubscribeEvent
  public static void tick(final TickEvent.PlayerTickEvent event) {
    if ((baublesCapability == null) || (event.side == Side.SERVER)) {
      return;
    }

    final IItemHandler baubles = getBaubles(event.player);
    final Collection<SlotItem> cache = new ArrayList<>(0);

    for (int slot = 0; slot < baubles.getSlots(); ++slot) {
      SlotItem.account(cache, baubles.getStackInSlot(slot), event.player.dimension);
    }

    BaubleCache.cache = ImmutableList.copyOf(cache);
  }

  private static IItemHandler getBaubles(final EntityPlayer player) {
    //noinspection ConstantConditions What the hell, IDEA?
    return Objects.requireNonNull(player.getCapability(baublesCapability, null));
  }
}
