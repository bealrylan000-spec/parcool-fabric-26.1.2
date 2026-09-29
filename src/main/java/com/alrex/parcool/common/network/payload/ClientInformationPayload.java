package com.alrex.parcool.common.network.payload;

import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.common.info.ClientSetting;
import com.alrex.parcool.server.limitation.Limitations;
import io.netty.buffer.ByteBuf;
import java.util.UUID;
import javax.annotation.Nonnull;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import com.alrex.parcool.platform.network.PacketDistributor;
import com.alrex.parcool.platform.network.IPayloadContext;

public record ClientInformationPayload(UUID playerID, boolean requestLimitation, ClientSetting information) implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<ClientInformationPayload> TYPE = new CustomPacketPayload.Type(Identifier.fromNamespaceAndPath((String)"parcool", (String)"payload.client_info"));
    public static final StreamCodec<ByteBuf, ClientInformationPayload> CODEC = StreamCodec.composite((StreamCodec)ByteBufCodecs.VAR_LONG, p -> p.playerID().getMostSignificantBits(), (StreamCodec)ByteBufCodecs.VAR_LONG, p -> p.playerID().getLeastSignificantBits(), (StreamCodec)ByteBufCodecs.BOOL, ClientInformationPayload::requestLimitation, ClientSetting.STREAM_CODEC, ClientInformationPayload::information, (ms, ls, r, i) -> new ClientInformationPayload(new UUID((long)ms, (long)ls), (boolean)r, (ClientSetting)i));

    @Nonnull
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handleClient(ClientInformationPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Level world = context.player().level();
            Player player = world.getPlayerByUUID(payload.playerID());
            if (player == null || player.isLocalPlayer()) {
                return;
            }
            Parkourability parkourability = Parkourability.get(player);
            if (parkourability == null) {
                return;
            }
            parkourability.getActionInfo().setClientSetting(payload.information());
        });
    }

    public static void handleServer(ClientInformationPayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            PacketDistributor.sendToAllPlayers((CustomPacketPayload)payload, (CustomPacketPayload[])new CustomPacketPayload[0]);
            Parkourability parkourability = Parkourability.get(player);
            if (parkourability == null) {
                return;
            }
            if (player instanceof ServerPlayer) {
                ServerPlayer serverPlayer = (ServerPlayer)player;
                if (payload.requestLimitation()) {
                    Limitations.update(serverPlayer);
                }
            }
            parkourability.getActionInfo().setClientSetting(payload.information());
        });
    }
}

