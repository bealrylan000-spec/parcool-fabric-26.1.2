package com.alrex.parcool.common.network.payload;

import com.alrex.parcool.common.attachment.client.LocalStamina;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.common.info.ServerLimitation;
import io.netty.buffer.ByteBuf;
import javax.annotation.Nonnull;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import com.alrex.parcool.platform.network.IPayloadContext;

public record LimitationPayload(ServerLimitation limitation) implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<LimitationPayload> TYPE = new CustomPacketPayload.Type(ResourceLocation.fromNamespaceAndPath((String)"parcool", (String)"payload.limitation"));
    public static final StreamCodec<ByteBuf, LimitationPayload> CODEC = StreamCodec.composite(ServerLimitation.STREAM_CODEC, LimitationPayload::limitation, LimitationPayload::new);

    @Nonnull
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handleClient(LimitationPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            Parkourability parkourability = Parkourability.get(player);
            parkourability.getActionInfo().setServerLimitation(payload.limitation());
            if (player instanceof LocalPlayer) {
                LocalPlayer localPlayer = (LocalPlayer)player;
                parkourability.getActionInfo().updateStaminaType(LocalStamina.get(localPlayer), localPlayer);
            }
        });
    }

    public static void handleServer(LimitationPayload payload, IPayloadContext context) {
        throw new UnsupportedOperationException("This should have been designed not to be called");
    }
}

