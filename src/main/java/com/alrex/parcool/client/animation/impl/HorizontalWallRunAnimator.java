package com.alrex.parcool.client.animation.impl;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.PlayerModelRotator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.action.impl.HorizontalWallRun;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.config.ParCoolConfig;
import com.alrex.parcool.utilities.MathUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import com.alrex.parcool.platform.event.client.ViewportEvent;

public class HorizontalWallRunAnimator
extends Animator {
    final boolean wallIsRightSide;
    private float limbSwing = 0.0f;

    public HorizontalWallRunAnimator(boolean wallIsRightSide) {
        this.wallIsRightSide = wallIsRightSide;
    }

    @Override
    public boolean shouldRemoved(Player player, Parkourability parkourability) {
        return !parkourability.get(HorizontalWallRun.class).isDoing();
    }

    @Override
    public void animatePost(Player player, Parkourability parkourability, PlayerModelTransformer transformer) {
        this.limbSwing = transformer.getLimbSwing();
        float factor = this.getFactor((float)this.getTick() + transformer.getPartialTick());
        float angle = factor * 15.0f * (this.wallIsRightSide ? -1.0f : 1.0f);
        float armSwingPhase = this.limbSwing * 0.6662f;
        transformer.rotateAdditionallyHeadPitch(-15.0f * factor).rotateAdditionallyHeadRoll(angle);
        if (this.wallIsRightSide) {
            transformer.addRotateLeftArm(0.0f, 0.0f, (float)Math.toRadians(-30.0)).makeArmsNatural().rotateRightArm((float)Math.toRadians(20.0 - 8.0 * Math.cos(armSwingPhase)), 0.0f, (float)Math.toRadians(110.0)).translateRightArm(-1.0f, 0.0f, 0.8f - 0.5f * Mth.cos((float)armSwingPhase)).addRotateLeftArm((float)Math.toRadians(-10.0), 0.0f, (float)(-Math.toRadians(35.0 + 5.0 * Math.sin(armSwingPhase))), factor).translateLeftArm(0.0f, 1.0f, 0.0f).addRotateRightLeg(0.0f, 0.0f, (float)Math.toRadians(17.0)).addRotateLeftLeg(0.0f, 0.0f, (float)Math.toRadians(25.0)).rotateAdditionallyHeadYaw(-5.0f + 8.0f * Mth.cos((float)armSwingPhase)).end();
        } else {
            transformer.addRotateRightArm(0.0f, 0.0f, (float)Math.toRadians(30.0)).makeArmsNatural().rotateLeftArm((float)Math.toRadians(20.0 - 8.0 * Math.cos(armSwingPhase)), 0.0f, (float)Math.toRadians(-110.0)).translateLeftArm(1.0f, 0.0f, 0.8f - 0.5f * Mth.cos((float)armSwingPhase)).addRotateRightArm((float)Math.toRadians(-10.0), 0.0f, (float)Math.toRadians(35.0 + 5.0 * Math.sin(armSwingPhase)), factor).translateRightArm(0.0f, 1.0f, 0.0f).addRotateRightLeg(0.0f, 0.0f, (float)Math.toRadians(-25.0)).addRotateLeftLeg(0.0f, 0.0f, (float)Math.toRadians(-17.0)).rotateAdditionallyHeadYaw(5.0f - 8.0f * Mth.cos((float)armSwingPhase)).end();
        }
    }

    private float getFactor(float tick) {
        return tick < 5.0f ? 1.0f - MathUtil.squaring((5.0f - tick) / 5.0f) : 1.0f;
    }

    @Override
    public void rotatePost(Player player, Parkourability parkourability, PlayerModelRotator rotator) {
        float factor = this.getFactor((float)this.getTick() + rotator.getPartialTick());
        float sign = this.wallIsRightSide ? -1.0f : 1.0f;
        float angle = factor * 30.0f * sign;
        float yOffset = 0.145f * (float)Math.pow(Math.cos((double)this.limbSwing * 0.6662), 2.0);
        rotator.translateY(yOffset).startBasedCenter().rotateRollRightward(angle).rotatePitchFrontward(20.0f * factor).rotateYawRightward(sign * (-5.0f + 8.0f * Mth.cos((float)(this.limbSwing * 0.66662f)))).end();
    }

    @Override
    public void onCameraSetUp(ViewportEvent.ComputeCameraAngles event, Player clientPlayer, Parkourability parkourability) {
        if (!Minecraft.getInstance().options.getCameraType().isFirstPerson() || !ParCoolConfig.Client.Booleans.EnableCameraAnimationOfHWallRun.get().booleanValue()) {
            return;
        }
        float factor = this.getFactor((float)((double)this.getTick() + event.getPartialTick()));
        float angle = factor * 20.0f * (float)(this.wallIsRightSide ? -1 : 1);
        event.setRoll(angle);
    }
}

