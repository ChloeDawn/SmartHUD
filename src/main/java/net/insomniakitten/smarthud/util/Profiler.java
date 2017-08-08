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

public class Profiler {

    private static net.minecraft.profiler.Profiler profiler = Minecraft.getMinecraft().mcProfiler;

    @SideOnly(Side.CLIENT)
    public static void start(Section section) {
        profiler.startSection(section.get());
    }

    @SideOnly(Side.CLIENT)
    public static void end() {
        profiler.endSection();
    }

    @SideOnly(Side.CLIENT)
    public static boolean isEnabled() {
        return profiler.profilingEnabled;
    }

    public enum Section {
        RENDER_HOTBAR("smarthud.hotbar.render_overlay"),
        CACHE_INVENTORY("smarthud.hotbar.cache_inventory"),
        RENDER_PICKUP("smarthud.pickup.render_overlay"),
        HANDLE_NETWORK_PACKET("smarthud.pickup.handle_network_packet"),
        ;

        private final String msg;
        Section(String msg) { this.msg = msg; }
        public String get() { return msg; }
    }

}