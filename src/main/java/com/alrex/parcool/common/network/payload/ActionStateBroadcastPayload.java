package com.alrex.parcool.common.network.payload;

import com.alrex.parcool.common.network.ListStreamCodec;
import com.alrex.parcool.common.network.payload.ActionStatePayload;
import io.netty.buffer.ByteBuf;
import java.util.List;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import com.alrex.parcool.platform.network.IPayloadContext;

public record ActionStateBroadcastPayload(List<ActionStatePayload> payloads) implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<ActionStateBroadcastPayload> TYPE = new CustomPacketPayload.Type(Identifier.fromNamespaceAndPath((String)"parcool", (String)"payload.action_state.broadcast"));
    public static final StreamCodec<ByteBuf, ActionStateBroadcastPayload> CODEC = StreamCodec.composite(new ListStreamCodec<ByteBuf, ActionStatePayload>(ActionStatePayload.CODEC), ActionStateBroadcastPayload::payloads, ActionStateBroadcastPayload::new);

    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handleClient(ActionStateBroadcastPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Level world = context.player().level();
            for (ActionStatePayload statePayload : payload.payloads()) {
                Player player = world.getPlayerByUUID(statePayload.playerID());
                if (player == null || player.isLocalPlayer()) {
                    return;
                }
                statePayload.processPlayer(player);
            }
        });
    }
}

