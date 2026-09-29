package com.alrex.parcool.client.animation.impl;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.PlayerModelRotator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.action.impl.Tap;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.utilities.Easing;
import net.minecraft.world.entity.player.Player;

public class TapAnimator
extends Animator {
    @Override
    public boolean shouldRemoved(Player player, Parkourability parkourability) {
        return this.getTick() >= parkourability.get(Tap.class).getMaxTappingTick();
    }

    public float getAngleFactor(float phase) {
        return new Easing(phase).squareOut(0.0f, 0.25f, 0.0f, 1.0f).sinInOut(0.25f, 1.0f, 1.0f, 0.0f).get();
    }

    @Override
    public void animatePost(Player player, Parkourability parkourability, PlayerModelTransformer transformer) {
        float phase = ((float)this.getTick() + transformer.getPartialTick()) / (float)parkourability.get(Tap.class).getMaxTappingTick();
        if (phase > 1.0f) {
            phase = 1.0f;
        }
        float factor = this.getAngleFactor(phase);
        float animFactor = new Easing(phase).squareOut(0.0f, 0.25f, 0.0f, 1.0f).linear(0.25f, 0.75f, 1.0f, 1.0f).squareIn(0.75f, 1.0f, 1.0f, 0.0f).get();
        float angle = 80.0f * factor;
        transformer.translateRightLeg(0.0f, -2.0f * factor, factor).translateLeftLeg(0.0f, -5.0f * factor, -1.5f * factor).translateRightArm(0.0f, 4.0f * factor, 0.0f).rotateLeftLeg((float)Math.toRadians(-30.0f * factor), 0.0f, 0.0f, animFactor).rotateRightLeg((float)Math.toRadians(-15.0f * factor), 0.0f, 0.0f, animFactor).rotateRightArm((float)Math.toRadians(20.0f * factor), 0.0f, (float)Math.toRadians(13.0f * factor), animFactor).rotateLeftArm((float)Math.toRadians(-angle), 0.0f, 0.0f, animFactor).rotateAdditionallyHeadPitch(-40.0f * factor).makeArmsNatural().end();
    }

    @Override
    public void rotatePost(Player player, Parkourability parkourability, PlayerModelRotator rotator) {
        float phase = ((float)this.getTick() + rotator.getPartialTick()) / (float)parkourability.get(Tap.class).getMaxTappingTick();
        if (phase > 1.0f) {
            phase = 1.0f;
        }
        float factor = this.getAngleFactor(phase);
        float angle = 80.0f * factor;
        rotator.startBasedCenter().translateY(-factor * player.getBbHeight() / 5.0f).rotatePitchFrontward(angle).endEnabledLegGrounding();
    }
}

