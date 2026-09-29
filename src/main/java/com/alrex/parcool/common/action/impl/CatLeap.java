package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.api.SoundEvents;
import com.alrex.parcool.client.animation.impl.CatLeapAnimator;
import com.alrex.parcool.client.input.KeyRecorder;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.StaminaConsumeTiming;
import com.alrex.parcool.common.action.impl.ChargeJump;
import com.alrex.parcool.common.action.impl.FastRun;
import com.alrex.parcool.common.action.impl.HideInBlock;
import com.alrex.parcool.common.action.impl.Roll;
import com.alrex.parcool.common.action.impl.Tap;
import com.alrex.parcool.common.attachment.client.Animation;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.config.ParCoolConfig;
import java.nio.ByteBuffer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import com.alrex.parcool.platform.dist.Dist;
import com.alrex.parcool.platform.dist.OnlyIn;

public class CatLeap
extends Action {
    private int coolTimeTick = 0;
    private boolean ready = false;
    private int readyTick = 0;
    private static final int MAX_COOL_TIME_TICK = 30;

    @Override
    public void onTick(Player player, Parkourability parkourability) {
        if (this.coolTimeTick > 0) {
            --this.coolTimeTick;
        }
    }

    @Override
    public void onClientTick(Player player, Parkourability parkourability) {
        if (player.isLocalPlayer()) {
            if (KeyRecorder.keySneak.isPressed() && parkourability.get(FastRun.class).getNotDashTick(parkourability.getAdditionalProperties()) < 10) {
                this.ready = true;
            }
            if (this.ready) {
                ++this.readyTick;
            }
            if (this.readyTick > 10) {
                this.ready = false;
                this.readyTick = 0;
            }
        }
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public boolean canStart(Player player, Parkourability parkourability, ByteBuffer startInfo) {
        Vec3 movement = player.getDeltaMovement();
        if (movement.lengthSqr() < 0.001) {
            return false;
        }
        movement = movement.multiply(1.0, 0.0, 1.0).normalize();
        startInfo.putDouble(movement.x()).putDouble(movement.z());
        return player.onGround() && !player.isInWater() && this.coolTimeTick <= 0 && this.readyTick > 0 && parkourability.get(ChargeJump.class).getChargingTick() < 9 && !parkourability.get(HideInBlock.class).isDoing() && !parkourability.get(Roll.class).isDoing() && !parkourability.get(Tap.class).isDoing() && KeyRecorder.keySneak.isReleased();
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public boolean canContinue(Player player, Parkourability parkourability) {
        return (this.getDoingTick() <= 1 || !player.onGround()) && !player.isFallFlying() && !player.isInWaterOrBubble() && !player.isInLava();
    }

    @Override
    public void onStartInLocalClient(Player player, Parkourability parkourability, ByteBuffer startData) {
        Vec3 jumpDirection = new Vec3(startData.getDouble(), 0.0, startData.getDouble());
        if (ParCoolConfig.Client.Booleans.EnableActionSounds.get().booleanValue()) {
            player.playSound((SoundEvent)SoundEvents.CATLEAP.get(), 1.0f, 1.0f);
        }
        this.coolTimeTick = 30;
        this.spawnJumpEffect(player, jumpDirection);
        player.jumpFromGround();
        Vec3 motionVec = player.getDeltaMovement();
        player.setDeltaMovement(jumpDirection.x(), motionVec.y() * 1.16667, jumpDirection.z());
        Animation animation = Animation.get(player);
        if (animation != null) {
            animation.setAnimator(new CatLeapAnimator());
        }
    }

    @Override
    public void onStartInOtherClient(Player player, Parkourability parkourability, ByteBuffer startData) {
        Vec3 jumpDirection = new Vec3(startData.getDouble(), 0.0, startData.getDouble());
        if (ParCoolConfig.Client.Booleans.EnableActionSounds.get().booleanValue()) {
            player.playSound((SoundEvent)SoundEvents.CATLEAP.get(), 1.0f, 1.0f);
        }
        this.spawnJumpEffect(player, jumpDirection);
        Animation animation = Animation.get(player);
        if (animation != null) {
            animation.setAnimator(new CatLeapAnimator());
        }
    }

    @Override
    public boolean wantsToShowStatusBar(LocalPlayer player, Parkourability parkourability) {
        return this.coolTimeTick > 0;
    }

    @Override
    public float getStatusValue(LocalPlayer player, Parkourability parkourability) {
        return (float)this.coolTimeTick / 30.0f;
    }

    @Override
    public StaminaConsumeTiming getStaminaConsumeTiming() {
        return StaminaConsumeTiming.OnStart;
    }

    @OnlyIn(value=Dist.CLIENT)
    private void spawnJumpEffect(Player player, Vec3 jumpDirection) {
        Vec3 pos;
        Vec3 blockPosVec;
        BlockPos blockpos;
        if (!ParCoolConfig.Client.Booleans.EnableActionParticles.get().booleanValue()) {
            return;
        }
        Level level = player.level();
        if (!level.isLoaded(blockpos = new BlockPos(Mth.floor((double)(blockPosVec = (pos = player.position()).add(0.0, -0.2, 0.0)).x()), Mth.floor((double)blockPosVec.y()), Mth.floor((double)blockPosVec.z())))) {
            return;
        }
        float width = player.getBbWidth();
        BlockState blockstate = level.getBlockState(blockpos);
        if (blockstate.getRenderShape() != RenderShape.INVISIBLE) {
            for (int i = 0; i < 20; ++i) {
                Vec3 particlePos = new Vec3(pos.x() + (jumpDirection.x() * -0.5 + player.getRandom().nextDouble() - 0.5) * (double)width, pos.y() + 0.1, pos.z() + (jumpDirection.z() * -0.5 + player.getRandom().nextDouble() - 0.5) * (double)width);
                Vec3 particleSpeed = particlePos.subtract(pos).normalize().scale(2.5 + 8.0 * player.getRandom().nextDouble()).add(0.0, 1.5, 0.0);
                level.addParticle((ParticleOptions)new BlockParticleOption(ParticleTypes.BLOCK, blockstate).setPos(blockpos), particlePos.x(), particlePos.y(), particlePos.z(), particleSpeed.x(), particleSpeed.y(), particleSpeed.z());
            }
        }
    }
}

