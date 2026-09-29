package com.alrex.parcool.common.handlers;

import com.alrex.parcool.common.action.impl.HideInBlock;
import com.alrex.parcool.common.attachment.common.Parkourability;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import com.alrex.parcool.platform.eventbus.SubscribeEvent;
import com.alrex.parcool.platform.event.entity.living.LivingEvent;

public class PlayerVisibilityHandler {
    @SubscribeEvent
    public static void onLivingVisibilityEvent(LivingEvent.LivingVisibilityEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity instanceof Player) {
            Player player = (Player)entity;
            Parkourability parkourability = Parkourability.get(player);
            if (parkourability == null) {
                return;
            }
            if (parkourability.get(HideInBlock.class).isDoing()) {
                event.modifyVisibility(event.getVisibilityModifier() * 0.1);
            }
        }
    }
}

