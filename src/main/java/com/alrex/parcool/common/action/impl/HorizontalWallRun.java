package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.platform.attachment.AttachmentHolder;

import com.alrex.parcool.api.SoundEvents;
import com.alrex.parcool.client.animation.impl.HorizontalWallRunAnimator;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.StaminaConsumeTiming;
import com.alrex.parcool.common.action.impl.ClingToCliff;
import com.alrex.parcool.common.action.impl.Crawl;
import com.alrex.parcool.common.action.impl.Dodge;
import com.alrex.parcool.common.action.impl.FastRun;
import com.alrex.parcool.common.action.impl.Vault;
import com.alrex.parcool.common.action.impl.WallJump;
import com.alrex.parcool.common.attachment.Attachments;
import com.alrex.parcool.common.attachment.client.Animation;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.common.attachment.common.ReadonlyStamina;
import com.alrex.parcool.common.info.ActionInfo;
import com.alrex.parcool.config.ParCoolConfig;
import com.alrex.parcool.utilities.BufferUtil;
import com.alrex.parcool.utilities.VectorUtil;
import com.alrex.parcool.utilities.WorldUtil;
import java.nio.ByteBuffer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import com.alrex.parcool.platform.dist.Dist;
import com.alrex.parcool.platform.dist.OnlyIn;
import com.alrex.parcool.platform.event.client.RenderFrameEvent;

