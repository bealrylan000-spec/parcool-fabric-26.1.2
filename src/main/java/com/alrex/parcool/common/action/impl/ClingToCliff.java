package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.api.SoundEvents;
import com.alrex.parcool.client.animation.impl.ClingToCliffAnimator;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.client.input.KeyRecorder;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.BehaviorEnforcer;
import com.alrex.parcool.common.action.StaminaConsumeTiming;
import com.alrex.parcool.common.action.impl.ClimbUp;
import com.alrex.parcool.common.action.impl.HorizontalWallRun;
import com.alrex.parcool.common.attachment.client.Animation;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.config.ParCoolConfig;
import com.alrex.parcool.utilities.VectorUtil;
import com.alrex.parcool.utilities.WorldUtil;
import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import com.alrex.parcool.platform.dist.Dist;
import com.alrex.parcool.platform.dist.OnlyIn;
import com.alrex.parcool.platform.event.client.RenderFrameEvent;

public class ClingToCliff
extends Action {
    private static final BehaviorEnforcer.ID ID_SNEAK_CANCEL = BehaviorEnforcer.newID();
    private static final BehaviorEnforcer.ID ID_FALL_FLY_CANCEL = BehaviorEnforcer.newID();
    private float armSwingAmount = 0.0f;
    private FacingDirection facingDirection = FacingDirection.ToWall;
    @Nullable
    private Vec3 clingWallDirection = null;

    public float getArmSwingAmount() {
        return this.armSwingAmount;
    }

    @Override
    public void onWorkingTick(Player player, Parkourability parkourability) {
        player.fallDistance = 0.0f;
    }

    public FacingDirection getFacingDirection() {
        return this.facingDirection;
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public boolean canStart(Player player, Parkourability parkourability, ByteBuffer startInfo) {
        boolean value;
        boolean bl = value = player.getDeltaMovement().y() < 0.2 && !parkourability.get(HorizontalWallRun.class).isDoing() && KeyBindings.getKeyGrabWall().isDown() && (KeyBindings.getKeyGrabWall().getKey().equals((Object)KeyBindings.getKeySneak().getKey()) || !player.isShiftKeyDown());
        if (!value) {
            return false;
        }
        Vec3 wallVec = WorldUtil.getGrabbableWall((LivingEntity)player);
        if (wallVec == null) {
            return false;
        }
        startInfo.putDouble(wallVec.x()).putDouble(wallVec.z());
        return 0.5 < wallVec.normalize().dot(player.getLookAngle().multiply(1.0, 0.0, 1.0).normalize());
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public boolean canContinue(Player player, Parkourability parkourability) {
        return parkourability.getActionInfo().can(ClingToCliff.class) && this.isGrabbing() && !parkourability.get(HorizontalWallRun.class).isDoing() && !parkourability.get(ClimbUp.class).isDoing() && WorldUtil.getGrabbableWall((LivingEntity)player) != null;
    }

    private boolean isGrabbing() {
        return ParCoolConfig.Client.getInstance().ClingToCliffControl.get() == ControlType.PressKey ? KeyBindings.getKeyGrabWall().isDown() : !KeyRecorder.keyGrabWall.isPressed();
    }

    @Override
    public void onStart(Player player, Parkourability parkourability, ByteBuffer startData) {
        parkourability.getBehaviorEnforcer().addMarkerCancellingFallFlying(ID_FALL_FLY_CANCEL, this::isDoing);
        this.armSwingAmount = 0.0f;
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public void onStartInLocalClient(Player player, Parkourability parkourability, ByteBuffer startData) {
        Animation animation;
        this.clingWallDirection = new Vec3(startData.getDouble(), 0.0, startData.getDouble());
        this.facingDirection = FacingDirection.ToWall;
        this.armSwingAmount = 0.0f;
        if (!KeyBindings.getKeyGrabWall().getKey().equals((Object)KeyBindings.getKeySneak().getKey())) {
            parkourability.getBehaviorEnforcer().addMarkerCancellingSneak(ID_SNEAK_CANCEL, this::isDoing);
        }
        if (ParCoolConfig.Client.Booleans.EnableActionSounds.get().booleanValue()) {
            player.playSound((SoundEvent)SoundEvents.CLING_TO_CLIFF.get(), 1.0f, 1.0f);
        }
        if ((animation = Animation.get(player)) != null) {
            animation.setAnimator(new ClingToCliffAnimator());
        }
    }

    @Override
    public void onStartInOtherClient(Player player, Parkourability parkourability, ByteBuffer startData) {
        Animation animation;
        this.clingWallDirection = new Vec3(startData.getDouble(), 0.0, startData.getDouble());
        this.facingDirection = FacingDirection.ToWall;
        this.armSwingAmount = 0.0f;
        if (ParCoolConfig.Client.Booleans.EnableActionSounds.get().booleanValue()) {
            player.playSound((SoundEvent)SoundEvents.CLING_TO_CLIFF.get(), 1.0f, 1.0f);
        }
        if ((animation = Animation.get(player)) != null) {
            animation.setAnimator(new ClingToCliffAnimator());
        }
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public void onWorkingTickInLocalClient(Player player, Parkourability parkourability) {
        this.armSwingAmount += (float)player.getDeltaMovement().multiply(1.0, 0.0, 1.0).lengthSqr();
        if (KeyBindings.isLeftAndRightDown().booleanValue()) {
            player.setDeltaMovement(0.0, 0.0, 0.0);
        } else if (this.clingWallDirection != null && this.facingDirection == FacingDirection.ToWall) {
            Vec3 vec = this.clingWallDirection.yRot(1.5707964f).normalize().scale(0.1);
            if (KeyBindings.isKeyLeftDown().booleanValue()) {
                player.setDeltaMovement(vec);
            } else if (KeyBindings.isKeyRightDown().booleanValue()) {
                player.setDeltaMovement(vec.reverse());
            } else {
                player.setDeltaMovement(0.0, 0.0, 0.0);
            }
        } else {
            player.setDeltaMovement(0.0, 0.0, 0.0);
        }
    }

    @Override
    public void onWorkingTickInClient(Player player, Parkourability parkourability) {
        this.clingWallDirection = WorldUtil.getGrabbableWall((LivingEntity)player);
        if (this.clingWallDirection == null) {
            return;
        }
        this.clingWallDirection = this.clingWallDirection.normalize();
        Vec3 lookingAngle = player.getLookAngle().multiply(1.0, 0.0, 1.0).normalize();
        Vec3 angle = new Vec3(this.clingWallDirection.x() * lookingAngle.x() + this.clingWallDirection.z() * lookingAngle.z(), 0.0, -this.clingWallDirection.x() * lookingAngle.z() + this.clingWallDirection.z() * lookingAngle.x()).normalize();
        this.facingDirection = angle.x() > 0.342 ? FacingDirection.ToWall : (angle.z() < 0.0 ? FacingDirection.RightAgainstWall : FacingDirection.LeftAgainstWall);
    }

    @Override
    public void saveSynchronizedState(ByteBuffer buffer) {
        buffer.putFloat(this.armSwingAmount);
    }

    @Override
    public void restoreSynchronizedState(ByteBuffer buffer) {
        this.armSwingAmount = buffer.getFloat();
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public void onRenderTick(RenderFrameEvent event, Player player, Parkourability parkourability) {
        if (this.isDoing() && this.clingWallDirection != null) {
            switch (this.facingDirection.ordinal()) {
                case 0: {
                    player.setYBodyRot((float)VectorUtil.toYawDegree(this.clingWallDirection));
                    break;
                }
                case 1: {
                    player.yBodyRotO = player.yBodyRot = (float)VectorUtil.toYawDegree(this.clingWallDirection.yRot(-1.5707964f));
                    break;
                }
                case 2: {
                    player.yBodyRotO = player.yBodyRot = (float)VectorUtil.toYawDegree(this.clingWallDirection.yRot(1.5707964f));
                }
            }
        }
    }

    @Override
    public StaminaConsumeTiming getStaminaConsumeTiming() {
        return StaminaConsumeTiming.OnWorking;
    }

    public static enum FacingDirection {
        ToWall,
        RightAgainstWall,
        LeftAgainstWall;

    }

    public static enum ControlType {
        PressKey,
        Toggle;

    }
}

