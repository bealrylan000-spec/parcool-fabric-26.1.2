package com.alrex.parcool.client.animation.impl;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.PlayerModelRotator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.config.ParCoolConfig;
import com.alrex.parcool.utilities.Easing;
import com.alrex.parcool.utilities.EasingFunctions;
import com.alrex.parcool.utilities.MathUtil;
import com.alrex.parcool.utilities.VectorUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import com.alrex.parcool.platform.event.client.ViewportEvent;

public class KongVaultAnimator
extends Animator {
    private float yRotDifference = 0.0f;
    private float yRotDifferenceOld = 0.0f;

    float getFactor(float phase) {
        if ((double)phase < 0.5) {
            return EasingFunctions.SinInOutBySquare(phase * 2.0f);
        }
        return EasingFunctions.SinInOutBySquare(2.0f - phase * 2.0f);
    }

    float getArmFactor(float phase) {
        return (double)phase < 0.2 ? 1.0f - 25.0f * (phase - 0.2f) * (phase - 0.2f) : 1.0f - EasingFunctions.SinInOutBySquare((phase - 0.2f) * 1.25f);
    }

    @Override
    public void tick(Player player) {
        super.tick(player);
        this.yRotDifferenceOld = this.yRotDifference;
        Vec3 currentAngle = VectorUtil.fromYawDegree(player.yBodyRot);
        Vec3 oldAngle = VectorUtil.fromYawDegree(player.yBodyRotO);
        this.yRotDifference = (float)Math.atan((oldAngle.x() * currentAngle.z() - currentAngle.x() * oldAngle.z()) / (currentAngle.x() * oldAngle.x() + currentAngle.z() * oldAngle.z()));
    }

    @Override
    public boolean shouldRemoved(Player player, Parkourability parkourability) {
        return this.getTick() >= 11;
    }

    @Override
    public void animatePost(Player player, Parkourability parkourability, PlayerModelTransformer transformer) {
        float phase = ((float)this.getTick() + transformer.getPartialTick()) / 11.0f;
        float armFactor = this.getArmFactor(phase);
        float factor = this.getFactor(phase);
        float animFactor = new Easing(phase).sinInOut(0.0f, 0.15f, 0.0f, 1.0f).linear(0.15f, 0.85f, 1.0f, 1.0f).sinInOut(0.85f, 1.0f, 1.0f, 0.0f).get();
        float difference = MathUtil.lerp(this.yRotDifferenceOld, this.yRotDifference, transformer.getPartialTick());
        transformer.translateLeftLeg(0.0f, -0.7f * factor, -0.9f * factor).translateRightLeg(0.0f, -0.7f * factor, -0.9f * factor).rotateAdditionallyHeadPitch(-40.0f * armFactor).rotateRightArm((float)Math.toRadians(30.0f - 195.0f * armFactor), 0.0f, (float)Math.toRadians(30.0f - 30.0f * armFactor), animFactor).rotateLeftArm((float)Math.toRadians(25.0f - 195.0f * armFactor), 0.0f, (float)Math.toRadians(-30.0f + 30.0f * armFactor), animFactor).rotateRightLeg((float)Math.toRadians(Easing.with(phase).squareOut(0.0f, 0.1f, 5.0f, -5.0f).sinInOut(0.1f, 0.47f, -5.0f, 25.0f).sinInOut(0.47f, 0.9f, 25.0f, -25.0f).sinInOut(0.9f, 1.0f, -25.0f, 0.0f).get()), 0.0f, difference - (float)Math.toRadians(5.0 * (double)factor), animFactor).rotateLeftLeg((float)Math.toRadians(Easing.with(phase).sinInOut(0.0f, 0.33f, -20.0f, 20.0f).sinInOut(0.33f, 0.79f, 20.0f, -45.0f).sinInOut(0.79f, 1.0f, -45.0f, 0.0f).get()), 0.0f, difference + (float)Math.toRadians(5.0 * (double)factor), animFactor).makeLegsLittleMoving().end();
    }

    @Override
    public void rotatePost(Player player, Parkourability parkourability, PlayerModelRotator rotator) {
        float phase = ((float)this.getTick() + rotator.getPartialTick()) / 11.0f;
        float factor = this.getFactor(phase);
        float yFactor = new Easing(phase).squareOut(0.0f, 0.5f, 0.0f, 1.0f).squareIn(0.5f, 1.0f, 1.0f, 0.0f).get();
        rotator.startBasedCenter().translateY(-yFactor * player.getBbHeight() / 5.0f).rotatePitchFrontward(factor * 95.0f).end();
    }

    @Override
    public void onCameraSetUp(ViewportEvent.ComputeCameraAngles event, Player clientPlayer, Parkourability parkourability) {
        if (!Minecraft.getInstance().options.getCameraType().isFirstPerson() || !ParCoolConfig.Client.Booleans.EnableCameraAnimationOfVault.get().booleanValue()) {
            return;
        }
        float phase = (float)(((double)this.getTick() + event.getPartialTick()) / 11.0);
        float factor = this.getFactor(phase);
        event.setPitch(30.0f * factor + clientPlayer.getViewXRot((float)event.getPartialTick()));
    }
}

