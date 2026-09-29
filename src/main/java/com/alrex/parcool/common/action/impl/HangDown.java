package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.platform.attachment.AttachmentHolder;

import com.alrex.parcool.api.SoundEvents;
import com.alrex.parcool.client.animation.impl.HangAnimator;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.BehaviorEnforcer;
import com.alrex.parcool.common.action.StaminaConsumeTiming;
import com.alrex.parcool.common.action.impl.ClingToCliff;
import com.alrex.parcool.common.action.impl.JumpFromBar;
import com.alrex.parcool.common.attachment.Attachments;
import com.alrex.parcool.common.attachment.client.Animation;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.common.attachment.common.ReadonlyStamina;
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

public class HangDown
extends Action {
    private static final BehaviorEnforcer.ID ID_SNEAK_CANCEL = BehaviorEnforcer.newID();
    private double bodySwingAngleFactor = 0.0;
    private float armSwingAmount = 0.0f;
    private boolean orthogonalToBar = false;
    private BarAxis hangingBarAxis = null;

    public float getArmSwingAmount() {
        return this.armSwingAmount;
    }

    public double getBodySwingAngleFactor() {
        return this.bodySwingAngleFactor;
    }

    public boolean isOrthogonalToBar() {
        return this.orthogonalToBar;
    }

    @Nullable
    public BarAxis getHangingBarAxis() {
        return this.hangingBarAxis;
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public boolean canStart(Player player, Parkourability parkourability, ByteBuffer startInfo) {
        startInfo.putDouble(Math.max(-1.0, Math.min(1.0, 3.0 * player.getLookAngle().multiply(1.0, 0.0, 1.0).normalize().dot(player.getDeltaMovement()))));
        return Math.abs(player.getDeltaMovement().y) < 0.2 && KeyBindings.getKeyHangDown().isDown() && !parkourability.get(JumpFromBar.class).isDoing() && !parkourability.get(ClingToCliff.class).isDoing() && WorldUtil.getHangableBars((LivingEntity)player) != null && (KeyBindings.getKeyHangDown().getKey().equals((Object)KeyBindings.getKeySneak().getKey()) || !player.isShiftKeyDown());
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public boolean canContinue(Player player, Parkourability parkourability) {
        return !((ReadonlyStamina)AttachmentHolder.getData(player, Attachments.STAMINA)).isExhausted() && KeyBindings.getKeyHangDown().isDown() && parkourability.getActionInfo().can(HangDown.class) && !parkourability.get(JumpFromBar.class).isDoing() && !parkourability.get(ClingToCliff.class).isDoing() && WorldUtil.getHangableBars((LivingEntity)player) != null;
    }

    private void setup(Player player, ByteBuffer startData) {
        this.armSwingAmount = 0.0f;
        this.bodySwingAngleFactor = startData.getDouble();
        this.hangingBarAxis = WorldUtil.getHangableBars((LivingEntity)player);
        Vec3 bodyVec = VectorUtil.fromYawDegree(player.yBodyRot);
        this.orthogonalToBar = this.hangingBarAxis == BarAxis.X && Math.abs(bodyVec.x) < Math.abs(bodyVec.z) || this.hangingBarAxis == BarAxis.Z && Math.abs(bodyVec.z) < Math.abs(bodyVec.x);
        player.setDeltaMovement(0.0, 0.0, 0.0);
        Animation animation = Animation.get(player);
        if (animation != null) {
            animation.setAnimator(new HangAnimator());
        }
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public void onStartInLocalClient(Player player, Parkourability parkourability, ByteBuffer startData) {
        this.setup(player, startData);
        if (!KeyBindings.getKeyHangDown().getKey().equals((Object)KeyBindings.getKeySneak().getKey())) {
            parkourability.getBehaviorEnforcer().addMarkerCancellingSneak(ID_SNEAK_CANCEL, this::isDoing);
        }
        if (ParCoolConfig.Client.Booleans.EnableActionSounds.get().booleanValue()) {
            player.playSound((SoundEvent)SoundEvents.HANG_DOWN.get(), 1.0f, 1.0f);
        }
    }

    @Override
    public void onStartInOtherClient(Player player, Parkourability parkourability, ByteBuffer startData) {
        this.setup(player, startData);
        if (ParCoolConfig.Client.Booleans.EnableActionSounds.get().booleanValue()) {
            player.playSound((SoundEvent)SoundEvents.HANG_DOWN.get(), 1.0f, 1.0f);
        }
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public void onWorkingTickInLocalClient(Player player, Parkourability parkourability) {
        Vec3 bodyVec = VectorUtil.fromYawDegree(player.yBodyRot);
        double speed = 0.1;
        double xSpeed = 0.0;
        double zSpeed = 0.0;
        if (this.orthogonalToBar) {
            if (this.hangingBarAxis == BarAxis.X) {
                xSpeed = (double)(bodyVec.z > 0.0 ? 1 : -1) * 0.1;
            } else {
                zSpeed = (double)(bodyVec.x > 0.0 ? 1 : -1) * 0.1;
            }
            if (KeyBindings.isKeyLeftDown().booleanValue()) {
                player.setDeltaMovement(xSpeed, 0.0, -zSpeed);
            } else if (KeyBindings.isKeyRightDown().booleanValue()) {
                player.setDeltaMovement(-xSpeed, 0.0, zSpeed);
            } else {
                player.setDeltaMovement(0.0, 0.0, 0.0);
            }
        } else {
            if (this.hangingBarAxis == BarAxis.X) {
                xSpeed = (double)(bodyVec.x > 0.0 ? 1 : -1) * 0.1;
            } else {
                zSpeed = (double)(bodyVec.z > 0.0 ? 1 : -1) * 0.1;
            }
            if (KeyBindings.isKeyForwardDown().booleanValue()) {
                player.setDeltaMovement(xSpeed, 0.0, zSpeed);
            } else if (KeyBindings.isKeyBackDown().booleanValue()) {
                player.setDeltaMovement(-xSpeed, 0.0, -zSpeed);
            } else {
                player.setDeltaMovement(0.0, 0.0, 0.0);
            }
        }
        this.armSwingAmount += (float)player.getDeltaMovement().multiply(1.0, 0.0, 1.0).lengthSqr();
    }

    @Override
    public void onWorkingTickInClient(Player player, Parkourability parkourability) {
        this.hangingBarAxis = WorldUtil.getHangableBars((LivingEntity)player);
        Vec3 bodyVec = VectorUtil.fromYawDegree(player.yBodyRot);
        boolean bl = this.orthogonalToBar = this.hangingBarAxis == BarAxis.X && Math.abs(bodyVec.x) < Math.abs(bodyVec.z) || this.hangingBarAxis == BarAxis.Z && Math.abs(bodyVec.z) < Math.abs(bodyVec.x);
        this.bodySwingAngleFactor = this.orthogonalToBar ? (this.bodySwingAngleFactor /= 1.05) : (this.bodySwingAngleFactor /= 1.5);
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
        if (this.isDoing()) {
            if (this.hangingBarAxis == null) {
                return;
            }
            Vec3 bodyVec = VectorUtil.fromYawDegree(player.yBodyRot).normalize();
            Vec3 lookVec = player.getLookAngle();
            Vec3 idealLookVec = Math.abs(lookVec.x) > Math.abs(lookVec.z) ? new Vec3(lookVec.x > 0.0 ? 1.0 : -1.0, 0.0, 0.0) : new Vec3(0.0, 0.0, lookVec.z > 0.0 ? 1.0 : -1.0);
            double differenceAngle = Math.acos(bodyVec.dot(idealLookVec));
            player.setYBodyRot((float)VectorUtil.toYawDegree(idealLookVec.yRot((float)(differenceAngle /= 4.0))));
        }
    }

    @Override
    public StaminaConsumeTiming getStaminaConsumeTiming() {
        return StaminaConsumeTiming.OnWorking;
    }

    public static enum BarAxis {
        X,
        Z;

    }
}

