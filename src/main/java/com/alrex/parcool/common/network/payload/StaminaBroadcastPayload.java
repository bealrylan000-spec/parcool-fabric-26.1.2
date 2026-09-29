package com.alrex.parcool.common.network.payload;

import com.alrex.parcool.common.network.ListStreamCodec;
import com.alrex.parcool.common.network.payload.StaminaPayload;
import io.netty.buffer.ByteBuf;
import java.util.List;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import com.alrex.parcool.platform.network.IPayloadContext;

public record StaminaBroadcastPayload(List<StaminaPayload> staminaList) implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<StaminaBroadcastPayload> TYPE = new CustomPacketPayload.Type(Identifier.fromNamespaceAndPath((String)"parcool", (String)"payload.stamina.broadcast"));
    private static final StreamCodec<ByteBuf, List<StaminaPayload>> STAMINA_CODEC = new ListStreamCodec<ByteBuf, StaminaPayload>(StaminaPayload.CODEC);
    public static final StreamCodec<ByteBuf, StaminaBroadcastPayload> CODEC = StreamCodec.composite(STAMINA_CODEC, StaminaBroadcastPayload::staminaList, StaminaBroadcastPayload::new);

    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handleClient(StaminaBroadcastPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            for (StaminaPayload elem : payload.staminaList()) {
                elem.processPlayer(context);
            }
        });
    }
}

