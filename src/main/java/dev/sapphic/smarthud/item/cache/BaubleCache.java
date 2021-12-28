package dev.sapphic.smarthud.item.cache;

import baubles.api.cap.IBaublesItemHandler;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import dev.sapphic.smarthud.SmartHud;
import dev.sapphic.smarthud.item.SlotItem;
import net.minecraft.client.Minecraft;
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
  public static void tick(final TickEvent.ClientTickEvent event) {
    if ((baublesCapability == null) || (event.phase != TickEvent.Phase.END)) {
      return;
    }

    final @Nullable EntityPlayer player = Minecraft.getMinecraft().player;

    if (player == null) {
      return;
    }

    final IItemHandler baubles = getBaubles(player);
    final Collection<SlotItem> cache = new ArrayList<>(0);

    for (int slot = 0; slot < baubles.getSlots(); ++slot) {
      SlotItem.account(cache, baubles.getStackInSlot(slot), player.dimension);
    }

    BaubleCache.cache = ImmutableList.copyOf(cache);
  }

  private static IItemHandler getBaubles(final EntityPlayer player) {
    //noinspection ConstantConditions What the hell, IDEA?
    return Objects.requireNonNull(player.getCapability(baublesCapability, null));
  }
}
