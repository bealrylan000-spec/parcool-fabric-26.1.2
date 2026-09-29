package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.client.animation.impl.TapAnimator;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.BehaviorEnforcer;
import com.alrex.parcool.common.action.StaminaConsumeTiming;
import com.alrex.parcool.common.attachment.client.Animation;
import com.alrex.parcool.common.attachment.common.Parkourability;
import java.nio.ByteBuffer;
import net.minecraft.world.entity.player.Player;

public class Tap
extends Action {
    private static final BehaviorEnforcer.ID ID_JUMP_CANCEL = BehaviorEnforcer.newID();
    private boolean startRequired = false;

    @Override
    public void onWorkingTickInLocalClient(Player player, Parkourability parkourability) {
        player.setDeltaMovement(player.getDeltaMovement().multiply(0.01, 1.0, 0.01));
    }

    @Override
    public StaminaConsumeTiming getStaminaConsumeTiming() {
        return StaminaConsumeTiming.OnStart;
    }

    @Override
    public void onStartInLocalClient(Player player, Parkourability parkourability, ByteBuffer startData) {
        this.startRequired = false;
        Animation animation = Animation.get(player);
        if (animation != null) {
            animation.setAnimator(new TapAnimator());
        }
        parkourability.getBehaviorEnforcer().addMarkerCancellingJump(ID_JUMP_CANCEL, this::isDoing);
    }

    @Override
    public void onStartInOtherClient(Player player, Parkourability parkourability, ByteBuffer startData) {
        this.startRequired = false;
        Animation animation = Animation.get(player);
        if (animation != null) {
            animation.setAnimator(new TapAnimator());
        }
    }

    @Override
    public boolean canStart(Player player, Parkourability parkourability, ByteBuffer startInfo) {
        return this.startRequired;
    }

    @Override
    public boolean canContinue(Player player, Parkourability parkourability) {
        return this.getDoingTick() < this.getMaxTappingTick();
    }

    public void startTap(Player player) {
        this.startRequired = true;
    }

    public int getMaxTappingTick() {
        return 8;
    }
}

