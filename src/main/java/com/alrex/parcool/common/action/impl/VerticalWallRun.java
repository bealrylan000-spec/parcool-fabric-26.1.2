package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.api.SoundEvents;
import com.alrex.parcool.client.animation.impl.VerticalWallRunAnimator;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.StaminaConsumeTiming;
import com.alrex.parcool.common.action.impl.CatLeap;
import com.alrex.parcool.common.action.impl.ClingToCliff;
import com.alrex.parcool.common.action.impl.Crawl;
import com.alrex.parcool.common.action.impl.FastRun;
import com.alrex.parcool.common.action.impl.Flipping;
import com.alrex.parcool.common.action.impl.HorizontalWallRun;
import com.alrex.parcool.common.action.impl.Vault;
import com.alrex.parcool.common.action.impl.WallSlide;
import com.alrex.parcool.common.attachment.client.Animation;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.config.ParCoolConfig;
import com.alrex.parcool.utilities.VectorUtil;
import com.alrex.parcool.utilities.WorldUtil;
import java.nio.ByteBuffer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import com.alrex.parcool.platform.dist.Dist;
import com.alrex.parcool.platform.dist.OnlyIn;
import com.alrex.parcool.platform.event.client.RenderFrameEvent;

public class VerticalWallRun
extends Action {
    private double playerYSpeed = 0.0;
    private Vec3 wallDirection = null;

    @Override
    public void onTick(Player player, Parkourability parkourability) {
        this.playerYSpeed = player.getDeltaMovement().y();
    }

    @Override
    public boolean canStart(Player player, Parkourability parkourability, ByteBuffer startInfo) {
        boolean able;
        int tickAfterJump = parkourability.getAdditionalProperties().getTickAfterLastJump();
        Vec3 lookVec = player.getLookAngle();
        boolean bl = able = Math.abs(player.getDeltaMovement().y()) <= (double)(player.getBbHeight() / 5.0f) && 4 < tickAfterJump && tickAfterJump < 13 && this.getNotDoingTick() > 15 && !player.isFallFlying() && KeyBindings.isKeyJumpDown() != false && !parkourability.get(ClingToCliff.class).isDoing() && !parkourability.get(Crawl.class).isDoing() && !parkourability.get(CatLeap.class).isDoing() && !parkourability.get(WallSlide.class).isDoing() && !parkourability.get(HorizontalWallRun.class).isDoing() && !parkourability.get(Vault.class).isDoing() && !parkourability.get(Flipping.class).isDoing() && parkourability.get(FastRun.class).getNotDashTick(parkourability.getAdditionalProperties()) < 8 && parkourability.getAdditionalProperties().getLastSprintingTick() > 12 && lookVec.y() > 0.0;
        if (able) {
            double height;
            Vec3 wall = WorldUtil.getWall((LivingEntity)player);
            if (wall == null) {
                return false;
            }
            if ((wall = wall.normalize()).dot(VectorUtil.fromYawDegree(player.getYHeadRot())) > 0.93 && (height = WorldUtil.getWallHeight((LivingEntity)player, wall, (double)player.getBbHeight() * 2.2, 0.2)) > (double)player.getBbHeight() * 1.3) {
                BlockPos targetBlock = new BlockPos(Mth.floor((double)(player.getX() + wall.x())), Mth.floor((double)(player.getBoundingBox().minY + (double)player.getBbHeight() * 0.5)), Mth.floor((double)(player.getZ() + wall.z())));
                if (!player.getCommandSenderWorld().isLoaded(targetBlock)) {
                    return false;
                }
                float slipperiness = player.getCommandSenderWorld().getBlockState(targetBlock).getFriction((LevelReader)player.getCommandSenderWorld(), targetBlock, (Entity)player);
                startInfo.putDouble(height);
                startInfo.putFloat(slipperiness);
                startInfo.putDouble(wall.x());
                startInfo.putDouble(wall.y());
                startInfo.putDouble(wall.z());
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canContinue(Player player, Parkourability parkourability) {
        Vec3 wall = WorldUtil.getWall((LivingEntity)player);
        if (wall == null) {
            return false;
        }
        return (wall = wall.normalize()).dot(VectorUtil.fromYawDegree(player.getYHeadRot())) > 0.93 && this.playerYSpeed > 0.0 || this.getDoingTick() > 30;
    }

    @Override
    public void onStartInLocalClient(Player player, Parkourability parkourability, ByteBuffer startData) {
        double height = startData.getDouble();
        float slipperiness = startData.getFloat();
        if (ParCoolConfig.Client.Booleans.EnableActionSounds.get().booleanValue()) {
            player.playSound((SoundEvent)SoundEvents.VERTICAL_WALL_RUN.get(), 1.0f, 1.0f);
        }
        player.setDeltaMovement(player.getDeltaMovement().multiply(1.0, 0.0, 1.0).add(0.0, (slipperiness <= 0.8f ? 0.32 : 0.16) * Math.sqrt(height), 0.0));
        this.onStartInOtherClient(player, parkourability, startData);
    }

    @Override
    public void onStartInOtherClient(Player player, Parkourability parkourability, ByteBuffer startData) {
        Animation animation;
        startData.position(12);
        this.wallDirection = new Vec3(startData.getDouble(), startData.getDouble(), startData.getDouble());
        if (ParCoolConfig.Client.Booleans.EnableActionSounds.get().booleanValue()) {
            player.playSound((SoundEvent)SoundEvents.VERTICAL_WALL_RUN.get(), 1.0f, 1.0f);
        }
        if ((animation = Animation.get(player)) != null) {
            animation.setAnimator(new VerticalWallRunAnimator());
        }
    }

    @Override
    public void onRenderTick(RenderFrameEvent event, Player player, Parkourability parkourability) {
        if (this.wallDirection != null && this.isDoing()) {
            player.setYHeadRot((float)VectorUtil.toYawDegree(this.wallDirection));
            player.yBodyRotO = player.yBodyRot = player.getYHeadRot();
        }
    }

    @Override
    public void onWorkingTickInClient(Player player, Parkourability parkourability) {
        this.spawnRunningParticle(player);
    }

    @Override
    public StaminaConsumeTiming getStaminaConsumeTiming() {
        return StaminaConsumeTiming.OnStart;
    }

    @OnlyIn(value=Dist.CLIENT)
    public void spawnRunningParticle(Player player) {
        Vec3 pos;
        BlockPos leanedBlock;
        if (!ParCoolConfig.Client.Booleans.EnableActionParticles.get().booleanValue()) {
            return;
        }
        if (this.wallDirection == null) {
            return;
        }
        Level level = player.level();
        if (!level.isLoaded(leanedBlock = new BlockPos(Mth.floor((double)((pos = player.position()).x() + this.wallDirection.x())), Mth.floor((double)(pos.y() + (double)player.getBbHeight() * 0.25)), Mth.floor((double)(pos.z() + this.wallDirection.z()))))) {
            return;
        }
        float width = player.getBbWidth();
        BlockState blockstate = level.getBlockState(leanedBlock);
        Vec3 normalizedWallVec = this.wallDirection.normalize();
        Vec3 orthogonalToWallVec = normalizedWallVec.yRot(1.5707964f);
        if (blockstate.getRenderShape() != RenderShape.INVISIBLE) {
            Vec3 particlePos = new Vec3(pos.x() + (normalizedWallVec.x() * 0.4 + orthogonalToWallVec.x() * (player.getRandom().nextDouble() - 0.5)) * (double)width, pos.y() + 0.1 + 0.3 * player.getRandom().nextDouble(), pos.z() + (normalizedWallVec.z() * 0.4 + orthogonalToWallVec.z() * (player.getRandom().nextDouble() - 0.5)) * (double)width);
            Vec3 particleSpeed = normalizedWallVec.reverse().yRot((float)(0.6283185307179586 * (player.getRandom().nextDouble() - 0.5))).scale(2.0 + 4.0 * player.getRandom().nextDouble()).add(0.0, 0.5, 0.0);
            level.addParticle((ParticleOptions)new BlockParticleOption(ParticleTypes.BLOCK, blockstate).setPos(leanedBlock), particlePos.x(), particlePos.y(), particlePos.z(), particleSpeed.x(), particleSpeed.y(), particleSpeed.z());
        }
    }
}

