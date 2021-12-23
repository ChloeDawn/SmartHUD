package dev.sapphic.smarthud.config;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import dev.sapphic.smarthud.SmartHUD;
import dev.sapphic.smarthud.util.CachedItem;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Mod.EventBusSubscriber(modid = SmartHUD.ID, value = Side.CLIENT)
public final class WhitelistParser {

    private static final List<CachedItem> WHITELIST = new ArrayList<>();

    private WhitelistParser() {}

    public static ImmutableList<CachedItem> getWhitelist() {
        return ImmutableList.copyOf(WHITELIST);
    }

    @SubscribeEvent
    protected static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (SmartHUD.ID.equals(event.getModID())) {
            reloadWhitelistEntries();
        }
    }

    public static void reloadWhitelistEntries() {
        if (!GeneralConfig.WHITELIST.isEnabled) {
            WHITELIST.clear();
            WHITELIST.add(new CachedItem(new ItemStack(Items.CLOCK)));
            WHITELIST.add(new CachedItem(new ItemStack(Items.COMPASS)));
            return;
        }

        Stopwatch stopwatch = Stopwatch.createStarted();
        List<String> missingEntries = new ArrayList<>();
        JsonElement file;

        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(getOrGenerateJson()), StandardCharsets.UTF_8)) {
            file = new JsonParser().parse(reader);
        } catch (IOException e) {
            SmartHUD.LOGGER.warn("Failed to parse whitelist config! Please report this to the mod author.");
            e.printStackTrace();
            return;
        }

        WHITELIST.clear();

        JsonArray entries;

        try {
            entries = file.getAsJsonArray();
        } catch (IllegalStateException e) {
            SmartHUD.LOGGER.warn("Received invalid data from the whitelist, please check your formatting!");
            entries = new JsonArray();
        }

        for (int i = 0; i < entries.size(); ++i) {
            JsonObject json = entries.get(i).getAsJsonObject();

            if (json.isJsonNull() || !json.has("item")) {
                String msg = "Whitelist entry at index {} is missing required value \"item\"";
                SmartHUD.LOGGER.warn(msg, i);
                continue;
            }

            ResourceLocation id = new ResourceLocation(json.get("item").getAsString());
            Item item = Item.REGISTRY.getObject(id);

            if (item == null) {
                if (Loader.isModLoaded(id.getNamespace())) {
                    String msg = "Unable to find item for whitelist entry at index {} by name <{}>";
                    SmartHUD.LOGGER.warn(msg, i, id);
                } else if (!missingEntries.contains(id.getNamespace())) {
                    missingEntries.add(id.toString());
                }
                continue;
            }

            CachedItem cachedItem = new CachedItem(new ItemStack(item));

            if (json.has("meta")) {
                int meta = json.get("meta").getAsInt();
                if (meta < 0 || meta > Short.MAX_VALUE) {
                    String msg = "Invalid metadata <{}> found in whitelist entry at index {}";
                    SmartHUD.LOGGER.warn(msg, meta, i);
                } else cachedItem.setMetadata(meta);
            }

            if (json.has("ignore_nbt")) {
                boolean ignoreNBT = json.get("ignore_nbt").getAsBoolean();
                cachedItem.setIgnoreNBT(ignoreNBT);
            }

            if (json.has("ignore_dmg")) {
                boolean ignoreDmg = json.get("ignore_dmg").getAsBoolean();
                cachedItem.setIgnoreDmg(ignoreDmg);
            }

            if (json.has("dimensions")) {
                JsonArray array = json.get("dimensions").getAsJsonArray();
                if (array.size() == 1) {
                    int dim = array.get(0).getAsInt();
                    if (testDimension(dim, i)) {
                        cachedItem.setDimensionPredicate(d -> d == dim);
                    } else cachedItem.setDimensionPredicate(d -> false);
                } else {
                    final int index = i;
                    IntOpenHashSet dimensions = Stream.of(array)
                            .map(JsonArray::getAsInt)
                            .filter(dim -> WhitelistParser.testDimension(dim, index))
                            .collect(Collectors.toCollection(IntOpenHashSet::new));
                    cachedItem.setDimensionPredicate(dimensions::contains);
                }
            }

            if (!WHITELIST.contains(cachedItem)) {
                WHITELIST.add(cachedItem);
            }
        }

        String msg = "Finished processing whitelist config in {}ms";
        long time = stopwatch.stop().elapsed(TimeUnit.MILLISECONDS);
        SmartHUD.LOGGER.info(msg, time);

        if (!missingEntries.isEmpty() && GeneralConfig.WHITELIST.logMissingEntries) {
            SmartHUD.LOGGER.warn("Entries were skipped as the following items could not be found:");
            for (String entry : missingEntries) {
                SmartHUD.LOGGER.warn("-> {}", entry);
            }
        }
    }

    private static boolean testDimension(int dim, int index) {
        if (DimensionManager.isDimensionRegistered(dim)) return true;
        SmartHUD.LOGGER.warn("Unregistered or invalid dimension {} found in whitelist entry at index {}", dim, index);
        return false;
    }

    private static File getOrGenerateJson() {
        String path = "/assets/" + SmartHUD.ID + "/data/whitelist.json";
        File defaultWhitelist = new File(SmartHUD.getConfigPath(), "defaults.json");
        File userWhitelist = new File(SmartHUD.getConfigPath(), "whitelist.json");
        writeToFile(path, defaultWhitelist, true);
        if (!userWhitelist.exists()) {
            writeToFile(path, userWhitelist, false);
        }
        return userWhitelist;
    }

    private static void writeToFile(String path, File file, boolean overwrite) {
        try (InputStream stream = SmartHUD.class.getResourceAsStream(path)) {
            if (overwrite) {
                Files.copy(stream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
            } else Files.copy(stream, file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
