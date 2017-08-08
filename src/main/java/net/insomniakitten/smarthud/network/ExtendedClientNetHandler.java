package net.insomniakitten.smarthud.network;
 
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

import com.mojang.authlib.GameProfile;
import net.insomniakitten.smarthud.feature.pickup.PickupManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.ClientAdvancementManager;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketThreadUtil;
import net.minecraft.network.play.server.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.UUID;

@SideOnly(Side.CLIENT)
public class ExtendedClientNetHandler extends NetHandlerPlayClient {

    private final NetHandlerPlayClient delegate;
    private final Minecraft mc;

    public ExtendedClientNetHandler(
            Minecraft minecraft, GuiScreen screen,
            NetworkManager networkManager, GameProfile profile) {
        super(minecraft, screen, networkManager, profile);
        this.delegate = (NetHandlerPlayClient) networkManager.getNetHandler();
        this.mc = minecraft;
    }

    @Override
    public void handleCollectItem(@Nonnull SPacketCollectItem packet) {
        // This must be called by us otherwise it enqueues the wrapped nethandler
        PacketThreadUtil.checkThreadAndEnqueue(packet, this, mc);
        PickupManager.handleItemCollectionPacket(packet);
        delegate.handleCollectItem(packet);
    }

    @Override
    public void cleanup() {
        delegate.cleanup();
    }

    @Override
    public void handleJoinGame(SPacketJoinGame packet) {
        delegate.handleJoinGame(packet);
    }

    @Override
    public void handleSpawnObject(SPacketSpawnObject packet) {
        delegate.handleSpawnObject(packet);
    }

    @Override
    public void handleSpawnExperienceOrb(SPacketSpawnExperienceOrb packet) {
        delegate.handleSpawnExperienceOrb(packet);
    }

    @Override
    public void handleSpawnGlobalEntity(SPacketSpawnGlobalEntity packet) {
        delegate.handleSpawnGlobalEntity(packet);
    }

    @Override
    public void handleSpawnPainting(SPacketSpawnPainting packet) {
        delegate.handleSpawnPainting(packet);
    }

    @Override
    public void handleEntityVelocity(SPacketEntityVelocity packet) {
        delegate.handleEntityVelocity(packet);
    }

    @Override
    public void handleEntityMetadata(SPacketEntityMetadata packet) {
        delegate.handleEntityMetadata(packet);
    }

    @Override
    public void handleSpawnPlayer(SPacketSpawnPlayer packet) {
        delegate.handleSpawnPlayer(packet);
    }

    @Override
    public void handleEntityTeleport(SPacketEntityTeleport packet) {
        delegate.handleEntityTeleport(packet);
    }

    @Override
    public void handleHeldItemChange(SPacketHeldItemChange packet) {
        delegate.handleHeldItemChange(packet);
    }

    @Override
    public void handleEntityMovement(SPacketEntity packet) {
        delegate.handleEntityMovement(packet);
    }

    @Override
    public void handleEntityHeadLook(SPacketEntityHeadLook packet) {
        delegate.handleEntityHeadLook(packet);
    }

    @Override
    public void handleDestroyEntities(SPacketDestroyEntities packet) {
        delegate.handleDestroyEntities(packet);
    }

    @Override
    public void handlePlayerPosLook(SPacketPlayerPosLook packet) {
        delegate.handlePlayerPosLook(packet);
    }

    @Override
    public void handleMultiBlockChange(SPacketMultiBlockChange packet) {
        delegate.handleMultiBlockChange(packet);
    }

    @Override
    public void handleChunkData(SPacketChunkData packet) {
        delegate.handleChunkData(packet);
    }

    @Override
    public void processChunkUnload(SPacketUnloadChunk packet) {
        delegate.processChunkUnload(packet);
    }

    @Override
    public void handleBlockChange(SPacketBlockChange packet) {
        delegate.handleBlockChange(packet);
    }

    @Override
    public void handleDisconnect(SPacketDisconnect packet) {
        delegate.handleDisconnect(packet);
    }

    @Override
    public void onDisconnect(@Nonnull ITextComponent reason) {
        delegate.onDisconnect(reason);
    }

    @Override
    public void sendPacket(@Nonnull Packet<?> packet) {
        delegate.sendPacket(packet);
    }

