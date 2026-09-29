package com.alrex.parcool.extern.paraglider;

import com.alrex.parcool.api.unstable.animation.ParCoolAnimationInfoEvent;
import com.alrex.parcool.extern.AdditionalMods;
import net.minecraft.world.entity.player.Player;
import com.alrex.parcool.platform.dist.Dist;
import com.alrex.parcool.platform.dist.OnlyIn;
import com.alrex.parcool.platform.eventbus.SubscribeEvent;

public class EventConsumerForParaglider {
    @OnlyIn(value=Dist.CLIENT)
    @SubscribeEvent
    public static void onUpdateAnimateInfo(ParCoolAnimationInfoEvent event) {
        if (AdditionalMods.paraglider().isFallingWithParaglider((Player)event.getPlayer())) {
            event.getOption().cancelAnimation();
        }
    }
}

