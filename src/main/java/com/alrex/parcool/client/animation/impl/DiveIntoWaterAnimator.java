package com.alrex.parcool.client.animation.impl;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.PlayerModelRotator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.utilities.Easing;
import net.minecraft.world.entity.player.Player;

public class DiveIntoWaterAnimator
extends Animator {
    private static final int MAX_ANIMATION_TICK = 9;
    private final boolean fromSkyDive;

    public DiveIntoWaterAnimator(boolean fromSkyDive) {
        this.fromSkyDive = fromSkyDive;
    }

    @Override
    public boolean shouldRemoved(Player player, Parkourability parkourability) {
        return this.getTick() >= 9;
    }

    @Override
    public void animatePost(Player player, Parkourability parkourability, PlayerModelTransformer transformer) {
        float phase = ((float)this.getTick() + transformer.getPartialTick()) / 9.0f;
        if (phase > 1.0f) {
            return;
        }
        float animationFactor = new Easing(phase).sinInOut(0.0f, 0.1f, 0.0f, 1.0f).linear(0.1f, 0.7f, 1.0f, 1.0f).sinInOut(0.7f, 1.0f, 1.0f, 0.0f).get();
        float headPitchFactor = new Easing(phase).squareOut(0.0f, 0.5f, 0.0f, 1.0f).sinInOut(0.5f, 1.0f, 1.0f, 0.0f).get();
        float legPitchFactor = new Easing(phase).squareOut(0.0f, 0.65f, 0.0f, 1.0f).sinInOut(0.65f, 1.0f, 1.0f, 0.0f).get();
        float armRollFactor = new Easing(phase).squareOut(0.0f, 1.0f, 1.0f, 0.0f).get();
        transformer.rotateLeftArm(0.0f, 0.0f, (float)Math.toRadians(-170.0f * armRollFactor), animationFactor).rotateRightArm(0.0f, 0.0f, (float)Math.toRadians(170.0f * armRollFactor), animationFactor).rotateLeftLeg((float)Math.toRadians(-75.0f * legPitchFactor), 0.0f, 0.0f, animationFactor).rotateRightLeg((float)Math.toRadians(20.0f - 95.0f * legPitchFactor), 0.0f, 0.0f, animationFactor).makeLegsLittleMoving().makeArmsNatural().rotateAdditionallyHeadPitch((float)Math.toRadians(30.0f * headPitchFactor));
    }

    @Override
    public void rotatePost(Player player, Parkourability parkourability, PlayerModelRotator rotator) {
        float phase = ((float)this.getTick() + rotator.getPartialTick()) / 9.0f;
        if (phase > 1.0f) {
            return;
        }
        float pitchFactor = new Easing(phase).linear(0.0f, 0.1f, 0.0f, 0.0f).squareOut(0.1f, 1.0f, 0.0f, 1.0f).get();
        rotator.startBasedCenter().rotatePitchFrontward(this.fromSkyDive ? 90.0f + 270.0f * pitchFactor : 180.0f + 180.0f * pitchFactor).end();
    }
}

