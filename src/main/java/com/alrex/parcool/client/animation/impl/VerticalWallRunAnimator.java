package com.alrex.parcool.client.animation.impl;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.PlayerModelRotator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.utilities.Easing;
import net.minecraft.world.entity.player.Player;

public class VerticalWallRunAnimator
extends Animator {
    static final int MAX_ANIMATION_TICK = 10;
    static final int ROLL_ANGLE = 15;

    @Override
    public boolean shouldRemoved(Player player, Parkourability parkourability) {
        return this.getTick() >= 10;
    }

    @Override
    public void animatePost(Player player, Parkourability parkourability, PlayerModelTransformer transformer) {
        float phase = ((float)this.getTick() + transformer.getPartialTick()) / 10.0f;
        if (phase > 1.0f) {
            return;
        }
        float animFactor = new Easing(phase).linear(0.0f, 0.75f, 1.0f, 1.0f).squareIn(0.75f, 1.0f, 1.0f, 0.0f).get();
        float rightLegXFactor = new Easing(phase).squareOut(0.0f, 0.25f, 0.0f, -1.0f).sinInOut(0.25f, 0.55f, -1.0f, 1.0f).sinInOut(0.55f, 1.0f, 1.0f, 0.0f).get();
        float leftLegXFactor = new Easing(phase).squareOut(0.0f, 0.25f, 0.0f, 1.0f).sinInOut(0.25f, 0.55f, 1.0f, -1.0f).sinInOut(0.55f, 1.0f, -1.0f, 0.0f).get();
        float rightArmXFactor = new Easing(phase).squareOut(0.0f, 0.55f, 0.0f, 1.0f).sinInOut(0.55f, 1.0f, 1.0f, 0.0f).get();
        float rightArmZFactor = new Easing(phase).sinInOut(0.0f, 0.3f, 0.0f, 1.0f).sinInOut(0.3f, 0.6f, 1.0f, 0.1f).linear(0.6f, 1.0f, 0.1f, 0.0f).get();
        float leftArmXFactor = new Easing(phase).sinInOut(0.0f, 0.65f, 1.0f, -0.3f).sinInOut(0.65f, 1.0f, -0.3f, 0.0f).get();
        float leftArmZFactor = new Easing(phase).linear(0.0f, 0.25f, 0.0f, 0.0f).sinInOut(0.25f, 0.6f, 0.0f, 1.0f).sinInOut(0.6f, 1.0f, 1.0f, 0.0f).get();
        float headFactor = new Easing(phase).sinInOut(0.0f, 0.3f, 0.0f, 1.0f).sinInOut(0.3f, 1.0f, 1.0f, 0.0f).get();
        float rollFactor = new Easing(phase).squareOut(0.0f, 0.4f, 0.0f, 1.0f).linear(0.4f, 0.75f, 1.0f, 1.0f).sinInOut(0.75f, 1.0f, 1.0f, 0.0f).get();
        transformer.rotateAdditionallyHeadPitch(15.0f * headFactor).rotateAdditionallyHeadRoll(-15.0f * rollFactor / 2.0f).rotateRightLeg((float)Math.toRadians(-45.0f + 50.0f * rightLegXFactor), 0.0f, (float)Math.toRadians(-14.25 * (double)rollFactor), animFactor).rotateLeftLeg((float)Math.toRadians(-40.0f + 55.0f * leftLegXFactor), 0.0f, (float)Math.toRadians(-18.0 * (double)rollFactor), animFactor).rotateRightArm((float)Math.toRadians(-180.0f * rightArmXFactor), 0.0f, (float)Math.toRadians(-15.0f * rollFactor - 20.0f * rightArmZFactor), animFactor).rotateLeftArm((float)Math.toRadians(-180.0f * leftArmXFactor), 0.0f, (float)Math.toRadians(leftArmZFactor * -30.0f), animFactor).end();
    }

    @Override
    public void rotatePost(Player player, Parkourability parkourability, PlayerModelRotator rotator) {
        float phase = ((float)this.getTick() + rotator.getPartialTick()) / 10.0f;
        if (phase > 1.0f) {
            return;
        }
        float factor = new Easing(phase).sinInOut(0.0f, 0.3f, 0.0f, 1.0f).sinInOut(0.3f, 1.0f, 1.0f, 0.0f).get();
        float rollFactor = new Easing(phase).squareOut(0.0f, 0.4f, 0.0f, 1.0f).linear(0.4f, 0.75f, 1.0f, 1.0f).sinInOut(0.75f, 1.0f, 1.0f, 0.0f).get();
        rotator.startBasedCenter().rotatePitchFrontward(-10.0f * factor).rotateRollRightward(-15.0f * rollFactor).end();
    }
}

