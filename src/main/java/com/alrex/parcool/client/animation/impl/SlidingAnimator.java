package com.alrex.parcool.client.animation.impl;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.PlayerModelRotator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.action.impl.Slide;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.utilities.Easing;
import com.alrex.parcool.utilities.VectorUtil;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class SlidingAnimator
extends Animator {
    private static final int MAX_TRANSITION_TICK = 5;

    @Override
    public boolean shouldRemoved(Player player, Parkourability parkourability) {
        Slide slide = parkourability.get(Slide.class);
        return !slide.isDoing() && slide.getNotDoingTick() >= 5;
    }

    @Override
    public void animatePost(Player player, Parkourability parkourability, PlayerModelTransformer transformer) {
        float animFactor = this.getAnimFactor(parkourability, transformer.getPartialTick());
        transformer.translateLeftLeg(0.0f, -1.2f * animFactor, -2.0f * animFactor).translateRightArm(0.0f, 1.2f * animFactor, 1.2f * animFactor).translateHead(0.0f, 0.0f, -animFactor).rotateHeadPitch(50.0f * animFactor).rotateAdditionallyHeadYaw(50.0f * animFactor).rotateAdditionallyHeadRoll(-10.0f * animFactor).rotateRightArm((float)Math.toRadians(50.0), (float)Math.toRadians(-40.0), 0.0f, animFactor).rotateLeftArm((float)Math.toRadians(20.0), 0.0f, (float)Math.toRadians(-100.0), animFactor).rotateRightLeg((float)Math.toRadians(-30.0), (float)Math.toRadians(40.0), 0.0f, animFactor).rotateLeftLeg((float)Math.toRadians(40.0), (float)Math.toRadians(-30.0), (float)Math.toRadians(15.0), animFactor).makeLegsLittleMoving().makeArmsNatural().end();
    }

    @Override
    public boolean rotatePre(Player player, Parkourability parkourability, PlayerModelRotator rotator) {
        Vec3 vec = parkourability.get(Slide.class).getSlidingVector();
        if (vec == null) {
            return false;
        }
        float animFactor = this.getAnimFactor(parkourability, rotator.getPartialTick());
        float yRot = parkourability.get(Slide.class).isDoing() ? (float)VectorUtil.toYawDegree(vec) : rotator.getYRot();
        rotator.rotateYawRightward(180.0f + yRot).rotatePitchFrontward(-55.0f * animFactor).translate(0.35f * animFactor, 0.0f, 0.0f).rotateYawRightward(-55.0f * animFactor).translate(0.0f, -0.7f * animFactor, -0.3f * animFactor);
        return true;
    }

    private float getAnimFactor(Parkourability parkourability, float partialTick) {
        Slide slide = parkourability.get(Slide.class);
        boolean doing = slide.isDoing();
        int tick = doing ? this.getTick() : slide.getNotDoingTick();
        float animFactor = Math.min(((float)tick + partialTick) / 5.0f, 1.0f);
        if (!doing) {
            animFactor = 1.0f - animFactor;
        }
        animFactor = new Easing(animFactor).sinInOut(0.0f, 1.0f, 0.0f, 1.0f).get();
        return animFactor;
    }
}

