package com.alrex.parcool.client.animation.impl;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.PlayerModelRotator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.action.impl.Dodge;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.utilities.Easing;
import net.minecraft.world.entity.player.Player;
import com.alrex.parcool.platform.event.client.RenderFrameEvent;

public class ExaggeratedSideDodgeAnimator
extends Animator {
    public static final int Dodge_Max_Tick = 14;
    final Dodge.DodgeDirection direction;

    @Override
    public boolean shouldRemoved(Player player, Parkourability parkourability) {
        return this.getTick() >= 14;
    }

    public ExaggeratedSideDodgeAnimator(Dodge.DodgeDirection dodgeDirection) {
        this.direction = dodgeDirection;
    }

    @Override
    public void animatePost(Player player, Parkourability parkourability, PlayerModelTransformer transformer) {
        float phase = ((float)this.getTick() + transformer.getPartialTick()) / 14.0f;
        if (phase > 1.0f) {
            return;
        }
        float animFactor = new Easing(phase).squareIn(0.0f, 0.25f, 0.0f, 1.0f).linear(0.25f, 0.75f, 1.0f, 1.0f).squareOut(0.75f, 1.0f, 1.0f, 0.0f).get();
        switch (this.direction) {
            case Front: {
                break;
            }
            case Right: {
                break;
            }
            case Left: {
                float rightArmZFactor = new Easing(phase).sinInOut(0.0f, 0.2f, 0.0f, 1.0f).linear(0.2f, 0.4f, 1.0f, 1.0f).sinInOut(0.4f, 1.0f, 1.0f, 0.0f).get();
                float rightArmXFactor = new Easing(phase).sinInOut(0.0f, 0.2f, 0.0f, -1.0f).sinInOut(0.2f, 0.6f, -1.0f, 1.0f).sinInOut(0.6f, 1.0f, 1.0f, 0.0f).get();
                float leftLegFactor = new Easing(phase).sinInOut(0.0f, 0.35f, 0.0f, 1.0f).linear(0.35f, 0.65f, 1.0f, 1.0f).sinInOut(0.65f, 1.0f, 1.0f, 0.0f).get();
                float leftArmZFactor = new Easing(phase).sinInOut(0.0f, 0.5f, 0.0f, 1.0f).sinInOut(0.5f, 1.0f, 1.0f, 0.0f).get();
                float leftArmXFactor = new Easing(phase).sinInOut(0.0f, 0.25f, 0.0f, -1.0f).sinInOut(0.25f, 0.75f, -1.0f, 1.0f).sinInOut(0.75f, 1.0f, 1.0f, 0.0f).get();
                float rightLegXFactor = new Easing(phase).linear(0.0f, 0.2f, 0.0f, 0.0f).sinInOut(0.2f, 0.6f, 0.0f, 1.0f).sinInOut(0.6f, 1.0f, 1.0f, 0.0f).get();
                float rightLegZFactor = new Easing(phase).squareOut(0.0f, 0.25f, 0.0f, 1.0f).sinInOut(0.25f, 0.6f, 1.0f, 0.2f).linear(0.6f, 1.0f, 0.2f, 0.0f).get();
                transformer.rotateRightLeg((float)Math.toRadians(70.0f * rightLegXFactor), 0.0f, (float)Math.toRadians(80.0f * rightLegZFactor), animFactor).rotateRightArm((float)Math.toRadians(70.0f * rightArmXFactor), 0.0f, (float)Math.toRadians(90.0f * rightArmZFactor), animFactor).rotateLeftLeg((float)Math.toRadians(-70.0f * leftLegFactor), 0.0f, 0.0f, animFactor).rotateLeftArm((float)Math.toRadians(70.0f * leftArmXFactor), 0.0f, (float)Math.toRadians(-60.0f * leftArmZFactor), animFactor).end();
                break;
            }
            case Back: {
                float leftArmFactor = new Easing(phase).linear(0.0f, 0.25f, 0.0f, 0.0f).sinInOut(0.2f, 0.45f, 0.0f, 1.0f).linear(0.45f, 0.55f, 1.0f, 1.0f).sinInOut(0.55f, 0.8f, 1.0f, 0.0f).linear(0.75f, 1.0f, 0.0f, 0.0f).get();
                float leftLegFactor = new Easing(phase).squareOut(0.0f, 0.4f, 0.0f, 1.0f).sinInOut(0.4f, 0.8f, 1.0f, 0.0f).linear(0.8f, 1.0f, 0.0f, 0.0f).get();
                transformer.rotateLeftArm(0.0f, 0.0f, (float)Math.toRadians(-100.0f * leftArmFactor), animFactor).rotateLeftLeg((float)Math.toRadians(-40.0f * leftLegFactor), 0.0f, 0.0f, animFactor).end();
            }
        }
    }

    @Override
    public void rotatePost(Player player, Parkourability parkourability, PlayerModelRotator rotator) {
        float phase = ((float)this.getTick() + rotator.getPartialTick()) / 14.0f;
        if (phase > 1.0f) {
            return;
        }
        switch (this.direction) {
            case Left: {
                float bodyPitchFactor = new Easing(phase).linear(0.0f, 0.1f, 0.0f, 0.1f).sinInOut(0.1f, 0.35f, 0.1f, 1.0f).linear(0.35f, 0.65f, 1.0f, 1.0f).sinInOut(0.65f, 0.9f, 1.0f, 0.1f).linear(0.9f, 1.0f, 0.1f, 0.0f).get();
                float yawFactor = new Easing(phase).squareIn(0.0f, 0.3f, 0.0f, 0.4f).linear(0.3f, 0.7f, 0.4f, 0.6f).squareOut(0.7f, 1.0f, 0.6f, 1.0f).get();
                float bodyYawFactor = new Easing(phase).cubicInOut(0.0f, 1.0f, 0.0f, 1.0f).get();
                rotator.startBasedCenter().rotateYawRightward(-360.0f * yawFactor).rotatePitchFrontward(90.0f * bodyPitchFactor).rotateYawRightward(-360.0f * bodyYawFactor).end();
                break;
            }
            case Back: {
                float bodyRollFactor = new Easing(phase).sinInOut(0.0f, 0.5f, 0.0f, 1.0f).sinInOut(0.5f, 1.0f, 1.0f, 0.0f).get();
                float bodyYawFactor = new Easing(phase).sinInOut(0.0f, 1.0f, 0.0f, 1.0f).get();
                rotator.startBasedCenter().rotateRollRightward(95.0f * bodyRollFactor).rotateYawRightward(-360.0f * bodyYawFactor).end();
            }
        }
    }

    @Override
    public void onRenderTick(RenderFrameEvent event, Player player, Parkourability parkourability) {
        player.setYBodyRot(player.getYHeadRot());
    }
}

