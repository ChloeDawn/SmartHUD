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
import net.minecraft.util.EnumHandSide;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class HandHelper {

    private HandHelper() {}

    public static EnumHandSide getMainHand() {
        return Minecraft.getMinecraft().gameSettings.mainHand;
    }

    public static boolean isLeftHanded() {
        return getMainHand() == EnumHandSide.LEFT;
    }

    /**
     * Used to automatically adjust element offset on the screen depending on
     * the current game setting for the player's main hand. This aids in supporting
     * left-handed mode, and avoiding conflicts with the vanilla HUD elements.
     * @param currentOffset The current offset of the HUD element (averaged from the screen center)
     * @param objectWidth   The current width of the element, used when inverting the position to the negative
     * @return The new offset depending on the current game setting
     */
    public static float handleVariableOffset(float currentOffset, float objectWidth) {
        float newOffset = 0.0F;
        if (isLeftHanded()) {
            currentOffset = -currentOffset;
            newOffset = -objectWidth;
        }
        return currentOffset + newOffset;
    }

}
