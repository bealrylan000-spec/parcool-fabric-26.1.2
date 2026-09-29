package com.alrex.parcool.client.animation.impl;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.PlayerModelRotator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.action.impl.Dodge;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.config.ParCoolConfig;
import com.alrex.parcool.utilities.Easing;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import com.alrex.parcool.platform.event.client.RenderFrameEvent;
import com.alrex.parcool.platform.event.client.ViewportEvent;

public class DodgeAnimator
extends Animator {
    final Dodge.DodgeDirection direction;

    @Override
    public boolean shouldRemoved(Player player, Parkourability parkourability) {
        return this.getTick() >= 11;
    }

    public DodgeAnimator(Dodge.DodgeDirection dodgeDirection) {
        this.direction = dodgeDirection;
    }

    @Override
    public void animatePost(Player player, Parkourability parkourability, PlayerModelTransformer transformer) {
        float phase = ((float)this.getTick() + transformer.getPartialTick()) / 11.0f;
        if (phase > 1.0f) {
            return;
        }
        float animFactor = new Easing(phase).squareOut(0.0f, 0.25f, 0.0f, 1.0f).linear(0.25f, 0.75f, 1.0f, 1.0f).sinInOut(0.7f, 1.0f, 1.0f, 0.0f).get();
        switch (this.direction) {
            case Right: {
                float leftArmXFactor = new Easing(phase).sinInOut(0.0f, 0.4f, 0.0f, 1.0f).linear(0.4f, 0.7f, 1.0f, 1.0f).sinInOut(0.7f, 1.0f, 1.0f, 0.0f).get();
                float rightArmXFactor = new Easing(phase).sinInOut(0.0f, 0.3f, 0.0f, -0.8f).sinInOut(0.3f, 0.6f, -0.8f, 1.0f).sinInOut(0.6f, 1.0f, 1.0f, 0.0f).get();
                float rightArmZFactor = new Easing(phase).linear(0.0f, 0.3f, 0.0f, 0.0f).sinInOut(0.4f, 0.7f, 0.0f, 1.0f).sinInOut(0.7f, 1.0f, 1.0f, 0.0f).get();
                float leftArmZFactor = new Easing(phase).sinInOut(0.0f, 0.5f, 0.0f, 1.0f).sinInOut(0.5f, 1.0f, 1.0f, 0.0f).get();
                float rightLegXFactor = new Easing(phase).sinInOut(0.0f, 0.35f, 0.0f, 1.0f).linear(0.35f, 0.65f, 1.0f, 1.0f).sinInOut(0.65f, 1.0f, 1.0f, 0.0f).get();
                float rightLegZFactor = new Easing(phase).linear(0.0f, 0.35f, 0.0f, -0.9f).sinInOut(0.35f, 0.75f, -0.9f, 1.0f).sinInOut(0.75f, 1.0f, 1.0f, 0.0f).get();
                float leftLegXFactor = new Easing(phase).linear(0.0f, 0.2f, 0.0f, 0.0f).sinInOut(0.2f, 0.6f, 0.0f, 1.0f).sinInOut(0.6f, 1.0f, 1.0f, 0.0f).get();
                float leftLegZFactor = new Easing(phase).linear(0.0f, 0.3f, 0.0f, -0.5f).sinInOut(0.3f, 0.6f, -0.5f, 0.8f).sinInOut(0.6f, 1.0f, 0.8f, 0.0f).get();
                float headYawFactor = new Easing(phase).sinInOut(0.0f, 0.5f, 0.0f, 1.0f).sinInOut(0.5f, 1.0f, 1.0f, 0.0f).get();
                float headPitchFactor = new Easing(phase).sinInOut(0.0f, 0.5f, 0.0f, 1.0f).sinInOut(0.5f, 1.0f, 1.0f, 0.0f).get();
                transformer.rotateRightLeg((float)Math.toRadians(-85.0f * rightLegXFactor), 0.0f, (float)Math.toRadians(30.0f * rightLegZFactor), animFactor).rotateLeftLeg((float)Math.toRadians(-85.0f * leftLegXFactor + 15.0f), 0.0f, (float)Math.toRadians(30.0f * leftLegZFactor), animFactor).rotateRightArm((float)Math.toRadians(-5.0f + 45.0f * rightArmXFactor), 0.0f, (float)Math.toRadians(-20.0f * rightArmZFactor), animFactor).rotateLeftArm((float)Math.toRadians(-60.0f * leftArmXFactor), 0.0f, (float)Math.toRadians(40.0f * leftArmZFactor), animFactor).rotateAdditionallyHeadPitch(40.0f * headPitchFactor).rotateAdditionallyHeadYaw(-30.0f * headYawFactor).end();
                break;
            }
            case Left: {
                float rightArmXFactor = new Easing(phase).sinInOut(0.0f, 0.4f, 0.0f, 1.0f).linear(0.4f, 0.7f, 1.0f, 1.0f).sinInOut(0.7f, 1.0f, 1.0f, 0.0f).get();
                float leftArmXFactor = new Easing(phase).sinInOut(0.0f, 0.3f, 0.0f, -0.8f).sinInOut(0.3f, 0.6f, -0.8f, 1.0f).sinInOut(0.6f, 1.0f, 1.0f, 0.0f).get();
                float leftArmZFactor = new Easing(phase).linear(0.0f, 0.3f, 0.0f, 0.0f).sinInOut(0.4f, 0.7f, 0.0f, 1.0f).sinInOut(0.7f, 1.0f, 1.0f, 0.0f).get();
                float rightArmZFactor = new Easing(phase).sinInOut(0.0f, 0.5f, 0.0f, 1.0f).sinInOut(0.5f, 1.0f, 1.0f, 0.0f).get();
                float leftLegXFactor = new Easing(phase).sinInOut(0.0f, 0.35f, 0.0f, 1.0f).linear(0.35f, 0.65f, 1.0f, 1.0f).sinInOut(0.65f, 1.0f, 1.0f, 0.0f).get();
                float leftLegZFactor = new Easing(phase).linear(0.0f, 0.5f, 0.0f, 0.2f).sinInOut(0.5f, 0.75f, 0.2f, 1.0f).sinInOut(0.75f, 1.0f, 1.0f, 0.0f).get();
                float rightLegXFactor = new Easing(phase).linear(0.0f, 0.2f, 0.0f, 0.0f).sinInOut(0.2f, 0.6f, 0.0f, 1.0f).sinInOut(0.6f, 1.0f, 1.0f, 0.0f).get();
                float rightLegZFactor = new Easing(phase).squareOut(0.0f, 0.4f, 0.0f, 1.0f).sinInOut(0.4f, 0.75f, 1.0f, 0.2f).linear(0.75f, 1.0f, 0.2f, 0.0f).get();
                float headYawFactor = new Easing(phase).sinInOut(0.0f, 0.5f, 0.0f, 1.0f).sinInOut(0.5f, 1.0f, 1.0f, 0.0f).get();
                float headPitchFactor = new Easing(phase).sinInOut(0.0f, 0.5f, 0.0f, 1.0f).sinInOut(0.5f, 1.0f, 1.0f, 0.0f).get();
                transformer.rotateRightLeg((float)Math.toRadians(-85.0f * rightLegXFactor + 15.0f), 0.0f, (float)Math.toRadians(30.0f * rightLegZFactor), animFactor).rotateLeftLeg((float)Math.toRadians(-85.0f * leftLegXFactor), 0.0f, (float)Math.toRadians(-35.0f * leftLegZFactor), animFactor).rotateRightArm((float)Math.toRadians(-60.0f * rightArmXFactor), 0.0f, (float)Math.toRadians(40.0f * rightArmZFactor), animFactor).rotateLeftArm((float)Math.toRadians(-5.0f + 45.0f * leftArmXFactor), 0.0f, (float)Math.toRadians(-20.0f * leftArmZFactor), animFactor).rotateAdditionallyHeadPitch(40.0f * headPitchFactor).rotateAdditionallyHeadYaw(30.0f * headYawFactor).end();
                break;
            }
            case Front: {
                float rightLegXFactor = new Easing(phase).sinInOut(0.0f, 0.2f, 0.0f, -0.3f).sinInOut(0.2f, 0.4f, -0.3f, 1.0f).sinInOut(0.4f, 0.85f, 1.0f, -0.3f).sinInOut(0.85f, 1.0f, -0.3f, 0.0f).get();
                float leftLegXFactor = new Easing(phase).sinInOut(0.0f, 0.1f, 0.0f, -0.3f).sinInOut(0.1f, 0.3f, -0.3f, 1.0f).sinInOut(0.3f, 0.75f, 1.0f, -0.3f).sinInOut(0.75f, 1.0f, -0.3f, 0.0f).get();
                float armXFactor = new Easing(phase).squareOut(0.0f, 0.2f, -0.1f, 1.0f).sinInOut(0.0f, 1.0f, 1.0f, 0.0f).get();
                float headPitchFactor = new Easing(phase).sinInOut(0.0f, 0.1f, 0.0f, -0.2f).sinInOut(0.1f, 0.25f, -0.2f, 1.0f).sinInOut(0.25f, 1.0f, 1.0f, 0.0f).get();
                float armZFactor = new Easing(phase).sinInOut(0.0f, 0.4f, 0.0f, 1.0f).sinInOut(0.4f, 1.0f, 1.0f, 0.0f).get();
                transformer.translateLeftArm(-0.8f * armZFactor, 0.0f, 0.0f).translateRightArm(0.8f * armZFactor, 0.0f, 0.0f).rotateRightLeg((float)Math.toRadians(rightLegXFactor * -70.0f), 0.0f, 0.0f, animFactor).rotateLeftLeg((float)Math.toRadians(leftLegXFactor * -70.0f), 0.0f, 0.0f, animFactor).rotateRightArm((float)Math.toRadians(-170.0f * armXFactor), 0.0f, (float)Math.toRadians(15.0f * armZFactor), animFactor).rotateLeftArm((float)Math.toRadians(-170.0f * armXFactor), 0.0f, (float)Math.toRadians(-15.0f * armZFactor), animFactor).rotateAdditionallyHeadPitch(40.0f * headPitchFactor).end();
                break;
            }
            case Back: {
                float leftLegXFactor = new Easing(phase).sinInOut(0.0f, 0.2f, 0.0f, -0.3f).sinInOut(0.2f, 0.4f, -0.3f, 1.0f).sinInOut(0.4f, 0.85f, 1.0f, -0.3f).sinInOut(0.85f, 1.0f, -0.3f, 0.0f).get();
                float rightLegXFactor = new Easing(phase).sinInOut(0.0f, 0.1f, 0.0f, -0.3f).sinInOut(0.1f, 0.4f, -0.3f, 1.0f).sinInOut(0.4f, 0.8f, 1.0f, -0.3f).sinInOut(0.8f, 1.0f, -0.3f, 0.0f).get();
                float armXFactor = new Easing(phase).sinInOut(0.0f, 0.1f, 0.0f, -0.2f).sinInOut(0.1f, 0.4f, -0.2f, 1.0f).sinInOut(0.4f, 1.0f, 1.0f, 0.0f).get();
                float headPitchFactor = new Easing(phase).sinInOut(0.0f, 0.1f, 0.0f, -0.2f).sinInOut(0.1f, 0.35f, -0.2f, 1.0f).sinInOut(0.35f, 1.0f, 1.0f, 0.0f).get();
                float armZFactor = new Easing(phase).sinInOut(0.0f, 0.4f, 0.0f, 1.0f).sinInOut(0.4f, 1.0f, 1.0f, 0.0f).get();
                transformer.rotateRightLeg((float)Math.toRadians(rightLegXFactor * 70.0f), 0.0f, 0.0f, animFactor).rotateLeftLeg((float)Math.toRadians(leftLegXFactor * 70.0f), 0.0f, 0.0f, animFactor).rotateRightArm((float)Math.toRadians(-180.0f * armXFactor), 0.0f, (float)Math.toRadians(10.0f * armZFactor), animFactor).rotateLeftArm((float)Math.toRadians(-180.0f * armXFactor), 0.0f, (float)Math.toRadians(-10.0f * armZFactor), animFactor).rotateAdditionallyHeadPitch(-40.0f * headPitchFactor).end();
                break;
            }
        }
    }

    @Override
    public void rotatePost(Player player, Parkourability parkourability, PlayerModelRotator rotator) {
        float phase = ((float)this.getTick() + rotator.getPartialTick()) / 11.0f;
        if (phase > 1.0f) {
            return;
        }
        switch (this.direction) {
            case Front: {
                float bodyPitchFactor = new Easing(phase).squareOut(0.0f, 1.0f, 0.0f, 1.0f).get();
                float translateYFactor = new Easing(phase).squareOut(0.0f, 0.4f, 0.0f, 1.0f).sinInOut(0.4f, 1.0f, 1.0f, 0.0f).get();
                rotator.startBasedCenter().translateY(-translateYFactor * player.getBbHeight() / 4.0f).rotatePitchFrontward(360.0f * bodyPitchFactor).end();
                break;
            }
            case Right: 
            case Left: {
                float bodyPitchFactor = new Easing(phase).squareOut(0.0f, 0.25f, 0.0f, 1.0f).linear(0.25f, 0.4f, 1.0f, 1.0f).sinInOut(0.4f, 1.0f, 1.0f, 0.0f).get();
                float bodyYawFactor = new Easing(phase).squareIn(0.0f, 0.15f, 0.0f, 0.2f).squareOut(0.15f, 1.0f, 0.2f, 1.0f).get();
                float yawFactor = new Easing(phase).sinInOut(0.0f, 0.15f, 0.0f, -1.0f).sinInOut(0.15f, 0.8f, -1.0f, 1.0f).sinInOut(0.8f, 1.0f, 1.0f, 0.0f).get();
                float yTranslateFactor = new Easing(phase).squareOut(0.0f, 0.3f, 0.0f, 1.0f).linear(0.3f, 0.5f, 1.0f, 1.0f).sinInOut(0.5f, 1.0f, 1.0f, 0.0f).get();
                if (this.direction == Dodge.DodgeDirection.Left) {
                    rotator.startBasedCenter().translateY(-yTranslateFactor * player.getBbHeight() / 3.0f).rotateYawRightward(25.0f * yawFactor - 4.0f).rotatePitchFrontward(105.0f * bodyPitchFactor).rotateYawRightward(360.0f * bodyYawFactor).end();
                    break;
                }
                rotator.startBasedCenter().translateY(-yTranslateFactor * player.getBbHeight() / 3.0f).rotateYawRightward(-25.0f * yawFactor + 4.0f).rotatePitchFrontward(105.0f * bodyPitchFactor).rotateYawRightward(-360.0f * bodyYawFactor).end();
                break;
            }
            case Back: {
                float bodyPitchFactor = new Easing(phase).linear(0.0f, 0.4f, 0.0f, 0.4f).sinInOut(0.4f, 1.0f, 0.4f, 1.0f).get();
                rotator.startBasedCenter().rotatePitchFrontward(-360.0f * bodyPitchFactor).end();
                break;
            }
        }
    }

    @Override
    public void onCameraSetUp(ViewportEvent.ComputeCameraAngles event, Player clientPlayer, Parkourability parkourability) {
        if (!(clientPlayer.isLocalPlayer() && Minecraft.getInstance().options.getCameraType().isFirstPerson() && ParCoolConfig.Client.Booleans.EnableCameraAnimationOfDodge.get().booleanValue())) {
            return;
        }
        float phase = (float)(((double)this.getTick() + event.getPartialTick()) / 11.0);
        switch (this.direction) {
            case Front: {
                float bodyPitchFactor = new Easing(phase).sinInOut(0.0f, 1.0f, 0.0f, 1.0f).get();
                event.setPitch(event.getPitch() + 360.0f * bodyPitchFactor);
                break;
            }
            case Right: 
            case Left: {
                float rollFactor = new Easing(phase).squareOut(0.0f, 1.0f, 0.0f, 1.0f).get();
                if (this.direction == Dodge.DodgeDirection.Right) {
                    event.setRoll(event.getRoll() + 360.0f * rollFactor);
                    break;
                }
                event.setRoll(event.getRoll() - 360.0f * rollFactor);
                break;
            }
            case Back: {
                float bodyPitchFactor = new Easing(phase).sinInOut(0.0f, 1.0f, 0.0f, 1.0f).get();
                event.setPitch(event.getPitch() - 360.0f * bodyPitchFactor);
                break;
            }
        }
    }

    @Override
    public void onRenderTick(RenderFrameEvent event, Player player, Parkourability parkourability) {
        switch (this.direction) {
            case Right: {
                player.setYBodyRot(player.getYHeadRot() - 5.0f);
                break;
            }
            case Left: {
                player.setYBodyRot(player.getYHeadRot() + 5.0f);
            }
        }
    }
}

