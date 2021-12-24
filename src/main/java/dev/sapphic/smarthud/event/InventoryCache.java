package dev.sapphic.smarthud.event;

import com.google.common.collect.ImmutableList;
import dev.sapphic.smarthud.config.ModulesConfig;
import dev.sapphic.smarthud.config.WhitelistParser;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import dev.sapphic.smarthud.SmartHUD;
import dev.sapphic.smarthud.compat.BaublesIntegration;
import dev.sapphic.smarthud.util.CachedItem;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = SmartHUD.ID, value = Side.CLIENT)
public final class InventoryCache {

    private static List<CachedItem> inventory = new ArrayList<>();

    private InventoryCache() {}

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        boolean merge = ModulesConfig.HOTBAR_HUD.mergeDuplicates;
        int dim = event.player.dimension;
        List<ItemStack> inv = event.player.inventory.mainInventory;
        List<CachedItem> inventoryCache = new ArrayList<>();
        for (int slot = 9; slot < 36; ++slot) {
            ItemStack stack = inv.get(slot).copy();
            if (!stack.isEmpty() && WhitelistParser.isWhitelisted(stack, dim)) {
                CachedItem.tryCache(inventoryCache, stack, merge);
            }
        }
        List<CachedItem> baubles = BaublesIntegration.getBaubles();
        if (!baubles.isEmpty()) inventoryCache.addAll(baubles);
        inventory = inventoryCache;
    }

    public static ImmutableList<CachedItem> getInventory() {
        return ImmutableList.copyOf(inventory);
    }

}
