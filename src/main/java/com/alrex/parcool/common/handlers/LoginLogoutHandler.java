package com.alrex.parcool.common.handlers;

import com.alrex.parcool.server.limitation.Limitations;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import com.alrex.parcool.platform.eventbus.SubscribeEvent;
import com.alrex.parcool.platform.event.entity.player.PlayerEvent;

public class LoginLogoutHandler {
    @SubscribeEvent
    public static void onLogoutInServer(PlayerEvent.PlayerLoggedOutEvent event) {
        Player player = event.getEntity();
        if (player instanceof ServerPlayer) {
            Limitations.unload(player.getUUID());
        }
    }
}

