package com.alrex.parcool.client.animation.impl;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.PlayerModelRotator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.action.impl.FastSwim;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.utilities.MathUtil;
import com.alrex.parcool.utilities.VectorUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class FastSwimAnimator
extends Animator {
    @Override
    public boolean shouldRemoved(Player player, Parkourability parkourability) {
        return !parkourability.get(FastSwim.class).isDoing();
    }

    @Override
    public void animatePost(Player player, Parkourability parkourability, PlayerModelTransformer transformer) {
        double leftArmXAngle;
        double rightArmXAngle;
        boolean rightArmAnimatable;
        float animationFactor = ((float)this.getTick() + transformer.getPartialTick()) / 10.0f;
        if (animationFactor > 1.0f) {
            animationFactor = 1.0f;
        }
        float tick = (float)this.getTick() + transformer.getPartialTick();
        double armFactor = Math.cos(Math.PI * (double)tick / 15.0);
        HumanoidArm attackHand = player.getMainArm();
        boolean leftArmAnimatable = attackHand != HumanoidArm.LEFT || transformer.getRawModel().attackTime <= 0.0f;
        boolean bl = rightArmAnimatable = attackHand != HumanoidArm.RIGHT || transformer.getRawModel().attackTime <= 0.0f;
        if (leftArmAnimatable && (transformer.getRawModel().leftArmPose != HumanoidModel.ArmPose.EMPTY && transformer.getRawModel().leftArmPose != HumanoidModel.ArmPose.ITEM || transformer.getRawModel().rightArmPose.isTwoHanded())) {
            leftArmAnimatable = false;
        }
        if (rightArmAnimatable && (transformer.getRawModel().rightArmPose != HumanoidModel.ArmPose.EMPTY && transformer.getRawModel().rightArmPose != HumanoidModel.ArmPose.ITEM || transformer.getRawModel().leftArmPose.isTwoHanded())) {
            rightArmAnimatable = false;
        }
        if ((rightArmXAngle = -(leftArmXAngle = 45.0 * armFactor)) < 0.0) {
            rightArmXAngle = -3.0 * Math.sqrt(-rightArmXAngle);
        }
        if (leftArmXAngle < 0.0) {
            leftArmXAngle = -3.0 * Math.sqrt(-leftArmXAngle);
        }
        if (rightArmAnimatable) {
            transformer.translateRightArm((float)Math.max(0.0, 2.4 * Math.sin(Math.PI * (double)tick / 15.0)), (float)Math.max(0.0, 1.2 * Math.sin(Math.PI * (double)tick / 15.0)), (float)(-1.2 * Math.sin(Math.PI * (double)tick / 15.0)));
        }
        if (leftArmAnimatable) {
            transformer.translateLeftArm(-((float)Math.max(0.0, -2.4 * Math.sin(Math.PI * (double)tick / 15.0))), (float)Math.max(0.0, -1.2 * Math.sin(Math.PI * (double)tick / 15.0)), (float)(1.2 * Math.sin(Math.PI * (double)tick / 15.0)));
        }
        transformer.translateHead(0.0f, 0.0f, 0.5f * animationFactor).rotateAdditionallyHeadYaw((float)(-5.0 * Math.sin(Math.PI * (double)tick / 15.0))).rotateRightArm((float)Math.toRadians(-190.0 + rightArmXAngle), 0.0f, (float)Math.toRadians(-40.0 + 50.0 * Math.sin(Math.PI * (double)tick / 15.0))).rotateLeftArm((float)Math.toRadians(-190.0 + leftArmXAngle), 0.0f, (float)Math.toRadians(40.0 + 50.0 * Math.sin(Math.PI * (double)tick / 15.0)));
        if (rightArmAnimatable) {
            transformer.rotateRightLeg((float)Math.toRadians(-40.0 * Math.cos(Math.PI * (double)tick / 9.0)), 0.0f, 0.0f, animationFactor);
        }
        if (leftArmAnimatable) {
            transformer.rotateLeftLeg((float)Math.toRadians(40.0 * Math.cos(Math.PI * (double)tick / 9.0)), 0.0f, 0.0f, animationFactor);
        }
        transformer.end();
    }

    @Override
    public void rotatePost(Player player, Parkourability parkourability, PlayerModelRotator rotator) {
        if (player.isLocalPlayer() && Minecraft.getInstance().screen != null) {
            return;
        }
        float tick = (float)this.getTick() + rotator.getPartialTick();
        Vec3 lookAngle = player.getLookAngle();
        Vec3 bodyAngle = VectorUtil.fromYawDegree(MathUtil.lerp(player.yBodyRotO, player.yBodyRot, rotator.getPartialTick()));
        Vec3 differenceVec = new Vec3(lookAngle.x() * bodyAngle.x() + lookAngle.z() * bodyAngle.z(), 0.0, -lookAngle.x() * bodyAngle.z() + lookAngle.z() * bodyAngle.x()).normalize();
        rotator.startBasedCenter().rotateYawRightward((float)(-15.0 * Math.asin(differenceVec.z()) + 12.0 * Math.sin(Math.PI * (double)tick / 15.0))).end();
    }
}

