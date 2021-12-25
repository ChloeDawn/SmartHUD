/* Copyright (c) 2021 ChloeDawn,DeflatedPickle under the APACHE-2.0 license */

package com.deflatedpickle.smarthud

import net.fabricmc.api.ClientModInitializer
import net.minecraft.item.ItemStack
import net.minecraft.item.Items

@Suppress("UNUSED")
object SmartHUDReheated : ClientModInitializer {
    const val MOD_ID = "$[id]"
    private const val NAME = "$[name]"
    private const val GROUP = "$[group]"
    private const val AUTHOR = "$[author]"
    private const val VERSION = "$[version]"

    const val DISTANCE = 7

    val options = mutableListOf(
        Items.CLOCK,
        Items.COMPASS,
    ).map { ItemStack(it) }

    var enabled = true

    override fun onInitializeClient() {
        println(listOf(MOD_ID, NAME, GROUP, AUTHOR, VERSION))

        KeyboardHandler.initialize()
    }
}
