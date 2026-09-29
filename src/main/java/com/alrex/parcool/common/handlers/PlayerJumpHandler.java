package com.alrex.parcool.common.handlers;

import com.alrex.parcool.common.action.impl.ChargeJump;
import com.alrex.parcool.common.action.impl.Dive;
import com.alrex.parcool.common.action.impl.Flipping;
import com.alrex.parcool.common.attachment.common.Parkourability;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import com.alrex.parcool.platform.eventbus.SubscribeEvent;
import com.alrex.parcool.platform.event.entity.living.LivingEvent;

public class PlayerJumpHandler {
    @SubscribeEvent
    public static void onJump(LivingEvent.LivingJumpEvent event) {
        LivingEntity livingEntity = event.getEntity();
        if (!(livingEntity instanceof Player)) {
            return;
        }
        Player player = (Player)livingEntity;
        Parkourability parkourability = Parkourability.get(player);
        if (parkourability == null) {
            return;
        }
        parkourability.getAdditionalProperties().onJump();
        if (!player.isLocalPlayer()) {
            return;
        }
        parkourability.get(Dive.class).onJump(player, parkourability);
        parkourability.get(Flipping.class).onJump(player, parkourability);
        parkourability.get(ChargeJump.class).onJump(player, parkourability);
    }
}

