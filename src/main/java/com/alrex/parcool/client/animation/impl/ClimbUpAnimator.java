package com.alrex.parcool.client.animation.impl;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.PlayerModelRotator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.utilities.EasingFunctions;
import com.alrex.parcool.utilities.MathUtil;
import net.minecraft.world.entity.player.Player;

public class ClimbUpAnimator
extends Animator {
    private static final int MaxTick = 10;

    @Override
    public boolean shouldRemoved(Player player, Parkourability parkourability) {
        return 10 <= this.getTick();
    }

    private float bodyAngleFactor(float phase) {
        if ((double)phase <= 0.3) {
            return 1.0f - 11.1f * (phase - 0.3f) * (phase - 0.3f);
        }
        return 1.0f - EasingFunctions.SinInOutBySquare((phase - 0.3f) * 1.45f);
    }

    private float armAngle(float phase) {
        if ((double)phase <= 0.3) {
            return MathUtil.lerp(0.0f, -170.0f, 11.1f * (phase - 0.3f) * (phase - 0.3f));
        }
        return 0.0f;
    }

    @Override
    public void rotatePost(Player player, Parkourability parkourability, PlayerModelRotator rotator) {
        float phase = ((float)this.getTick() + rotator.getPartialTick()) / 10.0f;
        float factor = this.bodyAngleFactor(phase);
        rotator.startBasedCenter().rotatePitchFrontward(factor * 50.0f).end();
    }

    @Override
    public void animatePost(Player player, Parkourability parkourability, PlayerModelTransformer transformer) {
        float phase = ((float)this.getTick() + transformer.getPartialTick()) / 10.0f;
        float angleArm = this.armAngle(phase);
        float bodyAngleFactor = this.bodyAngleFactor(phase);
        transformer.rotateRightArm((float)Math.toRadians(angleArm), 0.0f, 0.0f).rotateLeftArm((float)Math.toRadians(angleArm), 0.0f, 0.0f).addRotateLeftLeg((float)Math.toRadians(bodyAngleFactor * -50.0f), 0.0f, 0.0f).addRotateRightLeg((float)Math.toRadians(bodyAngleFactor * -50.0f), 0.0f, 0.0f).makeArmsNatural().end();
    }
}

