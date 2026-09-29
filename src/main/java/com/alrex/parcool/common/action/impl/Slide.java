package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.api.SoundEvents;
import com.alrex.parcool.client.animation.impl.CrawlAnimator;
import com.alrex.parcool.client.animation.impl.SlidingAnimator;
import com.alrex.parcool.client.input.KeyRecorder;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.BehaviorEnforcer;
import com.alrex.parcool.common.action.StaminaConsumeTiming;
import com.alrex.parcool.common.action.impl.Crawl;
import com.alrex.parcool.common.action.impl.FastRun;
import com.alrex.parcool.common.action.impl.Roll;
import com.alrex.parcool.common.action.impl.Tap;
import com.alrex.parcool.common.attachment.client.Animation;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.config.ParCoolConfig;
import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import com.alrex.parcool.platform.dist.Dist;
import com.alrex.parcool.platform.dist.OnlyIn;

public class Slide
extends Action {
    private static final BehaviorEnforcer.ID ID_JUMP_CANCEL = BehaviorEnforcer.newID();
    private Vec3 slidingVec = null;

    @Override
    public boolean canStart(Player player, Parkourability parkourability, ByteBuffer startInfo) {
        Vec3 lookingVec = player.getLookAngle().multiply(1.0, 0.0, 1.0).normalize();
        startInfo.putDouble(lookingVec.x()).putDouble(lookingVec.z());
        return KeyRecorder.keyCrawlState.isPressed() && player.onGround() && !parkourability.get(Roll.class).isDoing() && !parkourability.get(Tap.class).isDoing() && parkourability.get(Crawl.class).isDoing() && !player.isInWaterOrBubble() && parkourability.get(FastRun.class).getDashTick(parkourability.getAdditionalProperties()) > 5;
    }

    @Override
    public boolean canContinue(Player player, Parkourability parkourability) {
        int maxSlidingTick = Math.min(parkourability.getActionInfo().getClientSetting().get(ParCoolConfig.Client.Integers.SlidingContinuableTick), parkourability.getActionInfo().getServerLimitation().get(ParCoolConfig.Server.Integers.MaxSlidingContinuableTick));
        return this.getDoingTick() < maxSlidingTick && parkourability.get(Crawl.class).isDoing();
    }

    @Override
    public void onStartInLocalClient(Player player, Parkourability parkourability, ByteBuffer startData) {
        Animation animation;
        this.slidingVec = new Vec3(startData.getDouble(), 0.0, startData.getDouble());
        if (ParCoolConfig.Client.Booleans.EnableActionSounds.get().booleanValue()) {
            player.playSound((SoundEvent)SoundEvents.SLIDE.get(), 1.0f, 1.0f);
        }
        if ((animation = Animation.get(player)) != null) {
            animation.setAnimator(new SlidingAnimator());
        }
        parkourability.getBehaviorEnforcer().addMarkerCancellingJump(ID_JUMP_CANCEL, this::isDoing);
    }

    @Override
    public void onStartInOtherClient(Player player, Parkourability parkourability, ByteBuffer startData) {
        Animation animation;
        this.slidingVec = new Vec3(startData.getDouble(), 0.0, startData.getDouble());
        if (ParCoolConfig.Client.Booleans.EnableActionSounds.get().booleanValue()) {
            player.playSound((SoundEvent)SoundEvents.SLIDE.get(), 1.0f, 1.0f);
        }
        if ((animation = Animation.get(player)) != null) {
            animation.setAnimator(new SlidingAnimator());
        }
    }

    @Override
    public void onWorkingTickInLocalClient(Player player, Parkourability parkourability) {
        if (this.slidingVec != null) {
            AttributeInstance attr = player.getAttribute(Attributes.MOVEMENT_SPEED);
            double speedScale = 0.45;
            if (attr != null) {
                speedScale = attr.getValue() * 4.5;
            }
            Vec3 vec = this.slidingVec.scale(speedScale);
            player.setDeltaMovement((player.onGround() ? vec : vec.scale(0.6)).add(0.0, player.getDeltaMovement().y(), 0.0));
        }
    }

    @Override
    public void onWorkingTickInClient(Player player, Parkourability parkourability) {
        this.spawnSlidingParticle(player);
    }

    @Override
    public void onStopInLocalClient(Player player) {
        Animation animation = Animation.get(player);
        if (animation != null && !animation.hasAnimator()) {
            animation.setAnimator(new CrawlAnimator());
        }
        if (!Parkourability.get(player).get(Crawl.class).isDoing()) {
            player.swimAmount = 0.0f;
            player.swimAmountO = 0.0f;
        }
    }

    @Override
    public void onStopInOtherClient(Player player) {
        Animation animation = Animation.get(player);
        if (animation != null && !animation.hasAnimator()) {
            animation.setAnimator(new CrawlAnimator());
        }
        if (!Parkourability.get(player).get(Crawl.class).isDoing()) {
            player.swimAmount = 0.0f;
            player.swimAmountO = 0.0f;
        }
    }

    @Nullable
    public Vec3 getSlidingVector() {
        return this.slidingVec;
    }

    @Override
    public StaminaConsumeTiming getStaminaConsumeTiming() {
        return StaminaConsumeTiming.None;
    }

    @OnlyIn(value=Dist.CLIENT)
    private void spawnSlidingParticle(Player player) {
        if (!ParCoolConfig.Client.Booleans.EnableActionParticles.get().booleanValue()) {
            return;
        }
        Level level = player.level();
        Vec3 pos = player.position();
        BlockState feetBlock = player.level().getBlockState(player.blockPosition().below());
        float width = player.getBbWidth();
        Vec3 direction = this.getSlidingVector();
        if (direction == null) {
            return;
        }
        if (feetBlock.getRenderShape() != RenderShape.INVISIBLE) {
            Vec3 particlePos = new Vec3(pos.x() + (player.getRandom().nextDouble() - 0.5) * (double)width, pos.y() + 0.01 + 0.2 * player.getRandom().nextDouble(), pos.z() + (player.getRandom().nextDouble() - 0.5) * (double)width);
            Vec3 particleSpeed = direction.reverse().scale(2.5 + 5.0 * player.getRandom().nextDouble()).add(0.0, 1.5, 0.0);
            Vec3 blockPos = player.position().add(0.0, -0.5, 0.0);
            level.addParticle((ParticleOptions)new BlockParticleOption(ParticleTypes.BLOCK, feetBlock).setPos(new BlockPos(Mth.floor((double)blockPos.x()), Mth.floor((double)blockPos.y()), Mth.floor((double)blockPos.z()))), particlePos.x(), particlePos.y(), particlePos.z(), particleSpeed.x(), particleSpeed.y(), particleSpeed.z());
        }
    }

    @Override
    public void onWorkingTick(Player player, Parkourability parkourability) {
        player.setSprinting(false);
        if (player.getForcedPose() != Pose.SWIMMING) {
            player.setForcedPose(Pose.SWIMMING);
        }
    }

    @Override
    public void onStop(Player player) {
        player.setForcedPose(null);
    }
}

