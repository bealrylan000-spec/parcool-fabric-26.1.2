package com.alrex.parcool.common.network;

import com.alrex.parcool.common.network.payload.ActionStateBroadcastPayload;
import com.alrex.parcool.common.network.payload.ActionStatePayload;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import com.alrex.parcool.platform.eventbus.SubscribeEvent;
import com.alrex.parcool.platform.event.tick.ServerTickEvent;
import com.alrex.parcool.platform.network.PacketDistributor;

public class ActionSynchronizationBroadcaster {
    private static List<ActionStatePayload> pendingPayloads = new ArrayList<ActionStatePayload>();

    public static void add(ActionStatePayload payload) {
        pendingPayloads.addLast(payload);
    }

    private static void send() {
        if (pendingPayloads.isEmpty()) {
            return;
        }
        PacketDistributor.sendToAllPlayers((CustomPacketPayload)new ActionStateBroadcastPayload(pendingPayloads), (CustomPacketPayload[])new CustomPacketPayload[0]);
        pendingPayloads = new ArrayList<ActionStatePayload>();
    }

    @SubscribeEvent
    public static void onTick(ServerTickEvent.Post event) {
        ActionSynchronizationBroadcaster.send();
    }
}

