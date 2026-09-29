package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.client.animation.impl.RollAnimator;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.BehaviorEnforcer;
import com.alrex.parcool.common.action.StaminaConsumeTiming;
import com.alrex.parcool.common.action.impl.Crawl;
import com.alrex.parcool.common.action.impl.Dodge;
import com.alrex.parcool.common.attachment.client.Animation;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.config.ParCoolConfig;
import com.alrex.parcool.utilities.VectorUtil;
import java.nio.ByteBuffer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import com.alrex.parcool.platform.dist.Dist;
import com.alrex.parcool.platform.dist.OnlyIn;

public class Roll
extends Action {
    private int creativeCoolTime = 0;
    private boolean startRequired = false;
    private static final BehaviorEnforcer.ID ID_JUMP_CANCEL = BehaviorEnforcer.newID();

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public void onClientTick(Player player, Parkourability parkourability) {
        if (player.isLocalPlayer()) {
            if (KeyBindings.getKeyBreakfall().isDown() && KeyBindings.isKeyForwardDown().booleanValue() && !parkourability.get(Dodge.class).isDoing() && !parkourability.get(Crawl.class).isDoing() && !player.isVisuallyCrawling() && !player.isVisuallySwimming() && ParCoolConfig.Client.Booleans.EnableRollWhenCreative.get().booleanValue() && player.isCreative() && parkourability.getAdditionalProperties().getLandingTick() <= 1 && player.onGround() && !this.isDoing() && this.creativeCoolTime == 0) {
                this.startRequired = true;
                this.creativeCoolTime = 20;
            }
            if (this.creativeCoolTime > 0) {
                --this.creativeCoolTime;
            }
        }
    }

    @Override
    public StaminaConsumeTiming getStaminaConsumeTiming() {
        return StaminaConsumeTiming.OnStart;
    }

    @Override
    public boolean canStart(Player player, Parkourability parkourability, ByteBuffer startInfo) {
        LocalPlayer clientPlayer = (LocalPlayer)player;
        Direction rollDirection = Direction.Front;
        if ((double)clientPlayer.input.leftImpulse < -0.5) {
            rollDirection = Direction.Right;
        } else if ((double)clientPlayer.input.leftImpulse > 0.5) {
            rollDirection = Direction.Left;
        } else if ((double)clientPlayer.input.forwardImpulse < -0.5) {
            rollDirection = Direction.Back;
        }
        startInfo.putInt(rollDirection.ordinal());
        return this.startRequired;
    }

    @Override
    public boolean canContinue(Player player, Parkourability parkourability) {
        return this.getDoingTick() <= this.getRollMaxTick();
    }

    @Override
    public void onStartInOtherClient(Player player, Parkourability parkourability, ByteBuffer startData) {
        this.startRequired = false;
        Direction direction = Direction.values()[startData.getInt()];
        Animation animation = Animation.get(player);
        if (animation != null) {
            animation.setAnimator(new RollAnimator(direction));
        }
    }

    @Override
    public void onStartInLocalClient(Player player, Parkourability parkourability, ByteBuffer startData) {
        this.startRequired = false;
        Direction direction = Direction.values()[startData.getInt()];
        double modifier = Math.sqrt(player.getBbWidth());
        Vec3 vec = VectorUtil.fromYawDegree(player.yBodyRot).scale(modifier);
        switch (direction.ordinal()) {
            case 1: {
                vec = vec.reverse();
                break;
            }
            case 3: {
                vec = vec.yRot(-1.5707964f);
                break;
            }
            case 2: {
                vec = vec.yRot(1.5707964f);
            }
        }
        player.setDeltaMovement(vec.x(), 0.0, vec.z());
        Animation animation = Animation.get(player);
        if (animation != null) {
            animation.setAnimator(new RollAnimator(direction));
        }
        parkourability.getBehaviorEnforcer().addMarkerCancellingJump(ID_JUMP_CANCEL, this::isDoing);
    }

    public void startRoll(Player player) {
        this.startRequired = true;
    }

    public int getRollMaxTick() {
        return 9;
    }

    public static enum Direction {
        Front,
        Back,
        Left,
        Right;

    }
}

