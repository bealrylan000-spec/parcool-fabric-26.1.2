package com.alrex.parcool.client.animation.impl;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.action.impl.CatLeap;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.utilities.Easing;
import com.alrex.parcool.utilities.EasingFunctions;
import com.alrex.parcool.utilities.MathUtil;
import net.minecraft.world.entity.player.Player;

public class CatLeapAnimator
extends Animator {
    @Override
    public boolean shouldRemoved(Player player, Parkourability parkourability) {
        return !parkourability.get(CatLeap.class).isDoing() || this.getTick() > 20;
    }

    @Override
    public void animatePost(Player player, Parkourability parkourability, PlayerModelTransformer transformer) {
        CatLeap catLeap = parkourability.get(CatLeap.class);
        float phase = ((float)catLeap.getDoingTick() + transformer.getPartialTick()) / 20.0f;
        if (phase > 1.0f) {
            phase = 1.0f;
        }
        float factor = this.movingFactorFunc(phase);
        float animationFactor = 1.0f - phase * phase * phase * phase;
        float armY = 1.2f * factor * animationFactor;
        float armZ = 0.8f * factor * animationFactor;
        transformer.translateLeftArm(0.0f, -armY, -armZ).translateRightArm(0.0f, -armY, -armZ).translateLeftLeg(0.0f, 0.0f, -Easing.with(phase).squareOut(0.0f, 0.1f, 0.0f, -1.2f).sinInOut(0.1f, 0.25f, -1.2f, 0.6f).sinInOut(0.25f, 1.0f, 0.6f, 0.0f).get()).translateRightLeg(0.0f, 0.0f, Easing.with(phase).squareOut(0.0f, 0.1f, 0.0f, -1.2f).sinInOut(0.1f, 0.25f, -1.2f, 0.6f).sinInOut(0.25f, 1.0f, 0.6f, 0.0f).get()).rotateLeftArm((float)(-Math.toRadians(MathUtil.lerp(-25.0f, 170.0f, factor))), 0.0f, (float)(-Math.toRadians(MathUtil.lerp(24.0f, -4.0f, factor))), animationFactor).rotateRightArm((float)(-Math.toRadians(MathUtil.lerp(-25.0f, 170.0f, factor))), 0.0f, (float)Math.toRadians(MathUtil.lerp(24.0f, -4.0f, factor)), animationFactor).makeArmsNatural().rotateLeftLeg((float)Math.toRadians(-15.0f + Easing.with(phase).squareOut(0.0f, 0.1f, -5.0f, 65.0f).sinInOut(0.1f, 0.25f, 65.0f, -40.0f).sinInOut(0.25f, 0.65f, -40.0f, 20.0f).sinInOut(0.65f, 1.0f, 20.0f, 0.0f).get()), 0.0f, 0.0f, animationFactor).rotateRightLeg((float)Math.toRadians(-15.0f + Easing.with(phase).squareOut(0.0f, 0.1f, 10.0f, -40.0f).sinInOut(0.1f, 0.25f, -40.0f, 65.0f).sinInOut(0.25f, 0.65f, 65.0f, -15.0f).sinInOut(0.65f, 1.0f, -15.0f, 0.0f).get()), 0.0f, 0.0f, animationFactor).end();
    }

    private float movingFactorFunc(float phase) {
        if ((double)phase > 0.2) {
            return 1.0f - EasingFunctions.CubicInOut((phase - 0.2f) * 1.25f);
        }
        return (float)(1.0 - 25.0 * ((double)phase - 0.2) * ((double)phase - 0.2));
    }
}

