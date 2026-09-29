package com.alrex.parcool.common.network.payload;

import com.alrex.parcool.platform.attachment.AttachmentHolder;

import com.alrex.parcool.common.attachment.Attachments;
import com.alrex.parcool.common.attachment.common.ReadonlyStamina;
import com.alrex.parcool.common.network.StaminaSynchronizationBroadcaster;
import io.netty.buffer.ByteBuf;
import java.util.UUID;
import javax.annotation.Nonnull;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import com.alrex.parcool.platform.network.IPayloadContext;

public record StaminaPayload(UUID playerID, ReadonlyStamina stamina) implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<StaminaPayload> TYPE = new CustomPacketPayload.Type(ResourceLocation.fromNamespaceAndPath((String)"parcool", (String)"payload.stamina"));
    public static final StreamCodec<ByteBuf, StaminaPayload> CODEC = StreamCodec.composite((StreamCodec)UUIDUtil.STREAM_CODEC, StaminaPayload::playerID, ReadonlyStamina.STREAM_CODEC, StaminaPayload::stamina, StaminaPayload::new);

    @Nonnull
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public void processPlayer(IPayloadContext context) {
        Player player = context.player().level().getPlayerByUUID(this.playerID);
        if (player == null || player.isLocalPlayer()) {
            return;
        }
        AttachmentHolder.setData(player, Attachments.STAMINA, (Object)this.stamina);
    }

    public static void handleClient(StaminaPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> payload.processPlayer(context));
    }

    public static void handleServer(StaminaPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            payload.processPlayer(context);
            StaminaSynchronizationBroadcaster.add(payload.playerID, payload.stamina);
        });
    }
}

