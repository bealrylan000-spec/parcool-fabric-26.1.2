package com.alrex.parcool.client.animation.impl;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.PlayerModelRotator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.action.impl.ChargeJump;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.utilities.Easing;
import net.minecraft.world.entity.player.Player;

public class JumpChargingAnimator
extends Animator {
    @Override
    public boolean shouldRemoved(Player player, Parkourability parkourability) {
        ChargeJump c = parkourability.get(ChargeJump.class);
        return !c.isCharging();
    }

    @Override
    public boolean animatePre(Player player, Parkourability parkourability, PlayerModelTransformer transformer) {
        float animFactor = this.getAnimFactor(parkourability, transformer.getPartialTick());
        transformer.translateLeftLeg(0.0f, -3.4f * animFactor, -2.6f * animFactor).translateRightLeg(0.0f, -1.4f * animFactor, -1.6f * animFactor).translateRightArm(0.3f * animFactor, 0.4f * animFactor, 0.0f).translateLeftArm(-0.3f * animFactor, 0.4f * animFactor, 0.0f).rotateLeftLeg((float)Math.toRadians(-15.0), 0.0f, 0.0f, animFactor).rotateRightLeg((float)Math.toRadians(10.0), 0.0f, 0.0f, animFactor).rotateLeftArm((float)Math.toRadians(6.0), 0.0f, (float)Math.toRadians(-4.0), animFactor).rotateRightArm((float)Math.toRadians(-22.0), 0.0f, (float)Math.toRadians(4.0), animFactor).makeArmsNatural().rotateAdditionallyHeadPitch(-45.0f * animFactor);
        return true;
    }

    @Override
    public boolean rotatePre(Player player, Parkourability parkourability, PlayerModelRotator rotator) {
        float animFactor = this.getAnimFactor(parkourability, rotator.getPartialTick());
        rotator.rotateYawRightward(180.0f + rotator.getYRot()).translate(0.0f, 0.0f, 0.3f * animFactor).rotatePitchFrontward(25.0f * animFactor);
        return true;
    }

    private float getAnimFactor(Parkourability parkourability, float partialTick) {
        float transitionPhase = Math.min(1.0f, ((float)parkourability.get(ChargeJump.class).getChargingTick() + (partialTick *= (float)(parkourability.get(ChargeJump.class).getNotChargingTick() == 0 ? 1 : -1))) / 18.0f);
        float animFactor = new Easing(transitionPhase).sinInOut(0.0f, 1.0f, 0.0f, 1.0f).get();
        return animFactor;
    }
}

