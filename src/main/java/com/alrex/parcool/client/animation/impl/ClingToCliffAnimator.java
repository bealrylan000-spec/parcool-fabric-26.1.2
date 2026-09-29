package com.alrex.parcool.client.animation.impl;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.PlayerModelRotator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.action.impl.ClingToCliff;
import com.alrex.parcool.common.attachment.common.Parkourability;
import net.minecraft.world.entity.player.Player;

public class ClingToCliffAnimator
extends Animator {
    private final float Lean_Angle = 20.0f;

    @Override
    public boolean shouldRemoved(Player player, Parkourability parkourability) {
        return !parkourability.get(ClingToCliff.class).isDoing();
    }

    @Override
    public void animatePost(Player player, Parkourability parkourability, PlayerModelTransformer transformer) {
        switch (parkourability.get(ClingToCliff.class).getFacingDirection()) {
            case ToWall: {
                double zAngle = 10.0 + 20.0 * Math.sin(24.0f * parkourability.get(ClingToCliff.class).getArmSwingAmount());
                transformer.rotateLeftArm((float)Math.toRadians(-155.0), 0.0f, (float)Math.toRadians(zAngle)).rotateRightArm((float)Math.toRadians(-155.0), 0.0f, (float)Math.toRadians(-zAngle)).makeArmsNatural().rotateRightLeg(0.0f, 0.0f, 0.0f).rotateLeftLeg(0.0f, 0.0f, 0.0f).makeLegsLittleMoving().end();
                break;
            }
            case RightAgainstWall: {
                transformer.rotateAdditionallyHeadPitch(-10.0f).rotateRightArm(0.0f, 0.0f, (float)Math.toRadians(20.0)).makeArmsNatural().rotateLeftArm(0.0f, 0.0f, (float)Math.toRadians(-100.0)).rotateLeftLeg(6.0f, 0.0f, (float)Math.toRadians(-15.0)).rotateRightLeg(-6.0f, 0.0f, (float)Math.toRadians(-25.0)).end();
                break;
            }
            case LeftAgainstWall: {
                transformer.rotateAdditionallyHeadPitch(-10.0f).rotateLeftArm(0.0f, 0.0f, (float)Math.toRadians(-20.0)).makeArmsNatural().rotateRightArm(0.0f, 0.0f, (float)Math.toRadians(100.0)).rotateLeftLeg(-6.0f, 0.0f, (float)Math.toRadians(25.0)).rotateRightLeg(6.0f, 0.0f, (float)Math.toRadians(15.0)).end();
            }
        }
    }

    @Override
    public void rotatePost(Player player, Parkourability parkourability, PlayerModelRotator rotator) {
        ClingToCliff cling = parkourability.get(ClingToCliff.class);
        switch (cling.getFacingDirection()) {
            case RightAgainstWall: {
                rotator.startBasedCenter().rotateRollRightward(20.0f).end();
                break;
            }
            case LeftAgainstWall: {
                rotator.startBasedCenter().rotateRollRightward(-20.0f).end();
            }
        }
    }
}

