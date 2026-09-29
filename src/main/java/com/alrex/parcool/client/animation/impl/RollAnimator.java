package com.alrex.parcool.client.animation.impl;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.PlayerModelRotator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.action.impl.Roll;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.config.ParCoolConfig;
import com.alrex.parcool.utilities.Easing;
import com.alrex.parcool.utilities.MathUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import com.alrex.parcool.platform.event.client.RenderFrameEvent;
import com.alrex.parcool.platform.event.client.ViewportEvent;

public class RollAnimator
extends Animator {
    private final Roll.Direction direction;

    public RollAnimator(Roll.Direction direction) {
        this.direction = direction;
    }

    private static float calculateMovementFactor(float progress) {
        return -MathUtil.squaring(progress - 1.0f) + 1.0f;
    }

    @Override
    public boolean shouldRemoved(Player player, Parkourability parkourability) {
        return !parkourability.get(Roll.class).isDoing();
    }

    @Override
    public void animatePost(Player player, Parkourability parkourability, PlayerModelTransformer transformer) {
        switch (this.direction) {
            case Front: 
            case Back: {
                this.animatePostFrontBack(player, parkourability, transformer);
                break;
            }
            case Right: 
            case Left: {
                this.animatePostLeftRight(player, parkourability, transformer);
            }
        }
    }

    void animatePostFrontBack(Player player, Parkourability parkourability, PlayerModelTransformer transformer) {
        Roll roll = parkourability.get(Roll.class);
        float phase = ((float)this.getTick() + transformer.getPartialTick()) / (float)roll.getRollMaxTick();
        if (phase > 1.0f) {
            return;
        }
        float factor = 1.0f - 4.0f * (0.5f - phase) * (0.5f - phase);
        float animationFactor = new Easing(phase).squareOut(0.0f, 0.1f, 0.0f, 1.0f).linear(0.0f, 0.8f, 1.0f, 1.0f).sinInOut(0.8f, 1.0f, 1.0f, 0.0f).get();
        transformer.addRotateLeftLeg((float)Math.toRadians(-70.0f * factor), 0.0f, 0.0f, animationFactor).addRotateRightLeg((float)Math.toRadians(-70.0f * factor), 0.0f, 0.0f, animationFactor).addRotateRightArm((float)Math.toRadians(-80.0f * factor), 0.0f, 0.0f, animationFactor).addRotateLeftArm((float)Math.toRadians(-80.0f * factor), 0.0f, 0.0f, animationFactor).end();
    }

    void animatePostLeftRight(Player player, Parkourability parkourability, PlayerModelTransformer transformer) {
        Roll roll = parkourability.get(Roll.class);
        float phase = ((float)this.getTick() + transformer.getPartialTick()) / (float)roll.getRollMaxTick();
        if (phase > 1.0f) {
            return;
        }
        float animFactor = new Easing(phase).squareOut(0.0f, 0.25f, 0.0f, 1.0f).linear(0.25f, 0.75f, 1.0f, 1.0f).squareIn(0.75f, 1.0f, 1.0f, 0.0f).get();
        if (this.direction == Roll.Direction.Left) {
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
        } else {
            float leftArmXFactor = new Easing(phase).sinInOut(0.0f, 0.4f, 0.0f, 1.0f).linear(0.4f, 0.7f, 1.0f, 1.0f).sinInOut(0.7f, 1.0f, 1.0f, 0.0f).get();
            float rightArmXFactor = new Easing(phase).sinInOut(0.0f, 0.3f, 0.0f, -0.8f).sinInOut(0.3f, 0.6f, -0.8f, 1.0f).sinInOut(0.6f, 1.0f, 1.0f, 0.0f).get();
            float rightArmZFactor = new Easing(phase).linear(0.0f, 0.3f, 0.0f, 0.0f).sinInOut(0.4f, 0.7f, 0.0f, 1.0f).sinInOut(0.7f, 1.0f, 1.0f, 0.0f).get();
            float leftArmZFactor = new Easing(phase).sinInOut(0.0f, 0.5f, 0.0f, 1.0f).sinInOut(0.5f, 1.0f, 1.0f, 0.0f).get();
            float rightLegXFactor = new Easing(phase).sinInOut(0.0f, 0.35f, 0.0f, 1.0f).linear(0.35f, 0.65f, 1.0f, 1.0f).sinInOut(0.65f, 1.0f, 1.0f, 0.0f).get();
            float rightLegZFactor = new Easing(phase).linear(0.0f, 0.5f, 0.0f, 0.2f).sinInOut(0.5f, 0.75f, 0.2f, 1.0f).sinInOut(0.75f, 1.0f, 1.0f, 0.0f).get();
            float leftLegXFactor = new Easing(phase).linear(0.0f, 0.2f, 0.0f, 0.0f).sinInOut(0.2f, 0.6f, 0.0f, 1.0f).sinInOut(0.6f, 1.0f, 1.0f, 0.0f).get();
            float leftLegZFactor = new Easing(phase).squareOut(0.0f, 0.4f, 0.0f, 1.0f).sinInOut(0.4f, 0.75f, 1.0f, 0.2f).linear(0.75f, 1.0f, 0.2f, 0.0f).get();
            float headYawFactor = new Easing(phase).sinInOut(0.0f, 0.5f, 0.0f, 1.0f).sinInOut(0.5f, 1.0f, 1.0f, 0.0f).get();
            float headPitchFactor = new Easing(phase).sinInOut(0.0f, 0.5f, 0.0f, 1.0f).sinInOut(0.5f, 1.0f, 1.0f, 0.0f).get();
            transformer.rotateRightLeg((float)Math.toRadians(-85.0f * rightLegXFactor), 0.0f, (float)Math.toRadians(30.0f * rightLegZFactor), animFactor).rotateLeftLeg((float)Math.toRadians(-85.0f * leftLegXFactor + 15.0f), 0.0f, (float)Math.toRadians(-35.0f * leftLegZFactor), animFactor).rotateRightArm((float)Math.toRadians(-5.0f + 45.0f * rightArmXFactor), 0.0f, (float)Math.toRadians(-20.0f * rightArmZFactor), animFactor).rotateLeftArm((float)Math.toRadians(-60.0f * leftArmXFactor), 0.0f, (float)Math.toRadians(40.0f * leftArmZFactor), animFactor).rotateAdditionallyHeadPitch(40.0f * headPitchFactor).rotateAdditionallyHeadYaw(-30.0f * headYawFactor).end();
        }
    }

    @Override
    public void rotatePost(Player player, Parkourability parkourability, PlayerModelRotator rotator) {
        switch (this.direction) {
            case Front: 
            case Back: {
                this.rotateFrontBack(player, parkourability, rotator);
                break;
            }
            case Right: 
            case Left: {
                this.rotateLeftRight(player, parkourability, rotator);
            }
        }
    }

    private void rotateFrontBack(Player player, Parkourability parkourability, PlayerModelRotator rotator) {
        Roll roll = parkourability.get(Roll.class);
        float phase = ((float)this.getTick() + rotator.getPartialTick()) / (float)roll.getRollMaxTick();
        if (phase > 1.0f) {
            return;
        }
        float factor = RollAnimator.calculateMovementFactor(phase);
        float sign = this.direction == Roll.Direction.Front ? 1.0f : -1.0f;
        float translateYFactor = this.direction == Roll.Direction.Front ? new Easing(phase).squareOut(0.0f, 0.5f, 0.0f, 1.0f).sinInOut(0.5f, 1.0f, 1.0f, 0.0f).get() : new Easing(phase).squareOut(0.0f, 0.35f, 0.0f, 0.6f).sinInOut(0.35f, 0.9f, 0.6f, 0.0f).linear(0.9f, 1.0f, 0.0f, 0.0f).get();
        rotator.startBasedCenter().translateY(-translateYFactor * player.getBbHeight() / 4.0f).rotatePitchFrontward(sign * MathUtil.lerp(0.0f, 360.0f, factor)).end();
    }

    private void rotateLeftRight(Player player, Parkourability parkourability, PlayerModelRotator rotator) {
        float phase = ((float)this.getTick() + rotator.getPartialTick()) / (float)parkourability.get(Roll.class).getRollMaxTick();
        if (phase > 1.0f) {
            return;
        }
        switch (this.direction) {
            case Right: 
            case Left: {
                float bodyPitchFactor = new Easing(phase).squareOut(0.0f, 0.45f, 0.0f, 1.0f).linear(0.45f, 0.55f, 1.0f, 1.0f).sinInOut(0.55f, 1.0f, 1.0f, 0.0f).get();
                float bodyYawFactor = new Easing(phase).sinInOut(0.0f, 1.0f, 0.0f, 1.0f).get();
                float yawFactor = new Easing(phase).sinInOut(0.0f, 0.3f, 0.0f, -1.0f).sinInOut(0.3f, 0.7f, -1.0f, 1.0f).sinInOut(0.7f, 1.0f, 1.0f, 0.0f).get();
                float yTranslateFactor = new Easing(phase).squareOut(0.0f, 0.45f, 0.0f, 1.0f).linear(0.45f, 0.55f, 1.0f, 1.0f).sinInOut(0.55f, 1.0f, 1.0f, 0.0f).get();
                if (this.direction == Roll.Direction.Left) {
                    rotator.startBasedCenter().translateY(-yTranslateFactor * player.getBbHeight() / 3.5f).rotateYawRightward(25.0f * yawFactor - 13.0f).rotatePitchFrontward(110.0f * bodyPitchFactor).rotateYawRightward(360.0f * bodyYawFactor).end();
                    break;
                }
                rotator.startBasedCenter().translateY(-yTranslateFactor * player.getBbHeight() / 3.5f).rotateYawRightward(-25.0f * yawFactor + 13.0f).rotatePitchFrontward(110.0f * bodyPitchFactor).rotateYawRightward(-360.0f * bodyYawFactor).end();
                break;
            }
        }
    }

    @Override
    public void onCameraSetUp(ViewportEvent.ComputeCameraAngles event, Player clientPlayer, Parkourability parkourability) {
        switch (this.direction) {
            case Front: 
            case Back: {
                this.onCameraSetUpFrontBack(event, clientPlayer, parkourability);
                break;
            }
            case Right: 
            case Left: {
                this.onCameraSetUpLeftRight(event, clientPlayer, parkourability);
            }
        }
    }

    void onCameraSetUpFrontBack(ViewportEvent.ComputeCameraAngles event, Player clientPlayer, Parkourability parkourability) {
        float sign;
        Roll roll = parkourability.get(Roll.class);
        float f = sign = this.direction == Roll.Direction.Front ? 1.0f : -1.0f;
        if (roll.isDoing() && clientPlayer.isLocalPlayer() && Minecraft.getInstance().options.getCameraType().isFirstPerson() && ParCoolConfig.Client.Booleans.EnableCameraAnimationOfRolling.get().booleanValue()) {
            float factor = RollAnimator.calculateMovementFactor((float)(((double)roll.getDoingTick() + event.getPartialTick()) / (double)roll.getRollMaxTick()));
            event.setPitch(sign * ((double)factor > 0.5 ? factor - 1.0f : factor) * 360.0f + clientPlayer.getViewXRot((float)event.getPartialTick()));
        }
    }

    void onCameraSetUpLeftRight(ViewportEvent.ComputeCameraAngles event, Player clientPlayer, Parkourability parkourability) {
        float phase = (float)(((double)this.getTick() + event.getPartialTick()) / (double)parkourability.get(Roll.class).getRollMaxTick());
        if (phase > 1.0f) {
            return;
        }
        if (parkourability.get(Roll.class).isDoing() && clientPlayer.isLocalPlayer() && Minecraft.getInstance().options.getCameraType().isFirstPerson() && ParCoolConfig.Client.Booleans.EnableCameraAnimationOfRolling.get().booleanValue()) {
            float rollFactor = new Easing(phase).squareOut(0.0f, 1.0f, 0.0f, 1.0f).get();
            if (this.direction == Roll.Direction.Right) {
                event.setRoll(event.getRoll() + 360.0f * rollFactor);
            } else {
                event.setRoll(event.getRoll() - 360.0f * rollFactor);
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

