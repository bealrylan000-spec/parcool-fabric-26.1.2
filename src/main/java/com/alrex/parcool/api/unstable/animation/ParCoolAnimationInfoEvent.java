package com.alrex.parcool.api.unstable.animation;

import com.alrex.parcool.api.unstable.animation.AnimationOption;
import com.alrex.parcool.client.animation.Animator;
import net.minecraft.client.player.AbstractClientPlayer;
import com.alrex.parcool.platform.dist.Dist;
import com.alrex.parcool.platform.dist.OnlyIn;
import com.alrex.parcool.platform.eventbus.Event;

@OnlyIn(value=Dist.CLIENT)
public class ParCoolAnimationInfoEvent
extends Event {
    private final AbstractClientPlayer player;
    private final Animator animator;
    private final AnimationOption option;

    public ParCoolAnimationInfoEvent(AbstractClientPlayer player, Animator animator) {
        this.animator = animator;
        this.player = player;
        this.option = new AnimationOption();
    }

    public AbstractClientPlayer getPlayer() {
        return this.player;
    }

    public Animator getAnimator() {
        return this.animator;
    }

    public AnimationOption getOption() {
        return this.option;
    }
}

