package net.insomniakitten.smarthud.util; 
 
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

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class ModProfiler {

    private ModProfiler() {}

    public static void start(Section section) {
        Minecraft.getMinecraft().mcProfiler.startSection(section.get());
    }

    public static void end() {
        Minecraft.getMinecraft().mcProfiler.endSection();
    }

    public static boolean isEnabled() {
        return Minecraft.getMinecraft().mcProfiler.profilingEnabled;
    }

    public enum Section {

        RENDER_HOTBAR("smarthud.hotbar.renderOverlay"),
        CACHE_INVENTORY("smarthud.hotbar.cacheInventory"),
        RENDER_PICKUP("smarthud.pickup.renderOverlay"),
        HANDLE_COLLECTION("smarthud.pickup.handleCollection"),;

        private final String msg;

        Section(String msg) {
            this.msg = msg;
        }

        public String get() {
            return msg;
        }

    }

}
