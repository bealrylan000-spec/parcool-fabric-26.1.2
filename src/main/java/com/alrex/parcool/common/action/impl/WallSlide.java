package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.platform.attachment.AttachmentHolder;

import com.alrex.parcool.client.animation.impl.WallSlideAnimator;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.StaminaConsumeTiming;
import com.alrex.parcool.common.action.impl.ClingToCliff;
import com.alrex.parcool.common.action.impl.Dive;
import com.alrex.parcool.common.action.impl.Dodge;
import com.alrex.parcool.common.action.impl.FastRun;
import com.alrex.parcool.common.attachment.Attachments;
import com.alrex.parcool.common.attachment.client.Animation;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.common.attachment.common.ReadonlyStamina;
import com.alrex.parcool.common.damage.DamageSources;
import com.alrex.parcool.config.ParCoolConfig;
import com.alrex.parcool.utilities.WorldUtil;
import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
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

public class WallSlide
extends Action {
    private Vec3 leanedWallDirection = null;
    private byte particleSpawnCoolTime = 0;
    private int damageCount = 0;
    private int takenDamageCount = 0;
    private byte damageCoolTime = 0;

    @Nullable
    public Vec3 getLeanedWallDirection() {
        return this.leanedWallDirection;
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public boolean canStart(Player player, Parkourability parkourability, ByteBuffer startInfo) {
        startInfo.putDouble(Math.abs(player.getDeltaMovement().y()));
        return this.canContinue(player, parkourability);
    }

    @Override
    public boolean canContinue(Player player, Parkourability parkourability) {
        Vec3 wall = WorldUtil.getWall((LivingEntity)player);
        return wall != null && !player.onGround() && !parkourability.get(FastRun.class).isDoing() && !parkourability.get(Dodge.class).isDoing() && !player.getAbilities().flying && player.getDeltaMovement().y <= 0.0 && KeyBindings.getKeyWallSlide().isDown() && !((ReadonlyStamina)AttachmentHolder.getData(player, Attachments.STAMINA)).isExhausted() && !parkourability.get(Dive.class).isDoing() && !parkourability.get(ClingToCliff.class).isDoing() && parkourability.get(ClingToCliff.class).getNotDoingTick() > 12;
    }

    @Override
    public void onStart(Player player, Parkourability parkourability, ByteBuffer startData) {
        this.particleSpawnCoolTime = 0;
    }

    @Override
    public void onStartInServer(Player player, Parkourability parkourability, ByteBuffer startData) {
        double startYSpeed = startData.getDouble();
        this.damageCount = (int)(5.5 * (startYSpeed - 1.0) / (double)player.getBbHeight());
        this.takenDamageCount = 0;
        this.damageCoolTime = 0;
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public void onWorkingTickInClient(Player player, Parkourability parkourability) {
        Animation animation = Animation.get(player);
        if (animation != null && !animation.hasAnimator()) {
            animation.setAnimator(new WallSlideAnimator());
        }
        this.particleSpawnCoolTime = (byte)(this.particleSpawnCoolTime - 1);
        if (this.particleSpawnCoolTime <= 0) {
            this.particleSpawnCoolTime = (byte)2;
            this.spawnSlideParticle(player);
        }
    }

    @Override
    public StaminaConsumeTiming getStaminaConsumeTiming() {
        return StaminaConsumeTiming.OnWorking;
    }

    @Override
    public void onWorkingTick(Player player, Parkourability parkourability) {
        this.leanedWallDirection = WorldUtil.getWall((LivingEntity)player);
        if (this.leanedWallDirection != null) {
            BlockPos leanedBlock = new BlockPos(Mth.floor((double)(player.getX() + this.leanedWallDirection.x)), Mth.floor((double)(player.getY() + (double)player.getBbHeight() * 0.75)), Mth.floor((double)(player.getZ() + this.leanedWallDirection.z)));
            if (!player.getCommandSenderWorld().isLoaded(leanedBlock)) {
                return;
            }
            float slipperiness = player.getCommandSenderWorld().getBlockState(leanedBlock).getFriction((LevelReader)player.getCommandSenderWorld(), leanedBlock, (Entity)player);
            slipperiness = (float)Math.sqrt(slipperiness);
            player.fallDistance *= slipperiness;
            player.setDeltaMovement(player.getDeltaMovement().multiply(0.8, (double)slipperiness, 0.8));
        }
    }

    @Override
    public void onWorkingTickInServer(Player player, Parkourability parkourability) {
        if (this.damageCoolTime <= 0 && this.damageCount > this.takenDamageCount++) {
            int invulnerableTime = player.invulnerableTime;
            this.damageCoolTime = 1;
            player.invulnerableTime = 0;
            player.hurt(player.level().damageSources().source(DamageSources.WALL_SLIDE), 0.3f);
            player.invulnerableTime = invulnerableTime;
        } else {
            this.damageCoolTime = (byte)(this.damageCoolTime - 1);
        }
    }

    @OnlyIn(value=Dist.CLIENT)
    private void spawnSlideParticle(Player player) {
        Vec3 pos;
        BlockPos leanedBlock;
        if (!ParCoolConfig.Client.Booleans.EnableActionParticles.get().booleanValue()) {
            return;
        }
        if (this.leanedWallDirection == null) {
            return;
        }
        if (player.getRandom().nextBoolean()) {
            return;
        }
        Level level = player.level();
        if (!level.isLoaded(leanedBlock = new BlockPos(Mth.floor((double)((pos = player.position()).x() + this.leanedWallDirection.x())), Mth.floor((double)(pos.y() + (double)player.getBbHeight() * 0.25)), Mth.floor((double)(pos.z() + this.leanedWallDirection.z()))))) {
            return;
        }
        float width = player.getBbWidth();
        BlockState blockstate = level.getBlockState(leanedBlock);
        Vec3 normalizedWallVec = this.leanedWallDirection.normalize();
        Vec3 orthogonalToWallVec = normalizedWallVec.yRot(1.5707964f);
        if (blockstate.getRenderShape() != RenderShape.INVISIBLE) {
            Vec3 particlePos = new Vec3(pos.x() + (normalizedWallVec.x() * 0.4 + orthogonalToWallVec.x() * (player.getRandom().nextDouble() - 0.5)) * (double)width, pos.y() + (double)player.getBbHeight() - 0.2 + 0.3 * player.getRandom().nextDouble(), pos.z() + (normalizedWallVec.z() * 0.4 + orthogonalToWallVec.z() * (player.getRandom().nextDouble() - 0.5)) * (double)width);
            Vec3 particleSpeed = normalizedWallVec.reverse().yRot((float)(0.3141592653589793 * (player.getRandom().nextDouble() - 0.5))).scale(0.05).add(0.0, -0.5 - player.getRandom().nextDouble(), 0.0);
            level.addParticle((ParticleOptions)new BlockParticleOption(ParticleTypes.BLOCK, blockstate).setPos(leanedBlock), particlePos.x(), particlePos.y(), particlePos.z(), particleSpeed.x(), particleSpeed.y(), particleSpeed.z());
        }
    }
}

