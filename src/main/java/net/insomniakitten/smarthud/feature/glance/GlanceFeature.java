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
import net.insomniakitten.smarthud.compat.baubles.TOPLookup;
import net.insomniakitten.smarthud.feature.ISmartHUDFeature;
import net.insomniakitten.smarthud.util.RenderContext;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockSilverfish;
import net.minecraft.block.BlockStem;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

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
            String name = "";
            if (ctx.getRayTrace().getBlockPos() != null) {
                BlockPos pos = ctx.getRayTrace().getBlockPos();
                ItemStack stack = getBlockStack(ctx.getWorld().getBlockState(pos), pos, ctx);
                if (!stack.isEmpty()) name = stack.getDisplayName();
            } else if (ctx.getRayTrace().entityHit != null) {
                Entity entity = ctx.getRayTrace().entityHit;
                name = entity.getDisplayName().getFormattedText();
            }
            if (!name.isEmpty()) {
                float x = (ctx.getScreenWidth() / 2) - (ctx.getStringWidth(name) / 2);
                float y = (ctx.getScreenHeight() / 2) + (ctx.getFontHeight());
                ctx.drawString(name, x, y);
            }
        }
    }

    private ItemStack getBlockStack(IBlockState state, BlockPos pos, RenderContext ctx) {
        Block block = state.getBlock();
        if (block == Blocks.MONSTER_EGG && SmartHUDConfig.GLANCE.hideSilverfishBlocks) {
            IBlockState actualState = state.getValue(BlockSilverfish.VARIANT).getModelBlock();
            block = actualState.getBlock();
            state = actualState;
        } else if (block instanceof BlockCrops && SmartHUDConfig.GLANCE.showCropOutput) {
            IBlockState crop = ((BlockCrops) block).withAge(((BlockCrops) block).getMaxAge());
            return new ItemStack(block.getItemDropped(crop, ctx.getWorld().rand, 0));
        } else if (block instanceof BlockStem) {
            return new ItemStack(((BlockStem) block).crop);
        }
        return block.getPickBlock(state, ctx.getRayTrace(), ctx.getWorld(), pos, ctx.getPlayer());
    }

    private boolean checkSneaking(EntityPlayer player) {
        return !SmartHUDConfig.GLANCE.requireSneaking || player.isSneaking();
    }

    private boolean checkProbe(EntityPlayer player) {
        return !SmartHUDConfig.GLANCE.requireProbe || TOPLookup.hasProbe(player);
    }

}
