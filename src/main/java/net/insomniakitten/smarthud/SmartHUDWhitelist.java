package net.insomniakitten.smarthud;

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

import net.insomniakitten.smarthud.feature.hotbar.InventoryCache;
import net.insomniakitten.smarthud.util.CachedItem;
import net.insomniakitten.smarthud.util.StackHelper;
import net.insomniakitten.smarthud.util.DimensionPredicate;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.world.DimensionType;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.oredict.OreDictionary;

@Config(modid = SmartHUD.ID, name = "smarthud/whitelist", category = "whitelist")
@Config.LangKey("config.smarthud.whitelist")
public final class SmartHUDWhitelist {

    @Config.Name("Use Whitelist")
    @Config.Comment({ "Should the Hotbar HUD use the configurable whitelist when checking for valid items?",
                      "If false, Smart HUD will fall back to only checking for vanilla clocks and compasses." })
    @Config.LangKey("config.smarthud.whitelist.usewhitelist")
    public static boolean useWhitelist = true;

    @Config.Name("Item List")
    @Config.Comment({ "Configure items that will be displayed on the Hotbar HUD when present in the players inventory.",
                      "Follow the format modid:resourcename:metadata otherwise the item will not be registered.",
                      "Metadata is not required, and not defining it will default the check to any metadata.",
                      "This information can be obtained via Advanced Tooltips (F3+H) in-game." })
    @Config.LangKey("config.smarthud.whitelist.list")
    public static String[] itemList = new String[] {
            "minecraft:clock",
            "minecraft:compass",
            "appliedenergistics2:sky_compass",
            "botania:manaring",
            "botania:manaringgreater",
            "botania:manatablet",
            "endercompass:ender_compass",
            "inventoryneko:neko",
            "mist:hygrometer",
            "naturescompass:naturescompass",
            "quark:slime_bucket",
            "randomthings:goldencompass",
            "toughasnails:season_clock",
            "toughasnails:thermometer"
    };

    public static void initialize() {
        InventoryCache.whitelist = useWhitelist
                                   ? SmartHUDWhitelist.parseWhitelistEntries()
                                   : SmartHUDWhitelist.getDefaultEntries();
    }

    /**
     * Queries the Item registry with a resource location generated from each String in the whitelist String[].
     *
     * @return A list of ItemStacks that match the list of resource names. Any invalid resource names are ignored.
     */
    public static NonNullList<CachedItem> parseWhitelistEntries() {
        SmartHUD.LOGGER.info("Processing whitelist entries");
        NonNullList<CachedItem> cache = NonNullList.create();
        for (String item : itemList) {
            String entry = item.trim();
            DimensionPredicate predicate = DimensionPredicate.ANY;
            String[] contents = entry.split("@");

            if (contents.length > 1) {
                int dim = Integer.valueOf(contents[ 1 ].replaceAll("\\D", ""));
                predicate = DimensionPredicate.of(DimensionType.getById(dim));
            }

            String[] regname = contents[ 0 ].split(":");
            String modid = regname[ 0 ], name = regname[ 1 ];
            int meta = regname.length > 2 ? Integer.valueOf(regname[ 2 ]) : OreDictionary.WILDCARD_VALUE;
            ItemStack stack = StackHelper.getStackFromResourceName(modid, name, meta);

            if (!stack.isEmpty()) {
                CachedItem cachedItem = new CachedItem(stack);
                cachedItem.setDimension(predicate);
                if (meta >= 0) cachedItem.setMetadata(meta);
                cache.add(cachedItem);
            }
        }
        return cache;
    }

    /**
     * The default list of items that the mod will fall back to if useWhitelist is false in the config.
     */
    public static NonNullList<CachedItem> getDefaultEntries() {
        return NonNullList.from(
                new CachedItem(new ItemStack(Items.CLOCK)),
                new CachedItem(new ItemStack(Items.COMPASS))
        );
    }

}