    @Override
    public void handleChat(SPacketChat packet) {
        delegate.handleChat(packet);
    }

    @Override
    public void handleAnimation(SPacketAnimation packet) {
        delegate.handleAnimation(packet);
    }

    @Override
    public void handleUseBed(SPacketUseBed packet) {
        delegate.handleUseBed(packet);
    }

    @Override
    public void handleSpawnMob(SPacketSpawnMob packet) {
        delegate.handleSpawnMob(packet);
    }

    @Override
    public void handleTimeUpdate(SPacketTimeUpdate packet) {
        delegate.handleTimeUpdate(packet);
    }

    @Override
    public void handleSpawnPosition(SPacketSpawnPosition packet) {
        delegate.handleSpawnPosition(packet);
    }

    @Override
    public void handleSetPassengers(SPacketSetPassengers packet) {
        delegate.handleSetPassengers(packet);
    }

    @Override
    public void handleEntityAttach(SPacketEntityAttach packet) {
        delegate.handleEntityAttach(packet);
    }

    @Override
    public void handleEntityStatus(SPacketEntityStatus packet) {
        delegate.handleEntityStatus(packet);
    }

    @Override
    public void handleUpdateHealth(SPacketUpdateHealth packet) {
        delegate.handleUpdateHealth(packet);
    }

    @Override
    public void handleSetExperience(SPacketSetExperience packet) {
        delegate.handleSetExperience(packet);
    }

    @Override
    public void handleRespawn(SPacketRespawn packet) {
        delegate.handleRespawn(packet);
    }

    @Override
    public void handleExplosion(SPacketExplosion packet) {
        delegate.handleExplosion(packet);
    }

    @Override
    public void handleOpenWindow(SPacketOpenWindow packet) {
        delegate.handleOpenWindow(packet);
    }

    @Override
    public void handleSetSlot(SPacketSetSlot packet) {
        delegate.handleSetSlot(packet);
    }

    @Override
    public void handleConfirmTransaction(SPacketConfirmTransaction packet) {
        delegate.handleConfirmTransaction(packet);
    }

    @Override
    public void handleWindowItems(SPacketWindowItems packet) {
        delegate.handleWindowItems(packet);
    }

    @Override
    public void handleSignEditorOpen(SPacketSignEditorOpen packet) {
        delegate.handleSignEditorOpen(packet);
    }

    @Override
    public void handleUpdateTileEntity(SPacketUpdateTileEntity packet) {
        delegate.handleUpdateTileEntity(packet);
    }

    @Override
    public void handleWindowProperty(@Nonnull SPacketWindowProperty packet) {
        delegate.handleWindowProperty(packet);
    }

    @Override
    public void handleEntityEquipment(SPacketEntityEquipment packet) {
        delegate.handleEntityEquipment(packet);
    }

    @Override
    public void handleCloseWindow(@Nonnull SPacketCloseWindow packet) {
        delegate.handleCloseWindow(packet);
    }

    @Override
    public void handleBlockAction(SPacketBlockAction packet) {
        delegate.handleBlockAction(packet);
    }

    @Override
    public void handleBlockBreakAnim(SPacketBlockBreakAnim packet) {
        delegate.handleBlockBreakAnim(packet);
    }

    @Override
    public void handleChangeGameState(SPacketChangeGameState packet) {
        delegate.handleChangeGameState(packet);
    }

    @Override
    public void handleMaps(SPacketMaps packet) {
        delegate.handleMaps(packet);
    }

    @Override
    public void handleEffect(SPacketEffect packet) {
        delegate.handleEffect(packet);
    }

    @Override
    public void handleAdvancementInfo(@Nonnull SPacketAdvancementInfo packet) {
        delegate.handleAdvancementInfo(packet);
    }

    @Override
    public void handleSelectAdvancementsTab(SPacketSelectAdvancementsTab packet) {
        delegate.handleSelectAdvancementsTab(packet);
    }

    @Override
    public void handleStatistics(SPacketStatistics packet) {
        delegate.handleStatistics(packet);
    }

    @Override
    public void handleRecipeBook(SPacketRecipeBook packet) {
        delegate.handleRecipeBook(packet);
    }

