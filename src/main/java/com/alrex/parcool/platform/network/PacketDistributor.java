package com.alrex.parcool.platform.network;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ClientPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;

/** Stand-in for NeoForge's PacketDistributor, backed by Fabric's networking API. */
public final class PacketDistributor {
    private PacketDistributor() {}

    public static void sendToPlayer(ServerPlayer player, CustomPacketPayload payload) {
        ServerPlayNetworking.send(player, payload);
    }

    public static void sendToServer(CustomPacketPayload payload) {
        ClientPlayNetworking.send(payload);
    }

    public static void sendToAllPlayers(ServerLevel level, CustomPacketPayload payload) {
        for (ServerPlayer p : PlayerLookup.world(level)) {
            ServerPlayNetworking.send(p, payload);
        }
    }

    public static void sendToTracking(ServerPlayer subject, CustomPacketPayload payload) {
        for (ServerPlayer p : PlayerLookup.tracking(subject)) {
            ServerPlayNetworking.send(p, payload);
        }
        ServerPlayNetworking.send(subject, payload);
    }
}
