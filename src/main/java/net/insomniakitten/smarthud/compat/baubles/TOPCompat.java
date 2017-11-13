package net.insomniakitten.smarthud.compat.baubles; 
 
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

import com.google.common.collect.Lists;
import net.insomniakitten.smarthud.SmartHUD;
import net.insomniakitten.smarthud.util.ModProfiler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

import java.util.List;

@Mod.EventBusSubscriber(modid = SmartHUD.ID, value = Side.CLIENT)
@GameRegistry.ObjectHolder(TOPCompat.MOD_ID)
public final class TOPCompat {

    protected static final String MOD_ID = "theoneprobe";

    public static final Item PROBE = Items.AIR;
    public static final Item CREATIVEPROBE = Items.AIR;

    public static final Item DIAMOND_HElMET_PROBE = Items.AIR;
    public static final Item GOLD_HElMET_PROBE = Items.AIR;
    public static final Item IRON_HElMET_PROBE = Items.AIR;

    private TOPCompat() {}

    public static boolean hasProbe(EntityPlayer player) {
        ModProfiler.start(ModProfiler.Section.LOOKUP_TOP);
        List<Item> probeItems = Lists.newArrayList(PROBE, CREATIVEPROBE, DIAMOND_HElMET_PROBE, GOLD_HElMET_PROBE, IRON_HElMET_PROBE);
        for (ItemStack equipment : player.getEquipmentAndArmor()) {
            if (!equipment.isEmpty() && probeItems.contains(equipment.getItem())) {
                ModProfiler.end();
                return true;
            }
        }
        ModProfiler.end();
        return false;
    }

}
