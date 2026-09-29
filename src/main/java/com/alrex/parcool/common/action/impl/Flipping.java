package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.client.animation.impl.FlippingAnimator;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.client.input.KeyRecorder;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.StaminaConsumeTiming;
import com.alrex.parcool.common.action.impl.ChargeJump;
import com.alrex.parcool.common.action.impl.Crawl;
import com.alrex.parcool.common.action.impl.Dive;
import com.alrex.parcool.common.action.impl.HideInBlock;
import com.alrex.parcool.common.attachment.client.Animation;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.config.ParCoolConfig;
import java.nio.ByteBuffer;
import net.minecraft.world.entity.player.Player;
import com.alrex.parcool.platform.dist.Dist;
import com.alrex.parcool.platform.dist.OnlyIn;

public class Flipping
extends Action {
    private boolean justJumped = false;

    public void onJump(Player player, Parkourability parkourability) {
        this.justJumped = true;
    }

    @Override
    public boolean canStart(Player player, Parkourability parkourability, ByteBuffer startInfo) {
        Direction fDirection = KeyBindings.isKeyBackDown() != false ? Direction.Back : Direction.Front;
        ControlType control = (ControlType)((Object)ParCoolConfig.Client.getInstance().FlipControl.get());
        startInfo.putInt(control.ordinal()).putInt(fDirection.ordinal());
        boolean input = control.isInputDone(this.justJumped);
        this.justJumped = false;
        return input && !player.isInWater() && !player.isShiftKeyDown() && !parkourability.get(Crawl.class).isDoing() && !parkourability.get(Dive.class).isDoing() && !parkourability.get(ChargeJump.class).isDoing() && !parkourability.get(HideInBlock.class).isDoing() && !parkourability.getBehaviorEnforcer().cancelJump() && parkourability.getAdditionalProperties().getNotLandingTick() <= 1;
    }

    @Override
    public boolean canContinue(Player player, Parkourability parkourability) {
        return !player.onGround() || this.getDoingTick() <= 10;
    }

    @Override
    public void onStartInLocalClient(Player player, Parkourability parkourability, ByteBuffer startData) {
        Animation animation;
        ControlType control = ControlType.values()[startData.getInt()];
        if (control != ControlType.TapMovementAndJump) {
            player.jumpFromGround();
        }
        if ((animation = Animation.get(player)) != null) {
            animation.setAnimator(new FlippingAnimator(Direction.values()[startData.getInt()]));
        }
    }

    @Override
    public void onStartInOtherClient(Player player, Parkourability parkourability, ByteBuffer startData) {
        startData.position(4);
        Animation animation = Animation.get(player);
        if (animation != null) {
            animation.setAnimator(new FlippingAnimator(Direction.values()[startData.getInt()]));
        }
    }

    @Override
    public StaminaConsumeTiming getStaminaConsumeTiming() {
        return StaminaConsumeTiming.OnStart;
    }

    public static enum Direction {
        Front,
        Back;

    }

    public static enum ControlType {
        PressRightAndLeft,
        TapMovementAndJump,
        PressFlippingKey;


        @OnlyIn(value=Dist.CLIENT)
        public boolean isInputDone(boolean justJumped) {
            switch (this.ordinal()) {
                case 0: {
                    return KeyBindings.isKeyRightDown() != false && KeyRecorder.keyRight.getTickKeyDown() < 3 && KeyBindings.isKeyLeftDown() != false && KeyRecorder.keyLeft.getTickKeyDown() < 3;
                }
                case 2: {
                    return KeyRecorder.keyFlipping.isPressed();
                }
                case 1: {
                    return justJumped && (KeyBindings.isKeyForwardDown() != false && KeyRecorder.keyForward.getTickKeyDown() < 4 || KeyBindings.isKeyBackDown() != false && KeyRecorder.keyBack.getTickKeyDown() < 4);
                }
            }
            return false;
        }
    }
}

