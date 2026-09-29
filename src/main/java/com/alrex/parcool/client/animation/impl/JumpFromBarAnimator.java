package com.alrex.parcool.client.animation.impl;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.PlayerModelRotator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.utilities.EasingFunctions;
import com.alrex.parcool.utilities.MathUtil;
import net.minecraft.world.entity.player.Player;

public class JumpFromBarAnimator
extends Animator {
    private final int MAX_TICK = 8;

    @Override
    public boolean shouldRemoved(Player player, Parkourability parkourability) {
        return this.getTick() >= 8;
    }

    private static float getFactor(float phase) {
        if (phase < 0.333f) {
            return 0.5f + 0.5f * EasingFunctions.SinInOutBySquare(phase * 3.0f);
        }
        if (phase < 0.666f) {
            return 1.0f - EasingFunctions.SinInOutBySquare((phase - 0.333f) * 3.0f);
        }
        return 0.5f * EasingFunctions.SinInOutBySquare((phase - 0.666f) * 3.0f);
    }

    @Override
    public void animatePost(Player player, Parkourability parkourability, PlayerModelTransformer transformer) {
        float tick = (float)this.getTick() + transformer.getPartialTick();
        float phase = tick / 8.0f;
        float factor = JumpFromBarAnimator.getFactor(phase);
        float armFactor = MathUtil.squaring(phase - 1.0f);
        float armYAngleFactor = 1.0f - 4.0f * MathUtil.squaring(phase - 0.5f);
        float bodyFactor = this.getBodyAngleFactor(phase);
        float animationFactor = 1.0f - phase * phase * phase;
        transformer.rotateHeadPitch(bodyFactor * 40.0f).rotateRightArm((float)Math.toRadians(180.0f * armFactor), (float)Math.toRadians(-45.0f * armYAngleFactor), 0.0f, animationFactor).rotateLeftArm((float)Math.toRadians(180.0f * armFactor), (float)Math.toRadians(45.0f * armYAngleFactor), 0.0f, animationFactor).rotateRightLeg((float)Math.toRadians(30.0f - 60.0f * factor), 0.0f, 0.0f, animationFactor).rotateLeftLeg((float)Math.toRadians(30.0f - 60.0f * factor), 0.0f, 0.0f, animationFactor).makeLegsLittleMoving().end();
    }

    private float getBodyAngleFactor(float phase) {
        return 1.0f - 4.0f * MathUtil.squaring(phase - 0.5f);
    }

    @Override
    public void rotatePost(Player player, Parkourability parkourability, PlayerModelRotator rotator) {
        float tick = (float)this.getTick() + rotator.getPartialTick();
        float phase = tick / 8.0f;
        float factor = this.getBodyAngleFactor(phase);
        rotator.startBasedCenter().rotatePitchFrontward(-factor * 40.0f).end();
    }
}

