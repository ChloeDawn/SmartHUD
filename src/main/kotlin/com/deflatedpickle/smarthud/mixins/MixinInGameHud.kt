/* Copyright (c) 2021 ChloeDawn,DeflatedPickle under the APACHE-2.0 license */

@file:Suppress("unused")

package com.deflatedpickle.smarthud.mixins

import com.deflatedpickle.smarthud.SmartHUDReheated
import com.deflatedpickle.smarthud.SmartHUDReheated.DISTANCE
import net.minecraft.client.gui.DrawableHelper
import net.minecraft.client.gui.hud.InGameHud
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.util.Arm
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.Shadow
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.Inject
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo
import org.spongepowered.asm.mixin.injection.callback.LocalCapture

@Mixin(InGameHud::class)
abstract class MixinInGameHud : DrawableHelper() {
    @Shadow val scaledWidth = 0
    @Shadow val scaledHeight = 0

    @Shadow
    abstract fun renderHotbarItem(
        x: Int,
        y: Int,
        tickDelta: Float,
        playerEntity: PlayerEntity,
        itemStack: ItemStack,
        seed: Int,
    )

    @Inject(
        method = ["renderHotbar"],
        at = [
            At(
                value = "INVOKE",
                shift = At.Shift.AFTER,
                ordinal = 0,
                target = "Lnet/minecraft/client/gui/hud/InGameHud;drawTexture(Lnet/minecraft/client/util/math/MatrixStack;IIIIII)V",
            )
        ],
    )
    fun drawSmartHUDBackground(tickDelta: Float, matrixStack: MatrixStack, info: CallbackInfo) {
        if (SmartHUDReheated.enabled) {
            for (i in 0 until SmartHUDReheated.options.size) {
                this.drawTexture(
                    matrixStack,
                    scaledWidth / 2 + 182 / 2 + DISTANCE + (21 * i),
                    scaledHeight - 22 - 1,
                    24, 22,
                    22, 24,
                )
            }
        }
    }

    @Inject(
        method = ["renderHotbar"],
        at = [
            At(
                value = "INVOKE",
                shift = At.Shift.BEFORE,
                ordinal = 0,
                target = "Lnet/minecraft/client/gui/hud/InGameHud;renderHotbarItem(IIFLnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/ItemStack;I)V",
            )
        ],
        locals = LocalCapture.CAPTURE_FAILEXCEPTION,
    )
    fun drawSmartHUDItems(
        partialTicks: Float,
        matrixStack: MatrixStack,
        info: CallbackInfo,
        playerEntity: PlayerEntity,
        itemStack: ItemStack,
        arm: Arm,
        i: Int,
        j: Int,
        k: Int,
        l: Int,
        seed: Int,
        index: Int,
        x: Int,
        y: Int
    ) {
        if (SmartHUDReheated.enabled) {
            for ((ind, s) in SmartHUDReheated.options.withIndex()) {
                val slot = playerEntity.inventory.getSlotWithStack(s)

                if (slot != -1) {
                    this.renderHotbarItem(
                        scaledWidth / 2 + 182 / 2 + DISTANCE + 3 + (21 * ind), y,
                        partialTicks,
                        playerEntity,
                        playerEntity.inventory.main[slot],
                        seed,
                    )
                }
            }
        }
    }
}
