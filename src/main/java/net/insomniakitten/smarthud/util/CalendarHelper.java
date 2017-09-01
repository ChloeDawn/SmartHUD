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

import net.minecraft.world.World;

import static net.insomniakitten.smarthud.config.GeneralConfig.configHeld;

public class CalendarHelper {

    public static String getRealTime(World world) {
        int hour = (int) (6 + (world.getWorldTime() / 1000)) % 24;
        int minute = (int) ((world.getWorldTime() % 1000) * 0.06);
        if (configHeld.is24Hour) {
            return String.format("%02d:%02d", hour, minute);
        } else {
            hour = hour % 12 != 0 ? hour % 12 : 12;
            return String.format("%d:%02d %s", hour, minute, hour < 12 ? "AM" : "PM");
        }
    }

    public static String getRealDate(World world) {
        // FIXME Actually do this
        return "";
    }

}
