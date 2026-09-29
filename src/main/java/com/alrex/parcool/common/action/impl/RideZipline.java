package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.platform.attachment.AttachmentHolder;

import com.alrex.parcool.client.animation.impl.RideZiplineAnimator;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.client.input.KeyRecorder;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.BehaviorEnforcer;
import com.alrex.parcool.common.action.StaminaConsumeTiming;
import com.alrex.parcool.common.action.impl.Flipping;
import com.alrex.parcool.common.action.impl.HangDown;
import com.alrex.parcool.common.action.impl.HorizontalWallRun;
import com.alrex.parcool.common.action.impl.Vault;
import com.alrex.parcool.common.action.impl.VerticalWallRun;
import com.alrex.parcool.common.attachment.Attachments;
import com.alrex.parcool.common.attachment.client.Animation;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.common.attachment.common.ReadonlyStamina;
import com.alrex.parcool.common.entity.zipline.ZiplineRopeEntity;
import com.alrex.parcool.common.zipline.Zipline;
import com.alrex.parcool.utilities.BufferUtil;
import com.alrex.parcool.utilities.VectorUtil;
import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class RideZipline
extends Action {
    private static final BehaviorEnforcer.ID ID_FALL_FLY_CANCEL = BehaviorEnforcer.newID();
    private static final BehaviorEnforcer.ID ID_SPRINT_CANCEL = BehaviorEnforcer.newID();
    @Nullable
    private ZiplineRopeEntity ridingZipline;
    @Nullable
    private Vec3 endOffsetFromStart;
    private double speed;
    private double acceleration;
    private double slope;
    private float currentT;
    @Nullable
    private Vec3 currentPos;
    private boolean previouslyStopByCollision = false;

    public double getAcceleration() {
        return this.acceleration;
    }

    public double getSlope() {
        return this.slope;
    }

    @Nullable
    public Vec3 getEndOffsetFromStart() {
        return this.endOffsetFromStart;
    }

    @Override
    public boolean canStart(Player player, Parkourability parkourability, ByteBuffer startInfo) {
        if (!(!KeyBindings.getKeyRideZipline().isDown() || player.onGround() || player.isInWater() || player.isFallFlying() || player.isCrouching() || player.isSwimming() || KeyBindings.isKeyJumpDown().booleanValue() && this.getNotDoingTick() <= 5 || this.previouslyStopByCollision && this.getNotDoingTick() <= 5 || parkourability.get(Vault.class).isDoing() || parkourability.get(HangDown.class).isDoing() || parkourability.get(Flipping.class).isDoing() || parkourability.get(HorizontalWallRun.class).isDoing() || parkourability.get(VerticalWallRun.class).isDoing())) {
            ZiplineRopeEntity ropeEntity = Zipline.getHangableZipline(player.level(), player);
            if (ropeEntity == null) {
                return false;
            }
            double t = ropeEntity.getZipline().getParameter(player.position());
            if (t < 0.0 || 1.0 < t) {
                return false;
            }
            this.ridingZipline = ropeEntity;
            BufferUtil.wrap(startInfo).putVec3(this.ridingZipline.getZipline().getOffsetToEndFromStart());
            return true;
        }
        return false;
    }

    @Override
    public boolean canContinue(Player player, Parkourability parkourability) {
        if (player.horizontalCollision || player.verticalCollision) {
            this.previouslyStopByCollision = true;
            return false;
        }
        return KeyBindings.getKeyRideZipline().isDown() && !KeyRecorder.keyJumpState.isPressed() && !player.isInWall() && !((ReadonlyStamina)AttachmentHolder.getData(player, Attachments.STAMINA)).isExhausted() && this.ridingZipline != null && this.ridingZipline.isAlive() && 0.0f <= this.currentT && this.currentT <= 1.0f;
    }

    @Override
    public void onStartInLocalClient(Player player, Parkourability parkourability, ByteBuffer startData) {
        if (this.ridingZipline == null) {
            return;
        }
        this.rideNewZipline(this.ridingZipline, player.position(), player.getDeltaMovement());
        player.setSprinting(false);
        parkourability.getBehaviorEnforcer().setMarkerEnforceMovePoint(this::isDoing, () -> {
            if (this.currentPos == null) {
                return null;
            }
            return this.currentPos.subtract(0.0, (double)player.getBbHeight() * 1.11, 0.0);
        });
        parkourability.getBehaviorEnforcer().addMarkerCancellingSprint(ID_SPRINT_CANCEL, this::isDoing);
        Animation animation = Animation.get(player);
        if (animation == null) {
            return;
        }
        animation.setAnimator(new RideZiplineAnimator());
    }

    @Override
    public void onStartInOtherClient(Player player, Parkourability parkourability, ByteBuffer startData) {
        Animation animation = Animation.get(player);
        if (animation == null) {
            return;
        }
        animation.setAnimator(new RideZiplineAnimator());
    }

    @Override
    public void onStart(Player player, Parkourability parkourability, ByteBuffer startData) {
        this.previouslyStopByCollision = false;
        this.endOffsetFromStart = BufferUtil.getVec3(startData);
        player.setSprinting(false);
        parkourability.getBehaviorEnforcer().addMarkerCancellingFallFlying(ID_FALL_FLY_CANCEL, this::isDoing);
    }

    @Override
    public void onWorkingTickInLocalClient(Player player, Parkourability parkourability) {
        if (this.ridingZipline == null) {
            return;
        }
        AttributeInstance speedAttr = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttr == null) {
            return;
        }
        if (!(player instanceof LocalPlayer)) {
            return;
        }
        LocalPlayer localPlayer = (LocalPlayer)player;
        double oldSpeed = this.speed;
        Zipline zipline = this.ridingZipline.getZipline();
        double gravity = player.getAttributeValue(Attributes.GRAVITY);
        this.slope = zipline.getSlope(this.currentT);
        this.speed *= 0.98;
        if (player.isInWater()) {
            this.speed *= 0.8;
        }
        this.speed -= gravity * this.slope * Mth.invSqrt((double)(this.slope * this.slope + 1.0));
        Vec3 input = new Vec3((double)(-localPlayer.input.leftImpulse), 0.0, (double)localPlayer.input.forwardImpulse);
        Vec3 offset = zipline.getOffsetToEndFromStart();
        if (input.lengthSqr() > 0.01) {
            double dot = player.getLookAngle().yRot((float)Math.toRadians(VectorUtil.toYawDegree(input))).multiply(1.0, 0.0, 1.0).normalize().dot(new Vec3(offset.x(), 0.0, offset.z()).normalize());
            this.speed += Math.min(dot * 0.01 * (speedAttr.getValue() / speedAttr.getBaseValue()), 0.08);
        }
        this.currentT = (float)zipline.getMovedPositionByParameterApproximately(this.currentT, (float)this.speed);
        this.acceleration = this.speed - oldSpeed;
        this.currentPos = zipline.getMidPoint(this.currentT);
    }

    private void rideNewZipline(ZiplineRopeEntity ziplineRopeEntity, Vec3 position, Vec3 deltaMovement) {
        this.ridingZipline = ziplineRopeEntity;
        Zipline zipline = ziplineRopeEntity.getZipline();
        this.acceleration = 0.0;
        this.currentT = Mth.clamp((float)zipline.getParameter(position), (float)0.0f, (float)1.0f);
        this.currentPos = zipline.getMidPoint(this.currentT);
        this.slope = zipline.getSlope(this.currentT);
        float yScale = (float)this.slope;
        Vec3 pointsOffset = zipline.getOffsetToEndFromStart();
        double xzLenInvSqrt = Mth.fastInvSqrt((double)(pointsOffset.x() * pointsOffset.x() + pointsOffset.z() * pointsOffset.z()));
        double xScale = pointsOffset.x() * xzLenInvSqrt;
        double zScale = pointsOffset.z() * xzLenInvSqrt;
        Vec3 speedScale = new Vec3(xScale, (double)yScale, zScale).normalize();
        this.speed = deltaMovement.dot(speedScale);
    }

    private static Vec3 getDeltaMovement(Zipline zipline, double speed, float currentT) {
        float yScale = zipline.getSlope(currentT);
        Vec3 pointsOffset = zipline.getOffsetToEndFromStart();
        double xzLenInvSqrt = Mth.fastInvSqrt((double)(pointsOffset.x() * pointsOffset.x() + pointsOffset.z() * pointsOffset.z()));
        double xScale = pointsOffset.x() * xzLenInvSqrt;
        double zScale = pointsOffset.z() * xzLenInvSqrt;
        Vec3 speedScale = new Vec3(xScale, (double)yScale, zScale).normalize();
        return speedScale.scale(speed);
    }

    @Override
    public void saveSynchronizedState(ByteBuffer buffer) {
        buffer.putDouble(this.acceleration);
        buffer.putDouble(this.slope);
    }

    @Override
    public void restoreSynchronizedState(ByteBuffer buffer) {
        this.acceleration = buffer.getDouble();
        this.slope = buffer.getDouble();
    }

    @Override
    public void onWorkingTick(Player player, Parkourability parkourability) {
        player.fallDistance = 0.0f;
        player.setDeltaMovement(Vec3.ZERO);
    }

    @Override
    public void onStopInLocalClient(Player player) {
        if (this.ridingZipline != null) {
            player.setDeltaMovement(RideZipline.getDeltaMovement(this.ridingZipline.getZipline(), this.speed, this.currentT).add(0.0, KeyBindings.isKeyJumpDown() != false ? 0.25 : 0.0, 0.0));
        }
        this.currentT = 0.0f;
        this.currentPos = null;
        this.acceleration = 0.0;
        this.speed = 0.0;
        this.slope = 0.0;
    }

    @Override
    public void onStop(Player player) {
        this.ridingZipline = null;
    }

    @Override
    public StaminaConsumeTiming getStaminaConsumeTiming() {
        return StaminaConsumeTiming.OnWorking;
    }
}

