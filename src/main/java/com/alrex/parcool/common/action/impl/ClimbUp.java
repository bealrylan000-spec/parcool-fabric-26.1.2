package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.api.SoundEvents;
import com.alrex.parcool.client.animation.impl.ClimbUpAnimator;
import com.alrex.parcool.client.input.KeyRecorder;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.StaminaConsumeTiming;
import com.alrex.parcool.common.action.impl.ClingToCliff;
import com.alrex.parcool.common.attachment.client.Animation;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.config.ParCoolConfig;
import java.nio.ByteBuffer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import com.alrex.parcool.platform.dist.Dist;
import com.alrex.parcool.platform.dist.OnlyIn;

public class ClimbUp
extends Action {
    @Override
    @OnlyIn(value=Dist.CLIENT)
    public boolean canStart(Player player, Parkourability parkourability, ByteBuffer startInfo) {
        ClingToCliff cling = parkourability.get(ClingToCliff.class);
        return cling.isDoing() && cling.getDoingTick() > 2 && cling.getFacingDirection() == ClingToCliff.FacingDirection.ToWall && KeyRecorder.keyJumpState.isPressed();
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public boolean canContinue(Player player, Parkourability parkourability) {
        return this.getDoingTick() < 2;
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public void onStartInLocalClient(Player player, Parkourability parkourability, ByteBuffer startData) {
        Animation animation;
        Vec3 speed = player.getDeltaMovement();
        player.setDeltaMovement(speed.x(), 0.6, speed.z());
        if (ParCoolConfig.Client.Booleans.EnableActionSounds.get().booleanValue()) {
            player.playSound((SoundEvent)SoundEvents.CLING_TO_CLIFF_JUMP.get(), 1.0f, 1.0f);
        }
        if ((animation = Animation.get(player)) != null) {
            animation.setAnimator(new ClimbUpAnimator());
        }
    }

    @Override
    public void onStartInOtherClient(Player player, Parkourability parkourability, ByteBuffer startData) {
        Animation animation;
        if (ParCoolConfig.Client.Booleans.EnableActionSounds.get().booleanValue()) {
            player.playSound((SoundEvent)SoundEvents.CLING_TO_CLIFF_JUMP.get(), 1.0f, 1.0f);
        }
        if ((animation = Animation.get(player)) != null) {
            animation.setAnimator(new ClimbUpAnimator());
        }
    }

    @Override
    public StaminaConsumeTiming getStaminaConsumeTiming() {
        return StaminaConsumeTiming.OnStart;
    }
}

