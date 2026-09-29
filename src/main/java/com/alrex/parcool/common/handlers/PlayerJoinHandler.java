package com.alrex.parcool.common.handlers;

import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.common.info.ClientSetting;
import com.alrex.parcool.common.network.payload.ClientInformationPayload;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import com.alrex.parcool.platform.eventbus.SubscribeEvent;
import com.alrex.parcool.platform.event.entity.EntityJoinLevelEvent;
import com.alrex.parcool.platform.network.PacketDistributor;

public class PlayerJoinHandler {
    @SubscribeEvent
    public static void onPlayerJoin(EntityJoinLevelEvent event) {
        Player player;
        if (!event.getLevel().isClientSide()) {
            return;
        }
        Entity entity = event.getEntity();
        if (entity instanceof Player && (player = (Player)entity) instanceof LocalPlayer) {
            Parkourability parkourability = Parkourability.get(player);
            if (parkourability == null) {
                return;
            }
            parkourability.getActionInfo().setClientSetting(ClientSetting.readFromLocalConfig());
            PacketDistributor.sendToServer((CustomPacketPayload)new ClientInformationPayload(player.getUUID(), true, parkourability.getClientInfo()), (CustomPacketPayload[])new CustomPacketPayload[0]);
        }
    }
}

