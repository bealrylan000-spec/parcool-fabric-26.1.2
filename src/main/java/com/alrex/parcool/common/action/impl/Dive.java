package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.platform.attachment.AttachmentHolder;

import com.alrex.parcool.client.animation.impl.DiveAnimationHostAnimator;
import com.alrex.parcool.client.animation.impl.DiveIntoWaterAnimator;
import com.alrex.parcool.client.input.KeyRecorder;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.StaminaConsumeTiming;
import com.alrex.parcool.common.action.impl.CatLeap;
import com.alrex.parcool.common.action.impl.Crawl;
import com.alrex.parcool.common.action.impl.FastRun;
import com.alrex.parcool.common.action.impl.RideZipline;
import com.alrex.parcool.common.action.impl.SkyDive;
import com.alrex.parcool.common.attachment.Attachments;
import com.alrex.parcool.common.attachment.client.Animation;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.common.attachment.common.ReadonlyStamina;
import com.alrex.parcool.utilities.BufferUtil;
import com.alrex.parcool.utilities.WorldUtil;
import java.nio.ByteBuffer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import com.alrex.parcool.platform.dist.Dist;
import com.alrex.parcool.platform.dist.OnlyIn;

public class Dive
extends Action {
    private boolean justJumped = false;
    private double initialYVelocityOfLastJump = 0.42;
    private double playerYSpeedOld = 0.0;
    private double playerYSpeed = 0.0;
    private int fallingTick = 0;

    public double getPlayerYSpeed(float partialTick) {
        return Mth.lerp((double)partialTick, (double)this.playerYSpeedOld, (double)this.playerYSpeed);
    }

    @Override
    public void onWorkingTickInLocalClient(Player player, Parkourability parkourability) {
        this.playerYSpeedOld = this.playerYSpeed;
        this.playerYSpeed = player.getDeltaMovement().y();
    }

    @Override
    public void onClientTick(Player player, Parkourability parkourability) {
        this.fallingTick = this.isDoing() && (this.playerYSpeed < 0.0 || this.fallingTick > 0) ? ++this.fallingTick : 0;
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public boolean canStart(Player player, Parkourability parkourability, ByteBuffer startInfo) {
        boolean startInAir;
        if (player.getVehicle() != null) {
            return false;
        }
        boolean bl = startInAir = player.getDeltaMovement().y() < 0.0 && parkourability.getAdditionalProperties().getNotLandingTick() > 10 && parkourability.getAdditionalProperties().getNotInWaterTick() > 30 && KeyRecorder.keyJumpState.getTickKeyDown() > 10 && !parkourability.get(CatLeap.class).isDoing() && !parkourability.get(RideZipline.class).isDoing() && WorldUtil.existsSpaceBelow((LivingEntity)player);
        if (!(startInAir || this.justJumped && WorldUtil.existsDivableSpace((LivingEntity)player) && parkourability.get(FastRun.class).canActWithRunning(player))) {
            this.justJumped = false;
            return false;
        }
        startInfo.putDouble(this.initialYVelocityOfLastJump);
        BufferUtil.wrap(startInfo).putBoolean(startInAir);
        this.justJumped = false;
        return parkourability.getActionInfo().can(Dive.class) && !parkourability.get(Crawl.class).isDoing() && !player.isVisuallyCrawling();
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public boolean canContinue(Player player, Parkourability parkourability) {
        return !(player.isFallFlying() || player.getAbilities().flying || player.isInWaterOrBubble() || player.isInLava() || player.isSwimming() || player.onGround() || this.fallingTick > 5 && (double)player.fallDistance < 0.1 || ((ReadonlyStamina)AttachmentHolder.getData(player, Attachments.STAMINA)).isExhausted() || parkourability.get(RideZipline.class).isDoing());
    }

    public void onJump(Player player, Parkourability parkourability) {
        if (!player.isLocalPlayer()) {
            return;
        }
        this.initialYVelocityOfLastJump = player.getDeltaMovement().y();
        this.justJumped = true;
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public void onStartInLocalClient(Player player, Parkourability parkourability, ByteBuffer startData) {
        double initialYSpeed;
        this.playerYSpeedOld = this.playerYSpeed = (initialYSpeed = startData.getDouble());
        Animation animation = Animation.get(player);
        if (animation != null) {
            animation.setAnimator(new DiveAnimationHostAnimator(initialYSpeed, BufferUtil.getBoolean(startData)));
        }
    }

    @Override
    public void onStopInLocalClient(Player player) {
        if (player.isInWaterOrBubble()) {
            Animation animation = Animation.get(player);
            Parkourability parkourability = Parkourability.get(player);
            if (animation != null && parkourability != null && parkourability.getAdditionalProperties().getNotLandingTick() >= 5 && player.getDeltaMovement().y() < 0.0) {
                animation.setAnimator(new DiveIntoWaterAnimator(parkourability.get(SkyDive.class).isDoing()));
            }
        }
    }

    @Override
    public void onStopInOtherClient(Player player) {
        if (player.isInWaterOrBubble()) {
            Animation animation = Animation.get(player);
            Parkourability parkourability = Parkourability.get(player);
            if (animation != null && parkourability != null && parkourability.getAdditionalProperties().getNotLandingTick() >= 5 && player.getDeltaMovement().y() < 0.0) {
                animation.setAnimator(new DiveIntoWaterAnimator(parkourability.get(SkyDive.class).isDoing()));
            }
        }
    }

    @Override
    public StaminaConsumeTiming getStaminaConsumeTiming() {
        return StaminaConsumeTiming.None;
    }

    @Override
    public void saveSynchronizedState(ByteBuffer buffer) {
        buffer.putDouble(this.playerYSpeed).putDouble(this.playerYSpeedOld);
    }

    @Override
    public void restoreSynchronizedState(ByteBuffer buffer) {
        this.playerYSpeed = buffer.getDouble();
        this.playerYSpeedOld = buffer.getDouble();
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public void onStartInOtherClient(Player player, Parkourability parkourability, ByteBuffer startData) {
        double initialYSpeed;
        this.playerYSpeed = this.initialYVelocityOfLastJump = (initialYSpeed = startData.getDouble());
        this.playerYSpeedOld = this.initialYVelocityOfLastJump;
        Animation animation = Animation.get(player);
        if (animation != null) {
            animation.setAnimator(new DiveAnimationHostAnimator(initialYSpeed, BufferUtil.getBoolean(startData)));
        }
    }
}

