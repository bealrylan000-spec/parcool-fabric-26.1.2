package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.api.SoundEvents;
import com.alrex.parcool.client.animation.impl.KongVaultAnimator;
import com.alrex.parcool.client.animation.impl.SpeedVaultAnimator;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.StaminaConsumeTiming;
import com.alrex.parcool.common.action.impl.FastRun;
import com.alrex.parcool.common.attachment.client.Animation;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.config.ParCoolConfig;
import com.alrex.parcool.utilities.WorldUtil;
import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import com.alrex.parcool.platform.dist.Dist;
import com.alrex.parcool.platform.dist.OnlyIn;

public class Vault
extends Action {
    public static final int MAX_TICK = 11;
    private double stepHeight = 0.0;
    private Vec3 stepDirection = null;
    @Nullable
    private AnimationType currentAnimation;

    @Nullable
    public AnimationType getCurrentAnimation() {
        if (!this.isDoing()) {
            return null;
        }
        return this.currentAnimation;
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public boolean canStart(Player player, Parkourability parkourability, ByteBuffer startInfo) {
        Vec3 lookVec = player.getLookAngle();
        lookVec = new Vec3(lookVec.x(), 0.0, lookVec.z()).normalize();
        Vec3 step = WorldUtil.getVaultableStep((LivingEntity)player);
        if (step == null) {
            return false;
        }
        step = step.normalize();
        Vec3 dividedVec = new Vec3(lookVec.x() * step.x() + lookVec.z() * step.z(), 0.0, -lookVec.x() * step.z() + lookVec.z() * step.x()).normalize();
        if (dividedVec.x() < 0.707106) {
            return false;
        }
        AnimationType animationType = switch (((TypeSelectionMode)((Object)ParCoolConfig.Client.getInstance().VaultAnimationMode.get())).ordinal()) {
            case 1 -> AnimationType.KONG_VAULT;
            case 0 -> dividedVec.z() > 0.0 ? AnimationType.SPEED_VAULT_RIGHT : AnimationType.SPEED_VAULT_LEFT;
            default -> dividedVec.x() > 0.99 ? AnimationType.KONG_VAULT : (dividedVec.z() > 0.0 ? AnimationType.SPEED_VAULT_RIGHT : AnimationType.SPEED_VAULT_LEFT);
        };
        double wallHeight = WorldUtil.getWallHeight((LivingEntity)player);
        startInfo.put((byte)animationType.ordinal()).putDouble(step.x()).putDouble(step.y()).putDouble(step.z()).putDouble(wallHeight);
        return !(ParCoolConfig.Client.Booleans.VaultKeyPressedNeeded.get() != false && !KeyBindings.getKeyVault().isDown() || player.isInWater() || !parkourability.get(FastRun.class).canActWithRunning(player) || !player.onGround() && ParCoolConfig.Client.Booleans.EnableVaultInAir.get() == false || !(wallHeight > (double)player.getBbHeight() * 0.44));
    }

    @Override
    public boolean canContinue(Player player, Parkourability parkourability) {
        return this.getDoingTick() < 11;
    }

    private int getVaultAnimateTime() {
        return 2;
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public void onStartInLocalClient(Player player, Parkourability parkourability, ByteBuffer startData) {
        this.currentAnimation = AnimationType.values()[startData.get()];
        if (ParCoolConfig.Client.Booleans.EnableActionSounds.get().booleanValue()) {
            player.playSound((SoundEvent)SoundEvents.VAULT.get(), 1.0f, 1.0f);
        }
        this.stepDirection = new Vec3(startData.getDouble(), startData.getDouble(), startData.getDouble());
        this.stepHeight = startData.getDouble();
        Animation animation = Animation.get(player);
        if (animation != null && this.currentAnimation != null) {
            switch (this.currentAnimation.ordinal()) {
                case 0: {
                    animation.setAnimator(new SpeedVaultAnimator(SpeedVaultAnimator.Type.Right));
                    break;
                }
                case 1: {
                    animation.setAnimator(new SpeedVaultAnimator(SpeedVaultAnimator.Type.Left));
                    break;
                }
                case 2: {
                    animation.setAnimator(new KongVaultAnimator());
                }
            }
        }
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public void onStartInOtherClient(Player player, Parkourability parkourability, ByteBuffer startData) {
        Animation animation;
        this.currentAnimation = AnimationType.values()[startData.get()];
        if (ParCoolConfig.Client.Booleans.EnableActionSounds.get().booleanValue()) {
            player.playSound((SoundEvent)SoundEvents.VAULT.get(), 1.0f, 1.0f);
        }
        if ((animation = Animation.get(player)) != null && this.currentAnimation != null) {
            switch (this.currentAnimation.ordinal()) {
                case 0: {
                    animation.setAnimator(new SpeedVaultAnimator(SpeedVaultAnimator.Type.Right));
                    break;
                }
                case 1: {
                    animation.setAnimator(new SpeedVaultAnimator(SpeedVaultAnimator.Type.Left));
                    break;
                }
                case 2: {
                    animation.setAnimator(new KongVaultAnimator());
                }
            }
        }
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public void onWorkingTickInLocalClient(Player player, Parkourability parkourability) {
        if (this.stepDirection == null) {
            return;
        }
        if (this.getDoingTick() < this.getVaultAnimateTime()) {
            player.setDeltaMovement(this.stepDirection.x() / 10.0, (this.stepHeight + 0.02) / (double)this.getVaultAnimateTime() / ((double)player.getBbHeight() / 1.8), this.stepDirection.z() / 10.0);
        } else if (this.getDoingTick() == this.getVaultAnimateTime()) {
            this.stepDirection = this.stepDirection.normalize();
            player.setDeltaMovement(this.stepDirection.x() * 0.45, 0.075 * ((double)player.getBbHeight() / 1.8), this.stepDirection.z() * 0.45);
        }
    }

    @Override
    public StaminaConsumeTiming getStaminaConsumeTiming() {
        return StaminaConsumeTiming.OnStart;
    }

    public static enum AnimationType {
        SPEED_VAULT_RIGHT,
        SPEED_VAULT_LEFT,
        KONG_VAULT;

    }

    public static enum TypeSelectionMode {
        SpeedVault,
        KongVault,
        Dynamic;

    }
}

