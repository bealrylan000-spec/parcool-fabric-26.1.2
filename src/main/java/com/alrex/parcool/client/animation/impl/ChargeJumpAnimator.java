package com.alrex.parcool.client.animation.impl;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.PlayerModelRotator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.utilities.Easing;
import net.minecraft.world.entity.player.Player;

public class ChargeJumpAnimator
extends Animator {
    @Override
    public boolean shouldRemoved(Player player, Parkourability parkourability) {
        return this.getTick() >= 10;
    }

    @Override
    public boolean animatePre(Player player, Parkourability parkourability, PlayerModelTransformer transformer) {
        float animationPhase = Math.min(1.0f, ((float)this.getTick() + transformer.getPartialTick()) / 10.0f);
        float animFactor = new Easing(animationPhase).linear(0.0f, 8.0f, 1.0f, 1.0f).sinInOut(0.8f, 1.0f, 1.0f, 0.0f).get();
        float outFactor = new Easing(animationPhase).sinInOut(0.0f, 1.0f, 1.0f, 0.0f).get();
        transformer.translateLeftLeg(0.0f, -3.4f * animFactor * Easing.with(animationPhase).squareOut(0.0f, 0.35f, 1.0f, -0.25f).sinInOut(0.35f, 1.0f, -0.25f, 0.0f).get(), -2.6f * animFactor * Easing.with(animationPhase).squareOut(0.0f, 0.4f, 1.0f, -0.4f).sinInOut(0.4f, 1.0f, -0.4f, 0.0f).get()).translateRightLeg(0.0f, -1.4f * Easing.with(animationPhase).squareOut(0.0f, 0.4f, 1.0f, 1.2f).sinInOut(0.4f, 1.0f, 1.0f, 0.0f).get(), -1.6f * Easing.with(animationPhase).squareOut(0.0f, 0.4f, 1.0f, 1.5f).sinInOut(0.4f, 1.0f, 1.0f, 0.0f).get()).translateRightArm(0.3f * outFactor, Easing.with(animationPhase).squareOut(0.0f, 0.35f, 0.4f, -1.2f).sinInOut(0.35f, 1.0f, -1.2f, 0.0f).get(), 0.0f).translateLeftArm(-0.3f * outFactor, Easing.with(animationPhase).squareOut(0.0f, 0.35f, 0.4f, -1.2f).sinInOut(0.35f, 1.0f, -1.2f, 0.0f).get(), 0.0f).rotateLeftLeg((float)Math.toRadians(-15.0f + 55.0f * Easing.with(animationPhase).squareOut(0.0f, 0.4f, 0.0f, 1.0f).sinInOut(0.4f, 1.0f, 1.0f, 0.15f).get()), 0.0f, 0.0f, animFactor).rotateRightLeg((float)Math.toRadians(10.0f - 40.0f * Easing.with(animationPhase).squareOut(0.0f, 0.25f, 0.0f, 1.0f).sinInOut(0.25f, 1.0f, 1.0f, 0.25f).get()), 0.0f, 0.0f, animFactor).rotateLeftArm((float)Math.toRadians(Easing.with(animationPhase).squareOut(0.0f, 0.35f, 6.0f, -190.0f).linear(0.35f, 0.5f, -180.0f, -180.0f).sinInOut(0.5f, 1.0f, -180.0f, 0.0f).get()), 0.0f, (float)Math.toRadians(Easing.with(animationPhase).squareOut(0.0f, 0.35f, -4.0f, 2.0f).squareIn(0.35f, 0.85f, 5.0f, -15.0f).squareOut(0.85f, 1.0f, -15.0f, 0.0f).get())).rotateRightArm((float)Math.toRadians(Easing.with(animationPhase).squareOut(0.0f, 0.35f, -22.0f, -190.0f).linear(0.35f, 0.5f, -180.0f, -180.0f).sinInOut(0.5f, 1.0f, -180.0f, 0.0f).get()), 0.0f, (float)Math.toRadians(Easing.with(animationPhase).squareOut(0.0f, 0.35f, 4.0f, -2.0f).squareIn(0.35f, 0.85f, -5.0f, 15.0f).squareOut(0.85f, 1.0f, 15.0f, 0.0f).get())).makeArmsNatural().rotateAdditionallyHeadPitch(Easing.with(animationPhase).sinInOut(0.0f, 1.0f, -45.0f, 0.0f).get());
        return true;
    }

    @Override
    public boolean rotatePre(Player player, Parkourability parkourability, PlayerModelRotator rotator) {
        float animationPhase = Math.min(1.0f, ((float)this.getTick() + rotator.getPartialTick()) / 10.0f);
        float animFactor = new Easing(animationPhase).linear(0.0f, 8.0f, 1.0f, 1.0f).sinInOut(0.8f, 1.0f, 1.0f, 0.0f).get();
        rotator.rotateYawRightward(180.0f + rotator.getYRot()).translate(0.0f, 0.0f, 0.3f * animFactor * Easing.with(animationPhase).linear(0.0f, 1.0f, 1.0f, 0.0f).get()).rotatePitchFrontward(25.0f * animFactor * Easing.with(animationPhase).squareOut(0.0f, 0.45f, 1.0f, -0.2f).linear(0.45f, 0.55f, -0.2f, -0.2f).squareIn(0.55f, 1.0f, -0.2f, 0.0f).get());
        return true;
    }
}

