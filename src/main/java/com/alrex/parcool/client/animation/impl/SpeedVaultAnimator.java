package com.alrex.parcool.client.animation.impl;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.PlayerModelRotator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.config.ParCoolConfig;
import com.alrex.parcool.utilities.Easing;
import com.alrex.parcool.utilities.EasingFunctions;
import com.alrex.parcool.utilities.MathUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import com.alrex.parcool.platform.event.client.ViewportEvent;

public class SpeedVaultAnimator
extends Animator {
    private final Type type;

    @Override
    public boolean shouldRemoved(Player player, Parkourability parkourability) {
        return this.getTick() >= 11;
    }

    private float getFactor(float tick) {
        float phase = tick / 11.0f;
        if ((double)phase < 0.5) {
            return EasingFunctions.SinInOutBySquare(phase * 2.0f);
        }
        return EasingFunctions.SinInOutBySquare(2.0f - phase * 2.0f);
    }

    @Override
    public void rotatePost(Player player, Parkourability parkourability, PlayerModelRotator rotator) {
        float phase = ((float)this.getTick() + rotator.getPartialTick()) / 11.0f;
        float factor = this.getFactor((float)this.getTick() + rotator.getPartialTick());
        float forwardFactor = (float)Math.sin((double)(phase * 2.0f) * Math.PI) + 0.5f;
        float yFactor = new Easing(phase).squareOut(0.0f, 0.5f, 0.0f, 1.0f).squareIn(0.5f, 1.0f, 1.0f, 0.0f).get();
        rotator.startBasedCenter().translateY(-yFactor * player.getBbHeight() / 5.0f).rotateRollRightward(factor * 60.0f * (float)(this.type == Type.Right ? -1 : 1)).rotatePitchFrontward(30.0f * forwardFactor).end();
    }

    @Override
    public void animatePost(Player player, Parkourability parkourability, PlayerModelTransformer transformer) {
        float phase = ((float)this.getTick() + transformer.getPartialTick()) / 11.0f;
        float animFactor = new Easing(phase).sinInOut(0.0f, 0.25f, 0.0f, 1.0f).linear(0.25f, 0.75f, 1.0f, 1.0f).sinInOut(0.75f, 1.0f, 1.0f, 0.0f).get();
        float freeArmXFactor = new Easing(phase).squareOut(0.0f, 0.65f, 1.0f, -1.0f).sinInOut(0.65f, 1.0f, -1.0f, 0.0f).get();
        float freeArmZFactor = new Easing(phase).squareOut(0.0f, 0.4f, 0.0f, 1.0f).sinInOut(0.4f, 0.65f, 1.0f, -0.3f).sinInOut(0.65f, 1.0f, -0.3f, 0.0f).get();
        float factor = this.getFactor((float)this.getTick() + transformer.getPartialTick());
        switch (this.type.ordinal()) {
            case 0: {
                transformer.rotateLeftArm((float)Math.toRadians(MathUtil.lerp(-45.0f, 45.0f, phase)), 0.0f, (float)(-Math.toRadians(factor * 70.0f))).rotateRightArm((float)Math.toRadians(20.0f + freeArmXFactor * 50.0f), 0.0f, (float)Math.toRadians(freeArmZFactor * 40.0f), animFactor).addRotateRightLeg(0.0f, 0.0f, (float)Math.toRadians(factor * 30.0f)).addRotateLeftLeg(0.0f, 0.0f, (float)Math.toRadians(factor * 20.0f)).rotateAdditionallyHeadRoll(factor * -15.0f).end();
                break;
            }
            case 1: {
                transformer.rotateRightArm((float)Math.toRadians(MathUtil.lerp(-45.0f, 45.0f, phase)), 0.0f, (float)Math.toRadians(factor * 70.0f)).rotateLeftArm((float)Math.toRadians(20.0f + freeArmXFactor * 50.0f), 0.0f, (float)Math.toRadians(freeArmZFactor * -40.0f), animFactor).addRotateRightLeg(0.0f, 0.0f, (float)Math.toRadians(factor * -20.0f)).addRotateLeftLeg(0.0f, 0.0f, (float)Math.toRadians(factor * -30.0f)).rotateAdditionallyHeadRoll(factor * 35.0f).end();
            }
        }
    }

    @Override
    public void onCameraSetUp(ViewportEvent.ComputeCameraAngles event, Player clientPlayer, Parkourability parkourability) {
        if (!Minecraft.getInstance().options.getCameraType().isFirstPerson() || !ParCoolConfig.Client.Booleans.EnableCameraAnimationOfVault.get().booleanValue()) {
            return;
        }
        float factor = this.getFactor((float)((double)this.getTick() + event.getPartialTick()));
        float phase = (float)(((double)this.getTick() + event.getPartialTick()) / 11.0);
        float forwardFactor = (float)Math.sin((double)(phase * 2.0f) * Math.PI) + 0.5f;
        event.setPitch(15.0f * forwardFactor);
        switch (this.type.ordinal()) {
            case 0: {
                event.setRoll(-25.0f * factor);
                break;
            }
            case 1: {
                event.setRoll(25.0f * factor);
            }
        }
    }

    public SpeedVaultAnimator(Type type) {
        this.type = type;
    }

    public static enum Type {
        Right,
        Left;

    }
}

