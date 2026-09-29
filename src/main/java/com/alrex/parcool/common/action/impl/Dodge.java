package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.api.SoundEvents;
import com.alrex.parcool.client.animation.impl.DodgeAnimator;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.client.input.KeyRecorder;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.BehaviorEnforcer;
import com.alrex.parcool.common.action.StaminaConsumeTiming;
import com.alrex.parcool.common.action.impl.ClingToCliff;
import com.alrex.parcool.common.action.impl.Crawl;
import com.alrex.parcool.common.action.impl.Roll;
import com.alrex.parcool.common.action.impl.Tap;
import com.alrex.parcool.common.attachment.client.Animation;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.common.info.ActionInfo;
import com.alrex.parcool.config.ParCoolConfig;
import com.alrex.parcool.extern.AdditionalMods;
import com.alrex.parcool.utilities.VectorUtil;
import java.nio.ByteBuffer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import com.alrex.parcool.platform.dist.Dist;
import com.alrex.parcool.platform.dist.OnlyIn;

public class Dodge
extends Action {
    public static final int MAX_TICK = 11;
    private static final BehaviorEnforcer.ID ID_JUMP_CANCEL = BehaviorEnforcer.newID();
    private static final BehaviorEnforcer.ID ID_DESCEND_EDGE = BehaviorEnforcer.newID();
    private DodgeDirection dodgeDirection = null;
    private int coolTime = 0;
    private int successivelyCount = 0;
    private int successivelyCoolTick = 0;

    private static int getMaxCoolTime(ActionInfo info) {
        return Math.max(info.getClientSetting().get(ParCoolConfig.Client.Integers.DodgeCoolTime), info.getServerLimitation().get(ParCoolConfig.Server.Integers.DodgeCoolTime));
    }

    private static int getMaxSuccessiveDodge(ActionInfo info) {
        return Math.min(info.getClientSetting().get(ParCoolConfig.Client.Integers.MaxSuccessiveDodgeCount), info.getServerLimitation().get(ParCoolConfig.Server.Integers.MaxSuccessiveDodgeCount));
    }

    private static int getSuccessiveCoolTime(ActionInfo info) {
        return Math.max(info.getClientSetting().get(ParCoolConfig.Client.Integers.SuccessiveDodgeCoolTime), info.getServerLimitation().get(ParCoolConfig.Server.Integers.SuccessiveDodgeCoolTime));
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public void onClientTick(Player player, Parkourability parkourability) {
        if (this.coolTime > 0) {
            --this.coolTime;
        }
        if (this.successivelyCoolTick > 0) {
            --this.successivelyCoolTick;
        } else {
            this.successivelyCount = 0;
        }
    }

    @Override
    public StaminaConsumeTiming getStaminaConsumeTiming() {
        return StaminaConsumeTiming.OnStart;
    }

    public double getSpeedModifier(ActionInfo info) {
        return Math.min(info.getClientSetting().get(ParCoolConfig.Client.Doubles.DodgeSpeedModifier), info.getServerLimitation().get(ParCoolConfig.Server.Doubles.MaxDodgeSpeedModifier));
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public boolean canStart(Player player, Parkourability parkourability, ByteBuffer startInfo) {
        boolean enabledDoubleTap = ParCoolConfig.Client.Booleans.EnableDoubleTappingForDodge.get();
        DodgeDirection direction = null;
        Vec3 dodgeVec = KeyRecorder.getLastMoveVector();
        if (enabledDoubleTap) {
            if (KeyRecorder.keyBack.isDoubleTapped()) {
                direction = DodgeDirection.Back;
            }
            if (KeyRecorder.keyLeft.isDoubleTapped()) {
                direction = DodgeDirection.Left;
            }
            if (KeyRecorder.keyRight.isDoubleTapped()) {
                direction = DodgeDirection.Right;
            }
        }
        if (direction == null && KeyRecorder.keyDodge.isPressed()) {
            if (KeyBindings.isKeyBackDown().booleanValue()) {
                direction = DodgeDirection.Back;
            }
            if (KeyBindings.isKeyForwardDown().booleanValue()) {
                direction = DodgeDirection.Front;
            }
            if (KeyBindings.isKeyLeftDown().booleanValue()) {
                direction = DodgeDirection.Left;
            }
            if (KeyBindings.isKeyRightDown().booleanValue()) {
                direction = DodgeDirection.Right;
            }
            if (direction != null) {
                dodgeVec = KeyBindings.getCurrentMoveVector();
            }
        }
        if (direction == null || dodgeVec == null) {
            return false;
        }
        direction = AdditionalMods.betterThirdPerson().handleCustomCameraRotationForDodge(direction);
        direction = AdditionalMods.shoulderSurfing().handleCustomCameraRotationForDodge(direction);
        startInfo.putInt(direction.ordinal());
        startInfo.putDouble(dodgeVec.x);
        startInfo.putDouble(dodgeVec.z);
        return (parkourability.getAdditionalProperties().getLandingTick() > 5 || parkourability.getAdditionalProperties().getPreviousNotLandingTick() < 2) && player.onGround() && !this.isInSuccessiveCoolDown(parkourability.getActionInfo()) && this.coolTime <= 0 && !player.isInWaterOrBubble() && player.onGround() && !player.isInWaterOrBubble() && !player.isShiftKeyDown() && !parkourability.get(Crawl.class).isDoing() && !parkourability.get(Roll.class).isDoing() && !parkourability.get(Tap.class).isDoing();
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public boolean canContinue(Player player, Parkourability parkourability) {
        return !parkourability.get(Roll.class).isDoing() && !parkourability.get(ClingToCliff.class).isDoing() && this.getDoingTick() < 11 && !player.isInWaterOrBubble() && !player.isFallFlying() && !player.getAbilities().flying;
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public void onStartInLocalClient(Player player, Parkourability parkourability, ByteBuffer startData) {
        this.dodgeDirection = DodgeDirection.values()[startData.getInt()];
        Vec3 dodgeVec = new Vec3(startData.getDouble(), 0.0, startData.getDouble());
        this.coolTime = Dodge.getMaxCoolTime(parkourability.getActionInfo());
        if (this.successivelyCount < Dodge.getMaxSuccessiveDodge(parkourability.getActionInfo())) {
            ++this.successivelyCount;
        }
        if (ParCoolConfig.Client.Booleans.EnableActionSounds.get().booleanValue()) {
            player.playSound((SoundEvent)SoundEvents.DODGE.get(), 1.0f, 1.0f);
        }
        this.successivelyCoolTick = Dodge.getSuccessiveCoolTime(parkourability.getActionInfo());
        if (!player.onGround()) {
            return;
        }
        Entity cameraEntity = Minecraft.getInstance().getCameraEntity();
        float cameraYRot = cameraEntity != null ? cameraEntity.getYRot() : 0.0f;
        dodgeVec = VectorUtil.rotateYDegrees(dodgeVec, cameraYRot);
        dodgeVec = dodgeVec.scale(0.9 * this.getSpeedModifier(parkourability.getActionInfo()));
        if (AdditionalMods.isCameraDecoupled()) {
            player.setYRot(VectorUtil.toYaw(dodgeVec));
        }
        player.setDeltaMovement(dodgeVec);
        Animation animation = Animation.get(player);
        if (animation != null) {
            animation.setAnimator(new DodgeAnimator(this.dodgeDirection));
        }
        parkourability.getBehaviorEnforcer().addMarkerCancellingJump(ID_JUMP_CANCEL, this::isDoing);
        if (!parkourability.getClientInfo().get(ParCoolConfig.Client.Booleans.CanGetOffStepsWhileDodge).booleanValue()) {
            parkourability.getBehaviorEnforcer().addMarkerCancellingDescendFromEdge(ID_DESCEND_EDGE, this::isDoing);
        }
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public void onStartInOtherClient(Player player, Parkourability parkourability, ByteBuffer startData) {
        Animation animation;
        this.dodgeDirection = DodgeDirection.values()[startData.getInt()];
        if (ParCoolConfig.Client.Booleans.EnableActionSounds.get().booleanValue()) {
            player.playSound((SoundEvent)SoundEvents.DODGE.get(), 1.0f, 1.0f);
        }
        if ((animation = Animation.get(player)) != null) {
            animation.setAnimator(new DodgeAnimator(this.dodgeDirection));
        }
    }

    public int getCoolTime() {
        return this.coolTime;
    }

    public int getSuccessivelyCoolTick() {
        return this.successivelyCoolTick;
    }

    public boolean isInSuccessiveCoolDown(ActionInfo info) {
        return this.successivelyCount >= Dodge.getMaxSuccessiveDodge(info);
    }

    public float getCoolDownPhase(ActionInfo info) {
        int maxCoolTime = Dodge.getMaxCoolTime(info);
        int successiveMaxCoolTime = Dodge.getSuccessiveCoolTime(info);
        return Math.min((float)(maxCoolTime - this.getCoolTime()) / (float)maxCoolTime, this.isInSuccessiveCoolDown(info) ? (float)(successiveMaxCoolTime - this.getSuccessivelyCoolTick()) / (float)successiveMaxCoolTime : 1.0f);
    }

    @Override
    public boolean wantsToShowStatusBar(LocalPlayer player, Parkourability parkourability) {
        return this.coolTime > 0 || this.isInSuccessiveCoolDown(parkourability.getActionInfo());
    }

    @Override
    public float getStatusValue(LocalPlayer player, Parkourability parkourability) {
        ActionInfo info = parkourability.getActionInfo();
        int maxCoolTime = Dodge.getMaxCoolTime(info);
        int successiveMaxCoolTime = Dodge.getSuccessiveCoolTime(info);
        return Math.max((float)this.getCoolTime() / (float)maxCoolTime, this.isInSuccessiveCoolDown(info) ? (float)this.getSuccessivelyCoolTick() / (float)successiveMaxCoolTime : 0.0f);
    }

    public static enum DodgeDirection {
        Front,
        Back,
        Left,
        Right;


        public DodgeDirection inverse() {
            switch (this.ordinal()) {
                case 0: {
                    return Back;
                }
                case 1: {
                    return Front;
                }
                case 2: {
                    return Right;
                }
                case 3: {
                    return Left;
                }
            }
            return Front;
        }

        public DodgeDirection right() {
            switch (this.ordinal()) {
                case 0: {
                    return Right;
                }
                case 3: {
                    return Back;
                }
                case 1: {
                    return Left;
                }
                case 2: {
                    return Front;
                }
            }
            return Front;
        }

        public DodgeDirection left() {
            switch (this.ordinal()) {
                case 0: {
                    return Left;
                }
                case 2: {
                    return Back;
                }
                case 1: {
                    return Right;
                }
                case 3: {
                    return Front;
                }
            }
            return Front;
        }
    }
}

