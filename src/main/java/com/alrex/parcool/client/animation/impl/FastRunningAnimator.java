package com.alrex.parcool.client.animation.impl;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.PlayerModelRotator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.action.impl.FastRun;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.config.ParCoolConfig;
import com.alrex.parcool.utilities.Easing;
import com.alrex.parcool.utilities.MathUtil;
import com.alrex.parcool.utilities.VectorUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class FastRunningAnimator
extends Animator {
    private float limbSwing = 0.0f;

    @Override
    public boolean shouldRemoved(Player player, Parkourability parkourability) {
        return !parkourability.get(FastRun.class).isDoing();
    }

    private float bodyAngleFactor(float phase) {
        return new Easing(phase).squareOut(0.0f, 1.0f, 0.0f, 1.0f).get();
    }

    @Override
    public void animatePost(Player player, Parkourability parkourability, PlayerModelTransformer transformer) {
        boolean rightArmAnimatable;
        this.limbSwing = transformer.getLimbSwing();
        float phase = ((float)this.getTick() + transformer.getPartialTick()) / 10.0f;
        if (phase > 1.0f) {
            phase = 1.0f;
        }
        float bodyAngleFactor = this.bodyAngleFactor(phase);
        double rightXRotFactor = Math.cos((double)this.limbSwing * 0.6662 + Math.PI);
        double leftXRotFactor = Math.cos((double)this.limbSwing * 0.6662);
        HumanoidArm attackHand = player.getMainArm();
        boolean leftArmAnimatable = attackHand != HumanoidArm.LEFT || transformer.getRawModel().attackTime <= 0.0f;
        boolean bl = rightArmAnimatable = attackHand != HumanoidArm.RIGHT || transformer.getRawModel().attackTime <= 0.0f;
        if (leftArmAnimatable && (transformer.getRawModel().leftArmPose != HumanoidModel.ArmPose.EMPTY && transformer.getRawModel().leftArmPose != HumanoidModel.ArmPose.ITEM || transformer.getRawModel().rightArmPose.isTwoHanded())) {
            leftArmAnimatable = false;
        }
        if (rightArmAnimatable && (transformer.getRawModel().rightArmPose != HumanoidModel.ArmPose.EMPTY && transformer.getRawModel().rightArmPose != HumanoidModel.ArmPose.ITEM || transformer.getRawModel().leftArmPose.isTwoHanded())) {
            rightArmAnimatable = false;
        }
        if (leftArmAnimatable) {
            transformer.translateLeftArm((float)(-Math.abs(leftXRotFactor)), bodyAngleFactor * 0.8f, (float)(leftXRotFactor * 2.0));
        }
        if (rightArmAnimatable) {
            transformer.translateRightArm((float)Math.abs(rightXRotFactor), bodyAngleFactor * 0.8f, (float)(rightXRotFactor * 2.0));
        }
        transformer.translateRightLeg(0.0f, (float)Math.min(0.0, (double)transformer.getRawModel().rightLeg.xRot / 1.5707963267948966), (float)((double)transformer.getRawModel().rightLeg.xRot / 1.0471975511965976)).translateLeftLeg(0.0f, (float)Math.min(0.0, (double)transformer.getRawModel().leftLeg.xRot / 1.5707963267948966), (float)((double)transformer.getRawModel().leftLeg.xRot / 1.0471975511965976));
        float bodyYaw = (float)(10.0 * Math.cos((double)this.limbSwing * 0.6662));
        float tick = (float)this.getTick() + transformer.getPartialTick();
        if (leftArmAnimatable) {
            transformer.rotateLeftArm((float)(Math.toRadians(-25.0f * bodyAngleFactor) + 1.2 * leftXRotFactor * (double)transformer.getLimbSwingAmount()), 0.0f, (float)Math.toRadians((double)(bodyAngleFactor * -15.0f) + (0.65 + Math.cos((double)this.limbSwing * 1.3324)) / 2.0 * Math.sin((double)this.limbSwing * 1.3324) * -30.0));
        }
        if (rightArmAnimatable) {
            transformer.rotateRightArm((float)(Math.toRadians(-25.0f * bodyAngleFactor) + 1.2 * rightXRotFactor * (double)transformer.getLimbSwingAmount()), 0.0f, (float)Math.toRadians((double)(bodyAngleFactor * 15.0f) + (0.65 + Math.cos((double)this.limbSwing * 1.3324 + Math.PI)) / 2.0 * Math.sin((double)this.limbSwing * 1.3324 + Math.PI) * 30.0));
        }
        transformer.rotateAdditionallyHeadPitch(bodyAngleFactor * -30.0f - 5.0f * (float)Math.sin(Math.PI * (double)tick / 10.0)).rotateAdditionallyHeadYaw(bodyYaw).addRotateRightLeg((float)Math.toRadians(-15.0f * bodyAngleFactor), (float)Math.toRadians(bodyYaw), 0.0f).addRotateLeftLeg((float)Math.toRadians(-15.0f * bodyAngleFactor), (float)Math.toRadians(bodyYaw), 0.0f).end();
    }

    @Override
    public void rotatePost(Player player, Parkourability parkourability, PlayerModelRotator rotator) {
        float tick = (float)this.getTick() + rotator.getPartialTick();
        float phase = tick / 10.0f;
        if (phase > 1.0f) {
            phase = 1.0f;
        }
        float bodyYaw = (float)(-10.0 * Math.cos((double)this.limbSwing * 0.6662));
        float pitch = this.bodyAngleFactor(phase) * 30.0f + 5.0f * (float)Math.sin(Math.PI * (double)tick / 10.0);
        float yOffset = 0.145f * (float)Math.pow(Math.cos((double)this.limbSwing * 0.6662), 2.0);
        rotator.translateY(yOffset);
        if (parkourability.getClientInfo().get(ParCoolConfig.Client.Booleans.EnableLeanAnimationOfFastRun).booleanValue()) {
            if (player.isLocalPlayer() && Minecraft.getInstance().screen != null) {
                rotator.startBasedCenter().rotatePitchFrontward(pitch).end();
            } else {
                Vec3 lookAngle = player.getLookAngle();
                Vec3 bodyAngle = VectorUtil.fromYawDegree(MathUtil.lerp(player.yBodyRotO, player.yBodyRot, rotator.getPartialTick()));
                Vec3 differenceVec = new Vec3(lookAngle.x() * bodyAngle.x() + lookAngle.z() * bodyAngle.z(), 0.0, -lookAngle.x() * bodyAngle.z() + lookAngle.z() * bodyAngle.x()).normalize();
                rotator.startBasedCenter().rotatePitchFrontward(pitch).rotateYawRightward(bodyYaw).rotateRollRightward((float)(30.0 * (double)phase * Math.asin(differenceVec.z()))).end();
            }
        } else {
            rotator.startBasedCenter().rotatePitchFrontward(pitch).end();
        }
    }
}

