package com.alrex.parcool.client.animation.impl;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.PlayerModelRotator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.utilities.Easing;
import net.minecraft.world.entity.player.Player;

public class WallJumpAnimator
extends Animator {
    private final boolean wallRightSide;
    private final int maxTick = 11;

    public WallJumpAnimator(boolean wallIsRightSide) {
        this.wallRightSide = wallIsRightSide;
    }

    private float getFactor(float phase) {
        return Easing.with(phase).squareOut(0.0f, 0.3f, 0.0f, 1.0f).linear(0.3f, 0.5f, 1.0f, 1.0f).sinInOut(0.5f, 1.0f, 1.0f, 0.0f).get();
    }

    float getFadeFactor(float phase) {
        return (float)(1.0 - Math.pow(2.0f * phase - 1.0f, 8.0));
    }

    @Override
    public boolean shouldRemoved(Player player, Parkourability parkourability) {
        return this.getTick() >= 11;
    }

    @Override
    public void animatePost(Player player, Parkourability parkourability, PlayerModelTransformer transformer) {
        float phase = ((float)this.getTick() + transformer.getPartialTick()) / 11.0f;
        float factor = this.getFactor(phase);
        float fadeFactor = this.getFadeFactor(phase);
        if (this.wallRightSide) {
            transformer.translateRightLeg(0.0f, -1.1f * Easing.with(phase).squareOut(0.0f, 0.15f, -1.0f, 1.0f).sinInOut(0.15f, 1.0f, 1.0f, 0.0f).get(), 3.2f * Easing.with(phase).squareOut(0.0f, 0.15f, -0.9f, 1.0f).sinInOut(0.15f, 1.0f, 1.0f, 0.0f).get()).translateLeftLeg(-1.1f * factor, 0.0f, -2.3f * factor).translateRightArm(factor, -1.9f * factor, -2.5f * factor).translateLeftArm(factor, factor, 1.9f * factor).rotateRightLeg((float)Math.toRadians(75.0f * factor), 0.0f, 0.0f, fadeFactor).rotateLeftLeg((float)Math.toRadians(-70.0f * factor), 0.0f, 0.0f, fadeFactor).rotateRightArm((float)Math.toRadians(factor * -120.0f), 0.0f, (float)Math.toRadians(-35.0f * factor), fadeFactor).rotateLeftArm((float)Math.toRadians(factor * 55.0f), 0.0f, (float)Math.toRadians(-35.0f * factor), fadeFactor).makeArmsNatural().end();
        } else {
            transformer.translateLeftLeg(0.0f, -1.1f * Easing.with(phase).squareOut(0.0f, 0.15f, -1.0f, 1.0f).sinInOut(0.15f, 1.0f, 1.0f, 0.0f).get(), 3.2f * Easing.with(phase).squareOut(0.0f, 0.15f, -0.9f, 1.0f).sinInOut(0.15f, 1.0f, 1.0f, 0.0f).get()).translateRightLeg(1.1f * factor, 0.0f, -2.3f * factor).translateLeftArm(-factor, -1.9f * factor, -2.5f * factor).translateRightArm(-factor, factor, 1.9f * factor).rotateRightLeg((float)Math.toRadians(-70.0f * factor), 0.0f, 0.0f, fadeFactor).rotateLeftLeg((float)Math.toRadians(75.0f * factor), 0.0f, 0.0f, fadeFactor).rotateRightArm((float)Math.toRadians(factor * 55.0f), 0.0f, (float)Math.toRadians(35.0f * factor), fadeFactor).rotateLeftArm((float)Math.toRadians(factor * -120.0f), 0.0f, (float)Math.toRadians(35.0f * factor), fadeFactor).makeArmsNatural().end();
        }
    }

    @Override
    public void rotatePost(Player player, Parkourability parkourability, PlayerModelRotator rotator) {
        float phase = ((float)this.getTick() + rotator.getPartialTick()) / 11.0f;
        float factor = this.getFactor(phase);
        rotator.startBasedCenter().rotatePitchFrontward(factor * 20.0f).rotateRollRightward((float)((this.wallRightSide ? -1 : 1) * 15) * factor).end();
    }
}

