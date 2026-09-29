package com.alrex.parcool.client.animation.impl;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.PlayerModelRotator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.action.impl.HideInBlock;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.utilities.Easing;
import com.alrex.parcool.utilities.MathUtil;
import com.alrex.parcool.utilities.VectorUtil;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class HideInBlockAnimator
extends Animator {
    private final boolean standing;
    private final boolean startFromDiving;

    public HideInBlockAnimator(boolean standing, boolean startFromDiving) {
        this.standing = standing;
        this.startFromDiving = startFromDiving;
    }

    @Override
    public boolean shouldRemoved(Player player, Parkourability parkourability) {
        return !parkourability.get(HideInBlock.class).isDoing();
    }

    @Override
    public void animatePost(Player player, Parkourability parkourability, PlayerModelTransformer transformer) {
        float phase = ((float)this.getTick() + transformer.getPartialTick()) / (this.startFromDiving ? 2.0f : 5.0f);
        if (phase > 1.0f) {
            phase = 1.0f;
        }
        float factor = Easing.with(phase).sinInOut(0.0f, 1.0f, 0.0f, 1.0f).get();
        if (this.standing) {
            transformer.rotateHeadYawRadian((float)(Mth.clamp((double)transformer.getRawModel().getHead().yRot, (double)-0.7853981633974483, (double)0.7853981633974483) / (double)1.4f)).rotateRightArm(0.0f, 0.0f, (float)Math.toRadians(6.0), factor).rotateLeftArm(0.0f, 0.0f, (float)Math.toRadians(-6.0), factor).makeArmsNatural().rotateRightLeg(0.0f, 0.0f, 0.0f).rotateLeftLeg(0.0f, 0.0f, 0.0f);
        } else {
            transformer.rotateHeadPitch(-40.0f * factor).rotateHeadYawRadian((float)(Mth.clamp((double)transformer.getRawModel().getHead().yRot, (double)-0.6283185307179586, (double)0.6283185307179586) / 2.0)).rotateLeftArm((float)Math.toRadians(-15.0), 0.0f, (float)Math.toRadians(-160.0), factor).rotateRightArm((float)Math.toRadians(-15.0), 0.0f, (float)Math.toRadians(160.0), factor).translateLeftLeg(0.0f, -0.8f * factor, -0.7f * factor).translateRightLeg(0.0f, -0.8f * factor, -0.7f * factor).rotateLeftLeg((float)Math.toRadians(-15.0), 0.0f, (float)Math.toRadians(-10.0), factor).rotateRightLeg((float)Math.toRadians(-15.0), 0.0f, (float)Math.toRadians(10.0), factor);
        }
    }

    @Override
    public boolean rotatePre(Player player, Parkourability parkourability, PlayerModelRotator rotator) {
        float phase = ((float)this.getTick() + rotator.getPartialTick()) / 5.0f;
        if (phase > 1.0f) {
            phase = 1.0f;
        }
        float factor = Easing.with(phase).sinInOut(0.0f, 1.0f, 0.0f, 1.0f).get();
        Vec3 lookVec = parkourability.get(HideInBlock.class).getLookDirection();
        if (lookVec == null) {
            return false;
        }
        if (this.standing) {
            float playerYRot = 180.0f + Mth.lerp((float)rotator.getPartialTick(), (float)player.yRotO, (float)player.getYRot());
            float yRot = (float)VectorUtil.toYawDegree(lookVec);
            rotator.rotateYawRightward(playerYRot + MathUtil.normalizeDegree(180.0f + yRot - playerYRot) * factor);
            return true;
        }
        float yRot = (float)VectorUtil.toYawDegree(lookVec);
        rotator.rotateYawRightward(180.0f + yRot).rotatePitchFrontward(this.startFromDiving ? 180.0f - 90.0f * factor : 90.0f * factor).translate(0.0f, -0.95f * factor, 0.3f * factor);
        return true;
    }
}

