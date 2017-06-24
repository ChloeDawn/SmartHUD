package net.insomniakitten.smarthud.asm;

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

import net.insomniakitten.smarthud.event.RenderHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumHandSide;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SuppressWarnings("ALL")
public class SmartHUDHooks {

    @SideOnly(Side.CLIENT)
    public static int transformAttackIndicator(int original) {
        Minecraft mc = Minecraft.getMinecraft();
        EnumHandSide handSide = mc.gameSettings.mainHand;
        int right = original + RenderHandler.getAttackIndicatorOffset();
        int left = original - RenderHandler.getAttackIndicatorOffset();
        return handSide == EnumHandSide.RIGHT ? right : left;
    }

}