    @Override
    public void handleEntityEffect(SPacketEntityEffect packet) {
        delegate.handleEntityEffect(packet);
    }

    @Override
    public void handleCombatEvent(SPacketCombatEvent packet) {
        delegate.handleCombatEvent(packet);
    }

    @Override
    public void handleServerDifficulty(SPacketServerDifficulty packet) {
        delegate.handleServerDifficulty(packet);
    }

    @Override
    public void handleCamera(SPacketCamera packet) {
        delegate.handleCamera(packet);
    }

    @Override
    public void handleWorldBorder(SPacketWorldBorder packet) {
        delegate.handleWorldBorder(packet);
    }

    @Override
    public void handleTitle(SPacketTitle packet) {
        delegate.handleTitle(packet);
    }

    @Override
    public void handlePlayerListHeaderFooter(SPacketPlayerListHeaderFooter packet) {
        delegate.handlePlayerListHeaderFooter(packet);
    }

    @Override
    public void handleRemoveEntityEffect(SPacketRemoveEntityEffect packet) {
        delegate.handleRemoveEntityEffect(packet);
    }

    @Override
    public void handlePlayerListItem(SPacketPlayerListItem packet) {
        delegate.handlePlayerListItem(packet);
    }

    @Override
    public void handleKeepAlive(SPacketKeepAlive packet) {
        delegate.handleKeepAlive(packet);
    }

    @Override
    public void handlePlayerAbilities(SPacketPlayerAbilities packet) {
        delegate.handlePlayerAbilities(packet);
    }

    @Override
    public void handleTabComplete(SPacketTabComplete packet) {
        delegate.handleTabComplete(packet);
    }

    @Override
    public void handleSoundEffect(SPacketSoundEffect packet) {
        delegate.handleSoundEffect(packet);
    }

    @Override
    public void handleCustomSound(SPacketCustomSound packet) {
        delegate.handleCustomSound(packet);
    }

    @Override
    public void handleResourcePack(SPacketResourcePackSend packet) {
        delegate.handleResourcePack(packet);
    }

    @Override
    public void handleUpdateBossInfo(@Nonnull SPacketUpdateBossInfo packet) {
        delegate.handleUpdateBossInfo(packet);
    }

    @Override
    public void handleCooldown(SPacketCooldown packet) {
        delegate.handleCooldown(packet);
    }

    @Override
    public void handleMoveVehicle(@Nonnull SPacketMoveVehicle packet) {
        delegate.handleMoveVehicle(packet);
    }

    @Override
    public void handleCustomPayload(SPacketCustomPayload packet) {
        delegate.handleCustomPayload(packet);
    }

    @Override
    public void handleScoreboardObjective(SPacketScoreboardObjective packet) {
        delegate.handleScoreboardObjective(packet);
    }

    @Override
    public void handleUpdateScore(SPacketUpdateScore packet) {
        delegate.handleUpdateScore(packet);
    }

    @Override
    public void handleDisplayObjective(SPacketDisplayObjective packet) {
        delegate.handleDisplayObjective(packet);
    }

    @Override
    public void handleTeams(SPacketTeams packet) {
        delegate.handleTeams(packet);
    }

    @Override
    public void handleParticles(SPacketParticles packet) {
        delegate.handleParticles(packet);
    }

    @Override
    public void handleEntityProperties(SPacketEntityProperties packet) {
        delegate.handleEntityProperties(packet);
    }

    @Override @Nonnull
    public NetworkManager getNetworkManager() {
        return delegate.getNetworkManager();
    }

    @Override @Nonnull
    public Collection<NetworkPlayerInfo> getPlayerInfoMap() {
        return delegate.getPlayerInfoMap();
    }

    @Override @Nonnull
    public NetworkPlayerInfo getPlayerInfo(@Nonnull UUID uniqueId) {
        return delegate.getPlayerInfo(uniqueId);
    }

    @Nullable
    @Override
    public NetworkPlayerInfo getPlayerInfo(String name) {
        return delegate.getPlayerInfo(name);
    }

    @Override @Nonnull
    public GameProfile getGameProfile() {
        return delegate.getGameProfile();
    }

    @Override @Nonnull
    public ClientAdvancementManager getAdvancementManager() {
        return delegate.getAdvancementManager();
    }

}
