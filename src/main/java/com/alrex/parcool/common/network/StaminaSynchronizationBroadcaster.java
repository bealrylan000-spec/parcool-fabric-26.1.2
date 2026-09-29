package com.alrex.parcool.common.network;

import com.alrex.parcool.common.attachment.common.ReadonlyStamina;
import com.alrex.parcool.common.network.payload.StaminaBroadcastPayload;
import com.alrex.parcool.common.network.payload.StaminaPayload;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import com.alrex.parcool.platform.eventbus.SubscribeEvent;
import com.alrex.parcool.platform.event.tick.ServerTickEvent;
import com.alrex.parcool.platform.network.PacketDistributor;

public class StaminaSynchronizationBroadcaster {
    private static Map<UUID, ReadonlyStamina> staminaMap = new TreeMap<UUID, ReadonlyStamina>();
    private static int syncCoolTime = 10;

    public static void add(UUID playerID, ReadonlyStamina stamina) {
        staminaMap.put(playerID, stamina);
    }

    private static void send() {
        if (staminaMap.isEmpty()) {
            return;
        }
        List<StaminaPayload> payloads = staminaMap.entrySet().stream().map(entry -> new StaminaPayload((UUID)entry.getKey(), (ReadonlyStamina)entry.getValue())).toList();
        staminaMap = new TreeMap<UUID, ReadonlyStamina>();
        PacketDistributor.sendToAllPlayers((CustomPacketPayload)new StaminaBroadcastPayload(payloads), (CustomPacketPayload[])new CustomPacketPayload[0]);
    }

    @SubscribeEvent
    public static void onTick(ServerTickEvent.Post event) {
        if (--syncCoolTime > 0) {
            return;
        }
        syncCoolTime = 10;
        StaminaSynchronizationBroadcaster.send();
    }
}

