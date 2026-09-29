package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.api.SoundEvents;
import com.alrex.parcool.client.animation.impl.JumpFromBarAnimator;
import com.alrex.parcool.client.input.KeyRecorder;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.StaminaConsumeTiming;
import com.alrex.parcool.common.action.impl.HangDown;
import com.alrex.parcool.common.attachment.client.Animation;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.config.ParCoolConfig;
import com.alrex.parcool.utilities.EntityUtil;
import java.nio.ByteBuffer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class JumpFromBar
extends Action {
    @Override
    public boolean canStart(Player player, Parkourability parkourability, ByteBuffer startInfo) {
        HangDown hangDown = parkourability.get(HangDown.class);
        return hangDown.isDoing() && hangDown.getDoingTick() > 2 && KeyRecorder.keyJumpState.isPressed();
    }

    @Override
    public boolean canContinue(Player player, Parkourability parkourability) {
        return this.getDoingTick() < 2;
    }

    @Override
    public void onStartInLocalClient(Player player, Parkourability parkourability, ByteBuffer startData) {
        Animation animation;
        EntityUtil.addVelocity((Entity)player, player.getLookAngle().multiply(1.0, 0.0, 1.0).normalize().scale((double)player.getBbWidth() * 0.75));
        if (ParCoolConfig.Client.Booleans.EnableActionSounds.get().booleanValue()) {
            player.playSound((SoundEvent)SoundEvents.HANG_DOWN_JUMP.get(), 1.0f, 1.0f);
        }
        if ((animation = Animation.get(player)) != null) {
            animation.setAnimator(new JumpFromBarAnimator());
        }
    }

    @Override
    public void onStartInOtherClient(Player player, Parkourability parkourability, ByteBuffer startData) {
        Animation animation;
        if (ParCoolConfig.Client.Booleans.EnableActionSounds.get().booleanValue()) {
            player.playSound((SoundEvent)SoundEvents.HANG_DOWN_JUMP.get(), 1.0f, 1.0f);
        }
        if ((animation = Animation.get(player)) != null) {
            animation.setAnimator(new JumpFromBarAnimator());
        }
    }

    @Override
    public StaminaConsumeTiming getStaminaConsumeTiming() {
        return StaminaConsumeTiming.OnStart;
    }
}

