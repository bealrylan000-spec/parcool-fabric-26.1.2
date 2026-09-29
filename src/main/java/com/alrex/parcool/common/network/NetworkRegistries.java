package com.alrex.parcool.common.network;

import com.alrex.parcool.common.network.payload.ActionStateBroadcastPayload;
import com.alrex.parcool.common.network.payload.ActionStatePayload;
import com.alrex.parcool.common.network.payload.ClientInformationPayload;
import com.alrex.parcool.common.network.payload.LimitationPayload;
import com.alrex.parcool.common.network.payload.StaminaBroadcastPayload;
import com.alrex.parcool.common.network.payload.StaminaPayload;
import com.alrex.parcool.common.network.payload.StaminaProcessOnServerPayload;
import com.alrex.parcool.common.network.payload.StartBreakfallEventPayload;
import com.alrex.parcool.platform.network.IPayloadContext;
import net.fabricmc.fabric.api.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

/**
 * Fabric replacement for the original NeoForge RegisterPayloadHandlersEvent-based registration.
 * The payload record classes themselves (ActionStatePayload, StaminaPayload, etc.) are vanilla
 * CustomPacketPayload/StreamCodec and needed NO changes - only this registration glue differs.
 *
 * Fabric's networking callbacks already run on the main client/server thread, so unlike NeoForge
 * there's no need for IPayloadContext.enqueueWork() to actually defer anything - it just runs the
 * task inline (see the two Ctx adapters below).
 */
public final class NetworkRegistries {
    private NetworkRegistries() {}

    private record ServerCtx(ServerPlayNetworking.Context ctx) implements IPayloadContext {
        public net.minecraft.world.entity.player.Player player() { return ctx.player(); }
        public void enqueueWork(Runnable task) { task.run(); }
    }

    private record ClientCtx(ClientPlayNetworking.Context ctx) implements IPayloadContext {
        public net.minecraft.world.entity.player.Player player() { return ctx.player(); }
        public void enqueueWork(Runnable task) { task.run(); }
    }

    public static void registerAll() {
        bidirectional(StartBreakfallEventPayload.TYPE, StartBreakfallEventPayload.CODEC,
                StartBreakfallEventPayload::handleClient, StartBreakfallEventPayload::handleServer);
        bidirectional(ActionStatePayload.TYPE, ActionStatePayload.CODEC,
                ActionStatePayload::handleClient, ActionStatePayload::handleServer);
        bidirectional(LimitationPayload.TYPE, LimitationPayload.CODEC,
                LimitationPayload::handleClient, LimitationPayload::handleServer);
        bidirectional(ClientInformationPayload.TYPE, ClientInformationPayload.CODEC,
                ClientInformationPayload::handleClient, ClientInformationPayload::handleServer);
        bidirectional(StaminaPayload.TYPE, StaminaPayload.CODEC,
                StaminaPayload::handleClient, StaminaPayload::handleServer);
        bidirectional(StaminaProcessOnServerPayload.TYPE, StaminaProcessOnServerPayload.CODEC,
                StaminaProcessOnServerPayload::handleClient, StaminaProcessOnServerPayload::handleServer);

        toClientOnly(StaminaBroadcastPayload.TYPE, StaminaBroadcastPayload.CODEC, StaminaBroadcastPayload::handleClient);
        toClientOnly(ActionStateBroadcastPayload.TYPE, ActionStateBroadcastPayload.CODEC, ActionStateBroadcastPayload::handleClient);
    }

    private static <T extends net.minecraft.network.protocol.common.custom.CustomPacketPayload> void bidirectional(
            net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type<T> type,
            net.minecraft.network.codec.StreamCodec<? super io.netty.buffer.ByteBuf, T> codec,
            java.util.function.BiConsumer<T, IPayloadContext> onClient,
            java.util.function.BiConsumer<T, IPayloadContext> onServer) {
        PayloadTypeRegistry.playC2S().register(type, codec);
        PayloadTypeRegistry.playS2C().register(type, codec);
        ServerPlayNetworking.registerGlobalReceiver(type, (payload, ctx) -> onServer.accept(payload, new ServerCtx(ctx)));
        ClientPlayNetworking.registerGlobalReceiver(type, (payload, ctx) -> onClient.accept(payload, new ClientCtx(ctx)));
    }

    private static <T extends net.minecraft.network.protocol.common.custom.CustomPacketPayload> void toClientOnly(
            net.minecraft.network.protocol.common.custom.CustomPacketPayload.Type<T> type,
            net.minecraft.network.codec.StreamCodec<? super io.netty.buffer.ByteBuf, T> codec,
            java.util.function.BiConsumer<T, IPayloadContext> onClient) {
        PayloadTypeRegistry.playS2C().register(type, codec);
        ClientPlayNetworking.registerGlobalReceiver(type, (payload, ctx) -> onClient.accept(payload, new ClientCtx(ctx)));
    }
}
