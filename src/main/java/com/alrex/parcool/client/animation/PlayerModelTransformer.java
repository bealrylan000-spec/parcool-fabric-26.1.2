package com.alrex.parcool.client.animation;

import com.alrex.parcool.api.unstable.animation.AnimationOption;
import com.alrex.parcool.api.unstable.animation.AnimationPart;
import com.alrex.parcool.utilities.MathUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

public class PlayerModelTransformer {
    private final Player player;
    private final PlayerModel model;
    private final float ageInTicks;
    private final float limbSwing;
    private final float limbSwingAmount;
    private final float netHeadYaw;
    private final float headPitch;
    private AnimationOption option = new AnimationOption();

    public float getPartialTick() {
        return Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true);
    }

    public float getHeadPitch() {
        return this.headPitch;
    }

    public float getNetHeadYaw() {
        return this.netHeadYaw;
    }

    public float getLimbSwing() {
        return this.limbSwing;
    }

    public float getLimbSwingAmount() {
        return this.limbSwingAmount;
    }

    public PlayerModel getRawModel() {
        return this.model;
    }

    public PlayerModelTransformer(Player player, PlayerModel model, boolean slim, float ageInTicks, float limbSwing, float limbSwingAmount, float netHeadYaw, float headPitch) {
        this.player = player;
        this.model = model;
        this.ageInTicks = ageInTicks;
        this.limbSwing = limbSwing;
        this.limbSwingAmount = limbSwingAmount;
        this.netHeadYaw = netHeadYaw;
        this.headPitch = headPitch;
    }

    public void setOption(AnimationOption option) {
        this.option = option;
    }

    public PlayerModelTransformer rotateRightArm(float angleX, float angleY, float angleZ) {
        if (this.option.isCanceled(AnimationPart.RIGHT_ARM)) {
            return this;
        }
        ModelPart rightArm = this.model.rightArm;
        if (rightArm.visible) {
            this.setRotations(rightArm, angleX, angleY, angleZ);
        }
        return this;
    }

    public PlayerModelTransformer rotateRightArm(float angleX, float angleY, float angleZ, float factor) {
        if (this.option.isCanceled(AnimationPart.RIGHT_ARM)) {
            return this;
        }
        ModelPart rightArm = this.model.rightArm;
        if (rightArm.visible) {
            this.setRotations(rightArm, MathUtil.lerp(rightArm.xRot, angleX, factor), MathUtil.lerp(rightArm.yRot, angleY, factor), MathUtil.lerp(rightArm.zRot, angleZ, factor));
        }
        return this;
    }

    public PlayerModelTransformer rotateLeftArm(float angleX, float angleY, float angleZ) {
        if (this.option.isCanceled(AnimationPart.LEFT_ARM)) {
            return this;
        }
        ModelPart leftArm = this.model.leftArm;
        if (leftArm.visible) {
            this.setRotations(leftArm, angleX, angleY, angleZ);
        }
        return this;
    }

    public PlayerModelTransformer rotateLeftArm(float angleX, float angleY, float angleZ, float factor) {
        if (this.option.isCanceled(AnimationPart.LEFT_ARM)) {
            return this;
        }
        ModelPart leftArm = this.model.leftArm;
        if (leftArm.visible) {
            this.setRotations(leftArm, MathUtil.lerp(leftArm.xRot, angleX, factor), MathUtil.lerp(leftArm.yRot, angleY, factor), MathUtil.lerp(leftArm.zRot, angleZ, factor));
        }
        return this;
    }

    public PlayerModelTransformer rotateRightLeg(float angleX, float angleY, float angleZ) {
        if (this.option.isCanceled(AnimationPart.RIGHT_LEG)) {
            return this;
        }
        ModelPart rightLeg = this.model.rightLeg;
        if (rightLeg.visible) {
            this.setRotations(rightLeg, angleX, angleY, angleZ);
        }
        return this;
    }

    public PlayerModelTransformer rotateRightLeg(float angleX, float angleY, float angleZ, float factor) {
        if (this.option.isCanceled(AnimationPart.RIGHT_LEG)) {
            return this;
        }
        ModelPart rightLeg = this.model.rightLeg;
        if (rightLeg.visible) {
            this.setRotations(rightLeg, MathUtil.lerp(rightLeg.xRot, angleX, factor), MathUtil.lerp(rightLeg.yRot, angleY, factor), MathUtil.lerp(rightLeg.zRot, angleZ, factor));
        }
        return this;
    }

    public PlayerModelTransformer rotateLeftLeg(float angleX, float angleY, float angleZ) {
        if (this.option.isCanceled(AnimationPart.LEFT_LEG)) {
            return this;
        }
        ModelPart leftLeg = this.model.leftLeg;
        if (leftLeg.visible) {
            this.setRotations(leftLeg, angleX, angleY, angleZ);
        }
        return this;
    }

    public PlayerModelTransformer rotateLeftLeg(float angleX, float angleY, float angleZ, float factor) {
        if (this.option.isCanceled(AnimationPart.LEFT_LEG)) {
            return this;
        }
        ModelPart leftLeg = this.model.leftLeg;
        if (leftLeg.visible) {
            this.setRotations(leftLeg, MathUtil.lerp(leftLeg.xRot, angleX, factor), MathUtil.lerp(leftLeg.yRot, angleY, factor), MathUtil.lerp(leftLeg.zRot, angleZ, factor));
        }
        return this;
    }

    public PlayerModelTransformer addRotateRightArm(float angleX, float angleY, float angleZ) {
        if (this.option.isCanceled(AnimationPart.RIGHT_ARM)) {
            return this;
        }
        ModelPart arm = this.model.rightArm;
        if (arm.visible) {
            this.setRotations(arm, arm.xRot + angleX, arm.yRot + angleY, arm.zRot + angleZ);
        }
        return this;
    }

    public PlayerModelTransformer addRotateLeftArm(float angleX, float angleY, float angleZ) {
        if (this.option.isCanceled(AnimationPart.LEFT_ARM)) {
            return this;
        }
        ModelPart arm = this.model.leftArm;
        if (arm.visible) {
            this.setRotations(arm, arm.xRot + angleX, arm.yRot + angleY, arm.zRot + angleZ);
        }
        return this;
    }

    public PlayerModelTransformer addRotateRightLeg(float angleX, float angleY, float angleZ) {
        if (this.option.isCanceled(AnimationPart.RIGHT_LEG)) {
            return this;
        }
        ModelPart leg = this.model.rightLeg;
        if (leg.visible) {
            this.setRotations(leg, leg.xRot + angleX, leg.yRot + angleY, leg.zRot + angleZ);
        }
        return this;
    }

    public PlayerModelTransformer addRotateLeftLeg(float angleX, float angleY, float angleZ) {
        if (this.option.isCanceled(AnimationPart.LEFT_LEG)) {
            return this;
        }
        ModelPart leg = this.model.leftLeg;
        if (leg.visible) {
            this.setRotations(leg, leg.xRot + angleX, leg.yRot + angleY, leg.zRot + angleZ);
        }
        return this;
    }

    public PlayerModelTransformer addRotateRightArm(float angleX, float angleY, float angleZ, float factor) {
        if (this.option.isCanceled(AnimationPart.RIGHT_ARM)) {
            return this;
        }
        return this.addRotateRightArm(angleX * factor, angleY * factor, angleZ * factor);
    }

    public PlayerModelTransformer addRotateLeftArm(float angleX, float angleY, float angleZ, float factor) {
        if (this.option.isCanceled(AnimationPart.LEFT_ARM)) {
            return this;
        }
        return this.addRotateLeftArm(angleX * factor, angleY * factor, angleZ * factor);
    }

    public PlayerModelTransformer addRotateRightLeg(float angleX, float angleY, float angleZ, float factor) {
        if (this.option.isCanceled(AnimationPart.RIGHT_LEG)) {
            return this;
        }
        return this.addRotateRightLeg(angleX * factor, angleY * factor, angleZ * factor);
    }

    public PlayerModelTransformer addRotateLeftLeg(float angleX, float angleY, float angleZ, float factor) {
        if (this.option.isCanceled(AnimationPart.LEFT_LEG)) {
            return this;
        }
        return this.addRotateLeftLeg(angleX * factor, angleY * factor, angleZ * factor);
    }

    public PlayerModelTransformer makeArmsNatural() {
        if (this.option.isCanceled(AnimationPart.RIGHT_ARM)) {
            return this;
        }
        if (this.option.isCanceled(AnimationPart.LEFT_ARM)) {
            return this;
        }
        AnimationUtils.bobArms((ModelPart)this.model.rightArm, (ModelPart)this.model.leftArm, (float)this.ageInTicks);
        return this;
    }

    public PlayerModelTransformer makeLegsMoveDynamically(float factor) {
        if (this.option.isCanceled(AnimationPart.RIGHT_LEG)) {
            return this;
        }
        if (this.option.isCanceled(AnimationPart.LEFT_LEG)) {
            return this;
        }
        this.model.rightLeg.zRot += Mth.cos((float)(this.ageInTicks * 0.56f)) * 0.8f * factor + 0.05f;
        this.model.leftLeg.zRot -= Mth.cos((float)(this.ageInTicks * 0.56f)) * 0.8f * factor + 0.05f;
        this.model.rightLeg.xRot += Mth.sin((float)(this.ageInTicks * 0.56f)) * 0.8f * factor;
        this.model.leftLeg.xRot -= Mth.sin((float)(this.ageInTicks * 0.56f)) * 0.8f * factor;
        return this;
    }

    public PlayerModelTransformer makeArmsMoveDynamically(float factor) {
        if (this.option.isCanceled(AnimationPart.RIGHT_ARM)) {
            return this;
        }
        if (this.option.isCanceled(AnimationPart.LEFT_ARM)) {
            return this;
        }
        this.model.rightArm.zRot += Mth.cos((float)(this.ageInTicks * 0.56f)) * 0.8f * factor + 0.05f;
        this.model.leftArm.zRot -= Mth.cos((float)(this.ageInTicks * 0.56f)) * 0.8f * factor + 0.05f;
        this.model.rightArm.xRot += Mth.sin((float)(this.ageInTicks * 0.56f)) * 0.8f * factor;
        this.model.leftArm.xRot -= Mth.sin((float)(this.ageInTicks * 0.56f)) * 0.8f * factor;
        return this;
    }

    public PlayerModelTransformer makeLegsLittleMoving() {
        if (this.option.isCanceled(AnimationPart.RIGHT_LEG)) {
            return this;
        }
        if (this.option.isCanceled(AnimationPart.LEFT_LEG)) {
            return this;
        }
        AnimationUtils.bobArms((ModelPart)this.model.rightLeg, (ModelPart)this.model.leftLeg, (float)this.ageInTicks);
        return this;
    }

    public PlayerModelTransformer makeLegsShakingDynamically(float factor) {
        if (this.option.isCanceled(AnimationPart.RIGHT_LEG)) {
            return this;
        }
        if (this.option.isCanceled(AnimationPart.LEFT_LEG)) {
            return this;
        }
        this.model.rightLeg.zRot += Mth.cos((float)(this.ageInTicks * 0.56f)) * 0.8f * factor + 0.05f;
        this.model.leftLeg.zRot += Mth.cos((float)(this.ageInTicks * 0.56f)) * 0.8f * factor + 0.05f;
        this.model.rightLeg.xRot += Mth.sin((float)(this.ageInTicks * 0.56f)) * 0.2f * factor;
        this.model.leftLeg.xRot -= Mth.sin((float)(this.ageInTicks * 0.56f)) * 0.2f * factor;
        return this;
    }

    public PlayerModelTransformer rotateAdditionallyHeadPitch(float pitchDegree) {
        if (this.option.isCanceled(AnimationPart.HEAD)) {
            return this;
        }
        this.model.head.xRot = (float)Math.toRadians(pitchDegree + this.headPitch);
        return this;
    }

    public PlayerModelTransformer rotateHeadPitch(float pitchDegree) {
        if (this.option.isCanceled(AnimationPart.HEAD)) {
            return this;
        }
        this.model.head.xRot = (float)Math.toRadians(pitchDegree);
        return this;
    }

    public PlayerModelTransformer rotateHeadYaw(float yawDegree) {
        if (this.option.isCanceled(AnimationPart.HEAD)) {
            return this;
        }
        this.model.head.yRot = (float)Math.toRadians(yawDegree);
        return this;
    }

    public PlayerModelTransformer rotateHeadYawRadian(float yawRadian) {
        if (this.option.isCanceled(AnimationPart.HEAD)) {
            return this;
        }
        this.model.head.yRot = yawRadian;
        return this;
    }

    public PlayerModelTransformer rotateAdditionallyHeadYaw(float yawDegree) {
        if (this.option.isCanceled(AnimationPart.HEAD)) {
            return this;
        }
        this.model.head.yRot = (float)Math.toRadians(yawDegree + this.netHeadYaw);
        return this;
    }

    public PlayerModelTransformer rotateAdditionallyHeadRoll(float rollDegree) {
        if (this.option.isCanceled(AnimationPart.HEAD)) {
            return this;
        }
        this.model.head.zRot = (float)Math.toRadians(rollDegree + this.netHeadYaw);
        return this;
    }

    public PlayerModelTransformer translateRightArm(float xOffset, float yOffset, float zOffset) {
        if (this.option.isCanceled(AnimationPart.RIGHT_ARM)) {
            return this;
        }
        this.getRawModel().rightArm.x += xOffset;
        this.getRawModel().rightArm.y += yOffset;
        this.getRawModel().rightArm.z += zOffset;
        return this;
    }

    public PlayerModelTransformer translateLeftArm(float xOffset, float yOffset, float zOffset) {
        if (this.option.isCanceled(AnimationPart.LEFT_ARM)) {
            return this;
        }
        this.getRawModel().leftArm.x += xOffset;
        this.getRawModel().leftArm.y += yOffset;
        this.getRawModel().leftArm.z += zOffset;
        return this;
    }

    public PlayerModelTransformer translateRightLeg(float xOffset, float yOffset, float zOffset) {
        if (this.option.isCanceled(AnimationPart.RIGHT_LEG)) {
            return this;
        }
        this.getRawModel().rightLeg.x += xOffset;
        this.getRawModel().rightLeg.y += yOffset;
        this.getRawModel().rightLeg.z += zOffset;
        return this;
    }

    public PlayerModelTransformer translateLeftLeg(float xOffset, float yOffset, float zOffset) {
        if (this.option.isCanceled(AnimationPart.LEFT_LEG)) {
            return this;
        }
        this.getRawModel().leftLeg.x += xOffset;
        this.getRawModel().leftLeg.y += yOffset;
        this.getRawModel().leftLeg.z += zOffset;
        return this;
    }

    public PlayerModelTransformer translateHead(float xOffset, float yOffset, float zOffset) {
        if (this.option.isCanceled(AnimationPart.HEAD)) {
            return this;
        }
        this.getRawModel().head.x += xOffset;
        this.getRawModel().head.y += yOffset;
        this.getRawModel().head.z += zOffset;
        return this;
    }

    public void end() {
    }

    public void copyFromBodyToWear() {
        this.model.rightSleeve.copyFrom(this.model.rightArm);
        this.model.leftSleeve.copyFrom(this.model.leftArm);
        this.model.rightPants.copyFrom(this.model.rightLeg);
        this.model.leftPants.copyFrom(this.model.leftLeg);
        this.model.jacket.copyFrom(this.model.body);
        this.model.hat.copyFrom(this.model.head);
    }

    private void setRotations(ModelPart renderer, float angleX, float angleY, float angleZ) {
        renderer.xRot = angleX;
        renderer.yRot = angleY;
        renderer.zRot = angleZ;
    }

    public void reset() {
        this.resetModel(this.model.head);
        this.resetModel(this.model.hat);
        this.resetModel(this.model.jacket);
        this.resetModel(this.model.body);
        this.resetModel(this.model.rightArm);
        this.model.rightArm.x = -5.0f;
        this.model.rightArm.y = 2.0f;
        this.model.rightArm.z = 0.0f;
        this.model.rightSleeve.copyFrom(this.model.rightArm);
        this.resetModel(this.model.leftArm);
        this.model.leftArm.x = 5.0f;
        this.model.leftArm.y = 2.0f;
        this.model.leftArm.z = 0.0f;
        this.model.leftSleeve.copyFrom(this.model.leftArm);
        this.resetModel(this.model.leftLeg);
        this.model.leftLeg.x = 1.9f;
        this.model.leftLeg.y = 12.0f;
        this.model.leftLeg.z = 0.0f;
        this.model.leftPants.copyFrom(this.model.leftLeg);
        this.resetModel(this.model.rightLeg);
        this.model.rightLeg.x = -1.9f;
        this.model.rightLeg.y = 12.0f;
        this.model.rightLeg.z = 0.0f;
        this.model.rightPants.copyFrom(this.model.rightLeg);
    }

    public void resetModel(ModelPart model) {
        model.xRot = 0.0f;
        model.yRot = 0.0f;
        model.zRot = 0.0f;
        model.x = 0.0f;
        model.y = 0.0f;
        model.z = 0.0f;
    }
}

