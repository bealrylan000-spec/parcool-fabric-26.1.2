package com.alrex.parcool.client.animation.impl;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.PlayerModelRotator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.config.ParCoolConfig;
import com.alrex.parcool.utilities.EasingFunctions;
import com.alrex.parcool.utilities.MathUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import com.alrex.parcool.platform.event.client.ViewportEvent;

public class BackwardWallJumpAnimator
extends Animator {
    private final int maxTick = 12;

    @Override
    public boolean shouldRemoved(Player player, Parkourability parkourability) {
        return this.getTick() >= 12;
    }

    private float armAngleXFactorBack(float phase) {
        if ((double)phase < 0.1) {
            return 1.0f - 100.0f * (phase - 0.1f) * (phase - 0.1f);
        }
        if ((double)phase < 0.6) {
            return 1.0f - EasingFunctions.SinInOutBySquare((phase - 0.1f) * 2.0f);
        }
        return 0.1f * EasingFunctions.SinInOutBySquare((phase - 0.6f) * 2.5f);
    }

    private float legAngleFactorBack(float phase) {
        if ((double)phase < 0.5) {
            return 8.0f * (phase - 0.25f) * (phase - 0.25f);
        }
        return 1.0f - 8.0f * (phase - 0.75f) * (phase - 0.75f);
    }

    private float armAngleZFactor(float phase) {
        if ((double)phase < 0.5) {
            return phase * 0.4f;
        }
        if ((double)phase < 0.75) {
            return 0.2f + 0.8f * EasingFunctions.CubicInOut((phase - 0.5f) * 4.0f);
        }
        return 1.0f - EasingFunctions.CubicInOut((phase - 0.75f) * 4.0f);
    }

    private float angleFactor(float phase) {
        return EasingFunctions.SinInOutBySquare(phase);
    }

    private float getAnimationFactor(float phase) {
        if ((double)phase < 0.3) {
            return EasingFunctions.SinInOutBySquare(phase * 3.33f);
        }
        if ((double)phase > 0.7) {
            return 1.0f - EasingFunctions.SinInOutBySquare((phase - 0.7f) * 3.33f);
        }
        return 1.0f;
    }

    @Override
    public void animatePost(Player player, Parkourability parkourability, PlayerModelTransformer transformer) {
        float phase = ((float)this.getTick() + transformer.getPartialTick()) / 12.0f;
        float armAngleX = MathUtil.lerp(20.0f, -190.0f, this.armAngleXFactorBack(phase));
        float armAngleZ = MathUtil.lerp((double)phase > 0.75 ? 0.0f : 14.0f, 28.0f, this.armAngleZFactor(phase));
        float legAngle = MathUtil.lerp(35.0f, -35.0f, this.legAngleFactorBack(phase));
        float headAngle = MathUtil.lerp(0.0f, -45.0f, 1.0f - 4.0f * (phase - 0.5f) * (phase - 0.5f));
        float animationFactor = this.getAnimationFactor(phase);
        transformer.rotateAdditionallyHeadPitch(headAngle).rotateRightArm((float)Math.toRadians(armAngleX), 0.0f, (float)Math.toRadians(armAngleZ), animationFactor).rotateLeftArm((float)Math.toRadians(armAngleX), 0.0f, (float)(-Math.toRadians(armAngleZ)), animationFactor).makeArmsNatural().rotateRightLeg((float)Math.toRadians(legAngle - 15.0f), 0.0f, 0.0f, animationFactor).rotateLeftLeg((float)Math.toRadians(legAngle + 15.0f), 0.0f, 0.0f, animationFactor).makeLegsLittleMoving().end();
    }

    @Override
    public void rotatePost(Player player, Parkourability parkourability, PlayerModelRotator rotator) {
        float phase = ((float)this.getTick() + rotator.getPartialTick()) / 12.0f;
        float factor = this.angleFactor(phase);
        rotator.startBasedCenter().rotatePitchFrontward(factor * -360.0f).end();
    }

    @Override
    public void onCameraSetUp(ViewportEvent.ComputeCameraAngles event, Player clientPlayer, Parkourability parkourability) {
        if (!(clientPlayer.isLocalPlayer() && Minecraft.getInstance().options.getCameraType().isFirstPerson() && ParCoolConfig.Client.Booleans.EnableCameraAnimationOfBackWallJump.get().booleanValue())) {
            return;
        }
        float phase = (float)(((double)this.getTick() + event.getPartialTick()) / 12.0);
        float factor = this.angleFactor(phase);
        event.setPitch(clientPlayer.getViewXRot((float)event.getPartialTick()) - factor * 360.0f + (float)((double)phase > 0.5 ? 360 : 0));
    }
}

