package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.api.SoundEvents;
import com.alrex.parcool.api.unstable.action.ParCoolActionEvent;
import com.alrex.parcool.client.animation.impl.ChargeJumpAnimator;
import com.alrex.parcool.client.animation.impl.JumpChargingAnimator;
import com.alrex.parcool.client.input.KeyRecorder;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.StaminaConsumeTiming;
import com.alrex.parcool.common.action.impl.Crawl;
import com.alrex.parcool.common.action.impl.FastRun;
import com.alrex.parcool.common.attachment.client.Animation;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.config.ParCoolConfig;
import com.alrex.parcool.utilities.VectorUtil;
import java.nio.ByteBuffer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import com.alrex.parcool.platform.eventbus.Event;
import com.alrex.parcool.platform.eventbus.NeoForge;

public class ChargeJump
extends Action {
    public static final int JUMP_ANIMATION_TICK = 10;
    public static final int JUMP_MAX_CHARGE_TICK = 18;
    private int chargeTick = 0;
    private int lastChargeTick = 0;
    private int notChargeTick = 0;
    private int coolTimeTick = 0;
    private boolean started = false;

    @Override
    public boolean canStart(Player player, Parkourability parkourability, ByteBuffer startInfo) {
        boolean start = this.started;
        this.started = false;
        return start;
    }

    @Override
    public boolean canContinue(Player player, Parkourability parkourability) {
        return this.getDoingTick() < 10;
    }

    @Override
    public StaminaConsumeTiming getStaminaConsumeTiming() {
        return StaminaConsumeTiming.OnStart;
    }

    @Override
    public void onStartInLocalClient(Player player, Parkourability parkourability, ByteBuffer startData) {
        Animation animation;
        this.coolTimeTick = 30;
        if (ParCoolConfig.Client.Booleans.EnableActionSounds.get().booleanValue()) {
            player.playSound((SoundEvent)SoundEvents.CHARGE_JUMP.get(), 1.0f, 1.0f);
        }
        if ((animation = Animation.get(player)) != null) {
            animation.setAnimator(new ChargeJumpAnimator());
        }
    }

    @Override
    public void onStartInOtherClient(Player player, Parkourability parkourability, ByteBuffer startData) {
        Animation animation;
        if (ParCoolConfig.Client.Booleans.EnableActionSounds.get().booleanValue()) {
            player.playSound((SoundEvent)SoundEvents.CHARGE_JUMP.get(), 1.0f, 1.0f);
        }
        if ((animation = Animation.get(player)) != null) {
            animation.setAnimator(new ChargeJumpAnimator());
        }
    }

    @Override
    public void onClientTick(Player player, Parkourability parkourability) {
        Animation animation;
        if (player instanceof LocalPlayer) {
            LocalPlayer cp = (LocalPlayer)player;
            if (!(!cp.onGround() || this.coolTimeTick > 0 || !parkourability.getActionInfo().can(ChargeJump.class) || cp.isVisuallyCrawling() || cp.isSprinting() || cp.isInWaterOrBubble() || cp.input.up || cp.input.down || cp.input.right || cp.input.left || parkourability.get(Crawl.class).isDoing() || ((ParCoolActionEvent.TryToStartEvent)NeoForge.EVENT_BUS.post((Event)new ParCoolActionEvent.TryToStartEvent(player, this))).isCanceled() || ((ParCoolActionEvent.TryToStart)NeoForge.EVENT_BUS.post((Event)new ParCoolActionEvent.TryToStart(player, this))).isCanceled())) {
                if (cp.isShiftKeyDown() && KeyRecorder.keySneak.getPreviousTickNotKeyDown() > 5) {
                    ++this.chargeTick;
                    if (this.chargeTick > 18) {
                        this.chargeTick = 18;
                    }
                    this.lastChargeTick = this.chargeTick;
                    this.notChargeTick = 0;
                } else {
                    --this.chargeTick;
                    if (this.chargeTick < 0) {
                        this.chargeTick = 0;
                    }
                    ++this.notChargeTick;
                }
                if (this.isCharging()) {
                    Vec3 targetAngle = VectorUtil.fromYawDegree(player.yHeadRot);
                    Vec3 currentAngle = VectorUtil.fromYawDegree(player.yBodyRot);
                    double differenceAngle = Math.atan((currentAngle.x() * targetAngle.z() - targetAngle.x() * currentAngle.z()) / (targetAngle.x() * currentAngle.x() + targetAngle.z() * currentAngle.z()));
                    player.setYBodyRot((float)VectorUtil.toYawDegree(currentAngle.yRot((float)(-differenceAngle / 2.0))));
                }
            } else {
                if (this.coolTimeTick > 0) {
                    --this.coolTimeTick;
                }
                this.chargeTick = 0;
                ++this.notChargeTick;
            }
        }
        if (this.isCharging() && (animation = Animation.get(player)) != null && !animation.hasAnimator()) {
            animation.setAnimator(new JumpChargingAnimator());
        }
    }

    public void onJump(Player player, Parkourability parkourability) {
        double power = (double)this.chargeTick / 18.0;
        if (power >= 0.5) {
            this.started = true;
        } else {
            power = (double)this.lastChargeTick / 18.0;
            if (power > 0.5 && this.notChargeTick < 5) {
                this.started = true;
            }
        }
        if (this.started) {
            player.setDeltaMovement(player.getDeltaMovement().add(0.0, 0.16 * power, 0.0));
        }
    }

    public void onLand(Player player, Parkourability parkourability) {
        if (player.isLocalPlayer() && player instanceof LocalPlayer) {
            LocalPlayer cp = (LocalPlayer)player;
            if (!(!parkourability.getActionInfo().can(ChargeJump.class) || this.coolTimeTick > 0 || cp.input.up || cp.input.down || cp.input.right || cp.input.left || parkourability.get(FastRun.class).getNotDashTick(parkourability.getAdditionalProperties()) >= 15)) {
                this.lastChargeTick = this.chargeTick = 23;
                this.notChargeTick = 0;
            }
        }
    }

    @Override
    public boolean wantsToShowStatusBar(LocalPlayer player, Parkourability parkourability) {
        return this.isCharging();
    }

    @Override
    public float getStatusValue(LocalPlayer player, Parkourability parkourability) {
        return (float)this.getChargingTick() / 18.0f;
    }

    @Override
    public void saveSynchronizedState(ByteBuffer buffer) {
        buffer.putInt(this.chargeTick);
    }

    @Override
    public void restoreSynchronizedState(ByteBuffer buffer) {
        this.chargeTick = buffer.getInt();
    }

    public boolean isCharging() {
        return this.chargeTick > 0 && !this.isDoing();
    }

    public int getChargingTick() {
        return this.chargeTick;
    }

    public int getNotChargingTick() {
        return this.notChargeTick;
    }
}

