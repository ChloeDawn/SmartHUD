package net.insomniakitten.smarthud.feature.glance;
 
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

import net.insomniakitten.smarthud.SmartHUDConfig;
import net.insomniakitten.smarthud.compat.baubles.TOPCompat;
import net.insomniakitten.smarthud.feature.ISmartHUDFeature;
import net.insomniakitten.smarthud.util.RenderContext;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public final class GlanceFeature implements ISmartHUDFeature {

    public GlanceFeature() {}

    @Override
    public boolean isEnabled() {
        return SmartHUDConfig.GLANCE.isEnabled;
    }

    @Override
    public RenderGameOverlayEvent.ElementType getType() {
        return RenderGameOverlayEvent.ElementType.TEXT;
    }

    @Override
    public void onRenderTickPre(RenderContext ctx) {
        if (ctx.getPlayer() != null && checkSneaking(ctx.getPlayer()) && checkProbe(ctx.getPlayer())) {
            if (ctx.getPlayerController() != null && ctx.getPlayerController().curBlockDamageMP > 0.0F) {
                float blockDmg = ctx.getPlayerController().curBlockDamageMP;
                NumberFormat format = DecimalFormat.getPercentInstance(Locale.ROOT);
                String perc = I18n.format("msg.smarthud.glance.progress", format.format(blockDmg));
                float x = (ctx.getScreenWidth() / 2) - (ctx.getStringWidth(perc) / 2);
                float y = (ctx.getScreenHeight() / 2) + (ctx.getFontHeight());
                ctx.drawString(perc, x, y);
            }
        }
    }

    private boolean checkSneaking(EntityPlayer player) {
        return !SmartHUDConfig.GLANCE.requireSneaking || player.isSneaking();
    }

    private boolean checkProbe(EntityPlayer player) {
        return !SmartHUDConfig.GLANCE.requireProbe || TOPCompat.hasProbe(player);
    }

}