public class HorizontalWallRun
extends Action {
    private int coolTime = 0;
    private float bodyYaw = 0.0f;
    private boolean wallIsRightward = false;
    private Vec3 runningWallDirection = null;
    private Vec3 runningDirection = null;

    private int getMaxRunningTick(ActionInfo info) {
        Integer value = info.getClientSetting().get(ParCoolConfig.Client.Integers.WallRunContinuableTick);
        if (value == null) {
            value = ParCoolConfig.Client.Integers.WallRunContinuableTick.DefaultValue;
        }
        return Math.min(value, info.getServerLimitation().get(ParCoolConfig.Server.Integers.MaxWallRunContinuableTick));
    }

    @Override
    public void onClientTick(Player player, Parkourability parkourability) {
        if (this.coolTime > 0) {
            --this.coolTime;
        }
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public void onWorkingTickInLocalClient(Player player, Parkourability parkourability) {
        Vec3 wallDirection = WorldUtil.getRunnableWall((LivingEntity)player, player.getBbWidth() * 0.65f);
        if (wallDirection == null) {
            return;
        }
        if (this.runningWallDirection == null) {
            return;
        }
        if (this.runningDirection == null) {
            return;
        }
        Vec3 lookVec = VectorUtil.fromYawDegree(player.yBodyRot);
        double differenceAngle = Math.asin(new Vec3(lookVec.x() * this.runningDirection.x() + lookVec.z() * this.runningDirection.z(), 0.0, -lookVec.x() * this.runningDirection.z() + lookVec.z() * this.runningDirection.x()).normalize().z());
        this.bodyYaw = (float)VectorUtil.toYawDegree(lookVec.yRot((float)(differenceAngle / 10.0)));
        Vec3 movement = player.getDeltaMovement();
        BlockPos leanedBlock = new BlockPos(Mth.floor((double)(player.getX() + this.runningWallDirection.x())), Mth.floor((double)(player.getBoundingBox().minY + (double)player.getBbHeight() * 0.5)), Mth.floor((double)(player.getZ() + this.runningWallDirection.z())));
        if (!player.getCommandSenderWorld().isLoaded(leanedBlock)) {
            return;
        }
        float slipperiness = player.getCommandSenderWorld().getBlockState(leanedBlock).getFriction((LevelReader)player.getCommandSenderWorld(), leanedBlock, (Entity)player);
        if ((double)slipperiness <= 0.8) {
            double speedScale = 0.2;
            AttributeInstance attr = player.getAttribute(Attributes.MOVEMENT_SPEED);
            if (attr != null) {
                speedScale *= attr.getValue() / attr.getBaseValue();
            }
            player.setDeltaMovement(this.runningDirection.x() * speedScale, movement.y() * ((double)slipperiness - 0.1) * (double)this.getDoingTick() / (double)this.getMaxRunningTick(parkourability.getActionInfo()), this.runningDirection.z() * speedScale);
        }
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public boolean canStart(Player player, Parkourability parkourability, ByteBuffer startInfo) {
        Vec3 wallDirection = WorldUtil.getRunnableWall((LivingEntity)player, player.getBbWidth() * 0.65f);
        if (wallDirection == null) {
            return false;
        }
        Vec3 wallVec = wallDirection.normalize();
        Vec3 lookDirection = VectorUtil.fromYawDegree(player.yBodyRot);
        lookDirection = new Vec3(lookDirection.x(), 0.0, lookDirection.z()).normalize();
        Vec3 dividedVec = new Vec3(wallVec.x() * lookDirection.x() + wallVec.z() * lookDirection.z(), 0.0, -wallVec.x() * lookDirection.z() + wallVec.z() * lookDirection.x()).normalize();
        if (Math.abs(dividedVec.z()) < 0.9) {
            return false;
        }
        BufferUtil.wrap(startInfo).putBoolean(dividedVec.z() > 0.0);
        Vec3 runDirection = wallVec.yRot(1.5707964f);
        if (runDirection.dot(lookDirection) < 0.0) {
            runDirection = runDirection.reverse();
        }
        startInfo.putDouble(wallDirection.x()).putDouble(wallDirection.z()).putDouble(runDirection.x()).putDouble(runDirection.z());
        return !parkourability.get(WallJump.class).justJumped() && (ParCoolConfig.Client.getInstance().HWallRunControl.get() == ControlType.PressKey && KeyBindings.getKeyHorizontalWallRun().isDown() || ParCoolConfig.Client.getInstance().HWallRunControl.get() == ControlType.Auto) && !parkourability.get(Crawl.class).isDoing() && !parkourability.get(Dodge.class).isDoing() && !parkourability.get(Vault.class).isDoing() && !parkourability.get(ClingToCliff.class).isDoing() && !player.isInWaterOrBubble() && Math.abs(player.getDeltaMovement().y()) < 0.5 && this.coolTime == 0 && !player.onGround() && parkourability.getAdditionalProperties().getNotLandingTick() > 5 && (parkourability.get(FastRun.class).canActWithRunning(player) || parkourability.get(FastRun.class).getNotDashTick(parkourability.getAdditionalProperties()) < 10);
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public boolean canContinue(Player player, Parkourability parkourability) {
        Vec3 wallDirection = WorldUtil.getRunnableWall((LivingEntity)player, player.getBbWidth() * 0.65f);
        if (wallDirection == null) {
            return false;
        }
        if (!(player instanceof LocalPlayer)) {
            return false;
        }
        LocalPlayer localPlayer = (LocalPlayer)player;
        if (localPlayer.input == null) {
            return false;
        }
        Vec2 moveVector = localPlayer.input.getMoveVector();
        Vec3 actualInputVector = new Vec3((double)moveVector.x, 0.0, (double)moveVector.y).normalize().yRot((float)(-Math.toRadians(player.getYRot())));
        if (wallDirection.normalize().dot(actualInputVector) < -0.86) {
            return false;
        }
        return this.getDoingTick() < this.getMaxRunningTick(parkourability.getActionInfo()) && !((ReadonlyStamina)AttachmentHolder.getData(player, Attachments.STAMINA)).isExhausted() && !parkourability.get(WallJump.class).justJumped() && !parkourability.get(Crawl.class).isDoing() && !parkourability.get(Dodge.class).isDoing() && !parkourability.get(Vault.class).isDoing() && (ParCoolConfig.Client.getInstance().HWallRunControl.get() == ControlType.PressKey && KeyBindings.getKeyHorizontalWallRun().isDown() || ParCoolConfig.Client.getInstance().HWallRunControl.get() == ControlType.Auto) && !player.onGround();
    }

    @Override
    public void onStop(Player player) {
        this.coolTime = 10;
    }

    @Override
    public void onStartInLocalClient(Player player, Parkourability parkourability, ByteBuffer startData) {
        Animation animation;
        this.wallIsRightward = BufferUtil.getBoolean(startData);
        this.runningWallDirection = new Vec3(startData.getDouble(), 0.0, startData.getDouble());
        this.runningDirection = new Vec3(startData.getDouble(), 0.0, startData.getDouble());
        if (ParCoolConfig.Client.Booleans.EnableActionSounds.get().booleanValue()) {
            player.playSound((SoundEvent)SoundEvents.HORIZONTAL_WALL_RUN.get(), 1.0f, 1.0f);
        }
        if ((animation = Animation.get(player)) != null) {
            animation.setAnimator(new HorizontalWallRunAnimator(this.wallIsRightward));
        }
    }

    @Override
    public void onStartInOtherClient(Player player, Parkourability parkourability, ByteBuffer startData) {
        this.wallIsRightward = BufferUtil.getBoolean(startData);
        this.runningWallDirection = new Vec3(startData.getDouble(), 0.0, startData.getDouble());
        this.runningDirection = new Vec3(startData.getDouble(), 0.0, startData.getDouble());
        Animation animation = Animation.get(player);
        if (ParCoolConfig.Client.Booleans.EnableActionSounds.get().booleanValue()) {
            player.playSound((SoundEvent)SoundEvents.HORIZONTAL_WALL_RUN.get(), 1.0f, 1.0f);
        }
        if (animation != null) {
            animation.setAnimator(new HorizontalWallRunAnimator(this.wallIsRightward));
        }
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public void onRenderTick(RenderFrameEvent event, Player player, Parkourability parkourability) {
        if (this.isDoing()) {
            if (this.runningDirection == null) {
                return;
            }
            Vec3 lookVec = VectorUtil.fromYawDegree(player.getYHeadRot());
            double differenceAngle = Math.asin(new Vec3(lookVec.x() * this.runningDirection.x() + lookVec.z() * this.runningDirection.z(), 0.0, -lookVec.x() * this.runningDirection.z() + lookVec.z() * this.runningDirection.x()).normalize().z());
            if (Math.abs(differenceAngle) > 0.7853981633974483) {
                player.setYRot((float)VectorUtil.toYawDegree(this.runningDirection.yRot((float)(-Math.signum(differenceAngle) * Math.PI / 4.0))));
            }
            player.yBodyRotO = player.yBodyRot = this.bodyYaw;
        }
    }

    @Override
    public void onWorkingTickInClient(Player player, Parkourability parkourability) {
        this.spawnRunningParticle(player);
    }

    @Override
    public void saveSynchronizedState(ByteBuffer buffer) {
        buffer.putFloat(this.bodyYaw);
    }

    @Override
    public void restoreSynchronizedState(ByteBuffer buffer) {
        this.bodyYaw = buffer.getFloat();
    }

    @Override
    public StaminaConsumeTiming getStaminaConsumeTiming() {
        return StaminaConsumeTiming.OnWorking;
    }

    @OnlyIn(value=Dist.CLIENT)
    public void spawnRunningParticle(Player player) {
        Vec3 pos;
        BlockPos leanedBlock;
        if (!ParCoolConfig.Client.Booleans.EnableActionParticles.get().booleanValue()) {
            return;
        }
        if (this.runningDirection == null || this.runningWallDirection == null) {
            return;
        }
        Level level = player.level();
        if (!level.isLoaded(leanedBlock = new BlockPos(Mth.floor((double)((pos = player.position()).x() + this.runningWallDirection.x())), Mth.floor((double)(pos.y() + (double)player.getBbHeight() * 0.25)), Mth.floor((double)(pos.z() + this.runningWallDirection.z()))))) {
            return;
        }
        float width = player.getBbWidth();
        BlockState blockstate = level.getBlockState(leanedBlock);
        Vec3 wallDirection = this.runningWallDirection.normalize();
        Vec3 orthogonalToWallVec = wallDirection.yRot(1.5707964f);
        Vec3 particleBaseDirection = this.runningDirection.subtract(wallDirection);
        if (blockstate.getRenderShape() != RenderShape.INVISIBLE) {
            Vec3 particlePos = new Vec3(pos.x() + (wallDirection.x() * 0.4 + orthogonalToWallVec.x() * (player.getRandom().nextDouble() - 0.5)) * (double)width, pos.y() + 0.1 + 0.3 * player.getRandom().nextDouble(), pos.z() + (wallDirection.z() * 0.4 + orthogonalToWallVec.z() * (player.getRandom().nextDouble() - 0.5)) * (double)width);
            Vec3 particleSpeed = particleBaseDirection.yRot((float)(0.6283185307179586 * (player.getRandom().nextDouble() - 0.5))).scale(3.0 + 6.0 * player.getRandom().nextDouble()).add(0.0, 1.5, 0.0);
            level.addParticle((ParticleOptions)new BlockParticleOption(ParticleTypes.BLOCK, blockstate).setPos(leanedBlock), particlePos.x(), particlePos.y(), particlePos.z(), particleSpeed.x(), particleSpeed.y(), particleSpeed.z());
        }
    }

    public static enum ControlType {
        PressKey,
        Auto;

    }
}

