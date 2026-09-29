package com.alrex.parcool.common.network.payload;

import com.alrex.parcool.common.action.impl.BreakfallReady;
import com.alrex.parcool.common.attachment.common.Parkourability;
import io.netty.buffer.ByteBuf;
import javax.annotation.Nonnull;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import com.alrex.parcool.platform.network.IPayloadContext;

public record StartBreakfallEventPayload(boolean justTimed) implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<StartBreakfallEventPayload> TYPE = new CustomPacketPayload.Type(ResourceLocation.fromNamespaceAndPath((String)"parcool", (String)"payload.start_breakfall_event"));
    public static final StreamCodec<ByteBuf, StartBreakfallEventPayload> CODEC = StreamCodec.composite((StreamCodec)ByteBufCodecs.BOOL, StartBreakfallEventPayload::justTimed, StartBreakfallEventPayload::new);

    @Nonnull
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handleClient(StartBreakfallEventPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            Parkourability parkourability = Parkourability.get(player);
            parkourability.get(BreakfallReady.class).startBreakfall(player, parkourability, payload.justTimed());
        });
    }

    public static void handleServer(StartBreakfallEventPayload payload, IPayloadContext context) {
        throw new UnsupportedOperationException("This should have been designed not to be called");
    }
}

