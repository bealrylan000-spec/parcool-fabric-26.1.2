package com.alrex.parcool.client.animation;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.world.entity.player.Player;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class PlayerModelRotator {
    private final PoseStack stack;
    private final Player player;
    private final float partial;
    private double playerHeight = 1.8;
    private final float givenYRot;
    private boolean basedCenter = false;
    private boolean basedTop = false;

    public float getYRot() {
        return this.givenYRot;
    }

    public float getPartialTick() {
        return this.partial;
    }

    public PlayerModelRotator(PoseStack stack, Player player, float partial, float yRot) {
        this.stack = stack;
        this.player = player;
        this.partial = partial;
        this.givenYRot = yRot;
        switch (player.getPose()) {
            case SWIMMING: 
            case CROUCHING: 
            case SLEEPING: {
                this.playerHeight = 0.6;
            }
        }
    }

    public PlayerModelRotator start() {
        return this;
    }

    public PlayerModelRotator startBasedCenter() {
        this.basedCenter = true;
        this.stack.translate(0.0, this.playerHeight / 2.0, 0.0);
        return this;
    }

    public PlayerModelRotator startBasedTop() {
        this.basedTop = true;
        this.stack.translate(0.0, this.playerHeight, 0.0);
        return this;
    }

    public PoseStack getRawStack() {
        return this.stack;
    }

    public PlayerModelRotator translateY(float offset) {
        this.stack.translate(0.0f, offset, 0.0f);
        return this;
    }

    public PlayerModelRotator translate(float offsetX, float offsetY, float offsetZ) {
        this.stack.translate(offsetX, offsetY, offsetZ);
        return this;
    }

    public PlayerModelRotator rotatePitchFrontward(float angleDegree) {
        this.stack.mulPose(Axis.XN.rotationDegrees(angleDegree));
        return this;
    }

    public PlayerModelRotator rotateRollRightward(float angleDegree) {
        this.stack.mulPose(Axis.ZN.rotationDegrees(angleDegree));
        return this;
    }

    public PlayerModelRotator rotateYawRightward(float angleDegree) {
        this.stack.mulPose(Axis.YN.rotationDegrees(angleDegree));
        return this;
    }

    public PlayerModelRotator rotate(float angle, Vector3f axis) {
        this.stack.mulPose(new Quaternionf().rotationAxis(angle, (Vector3fc)axis));
        return this;
    }

    public void end() {
        if (this.basedCenter) {
            this.stack.translate(0.0, -this.playerHeight / 2.0, 0.0);
        }
        if (this.basedTop) {
            this.stack.translate(0.0, -this.playerHeight, 0.0);
        }
    }

    public void endEnabledLegGrounding() {
        this.end();
    }
}

