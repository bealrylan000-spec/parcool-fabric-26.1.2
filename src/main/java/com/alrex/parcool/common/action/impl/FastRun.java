package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.client.animation.impl.FastRunningAnimator;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.client.input.KeyRecorder;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.AdditionalProperties;
import com.alrex.parcool.common.action.StaminaConsumeTiming;
import com.alrex.parcool.common.action.impl.ClingToCliff;
import com.alrex.parcool.common.action.impl.Crawl;
import com.alrex.parcool.common.action.impl.HangDown;
import com.alrex.parcool.common.action.impl.HideInBlock;
import com.alrex.parcool.common.action.impl.RideZipline;
import com.alrex.parcool.common.attachment.client.Animation;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.common.info.ActionInfo;
import com.alrex.parcool.config.ParCoolConfig;
import java.nio.ByteBuffer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import com.alrex.parcool.platform.dist.Dist;
import com.alrex.parcool.platform.dist.OnlyIn;

public class FastRun
extends Action {
    private static final Identifier FAST_RUNNING_MODIFIER = Identifier.fromNamespaceAndPath((String)"parcool", (String)"modifier.speed.fastrun");
    private double speedModifier = 0.0;
    private boolean toggleStatus = false;
    private int lastDashTick = 0;

    public double getSpeedModifier(ActionInfo info) {
        return Math.min(info.getClientSetting().get(ParCoolConfig.Client.Doubles.FastRunSpeedModifier), info.getServerLimitation().get(ParCoolConfig.Server.Doubles.MaxFastRunSpeedModifier));
    }

    @Override
    public void onServerTick(Player player, Parkourability parkourability) {
        AttributeInstance attr = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (attr == null) {
            return;
        }
        if (attr.getModifier(FAST_RUNNING_MODIFIER) != null) {
            attr.removeModifier(FAST_RUNNING_MODIFIER);
        }
        if (this.isDoing()) {
            attr.addTransientModifier(new AttributeModifier(FAST_RUNNING_MODIFIER, this.speedModifier / 100.0, AttributeModifier.Operation.ADD_VALUE));
        }
    }

    @Override
    public void onClientTick(Player player, Parkourability parkourability) {
        if (player.isLocalPlayer()) {
            if (ParCoolConfig.Client.getInstance().FastRunControl.get() == ControlType.Toggle && parkourability.getAdditionalProperties().getSprintingTick() > 3) {
                if (KeyRecorder.keyFastRunning.isPressed()) {
                    this.toggleStatus = !this.toggleStatus;
                }
            } else {
                this.toggleStatus = false;
            }
        }
    }

    @Override
    public StaminaConsumeTiming getStaminaConsumeTiming() {
        return StaminaConsumeTiming.OnWorking;
    }

    @Override
    public boolean canStart(Player player, Parkourability parkourability, ByteBuffer startInfo) {
        return this.canContinue(player, parkourability);
    }

    @Override
    public boolean canContinue(Player player, Parkourability parkourability) {
        return !player.isInWaterOrBubble() && player.getVehicle() == null && !player.isFallFlying() && player.isSprinting() && !player.isVisuallyCrawling() && !player.isSwimming() && !player.isShiftKeyDown() && !parkourability.get(Crawl.class).isDoing() && !parkourability.get(ClingToCliff.class).isDoing() && !parkourability.get(HangDown.class).isDoing() && !parkourability.get(RideZipline.class).isDoing() && !parkourability.get(HideInBlock.class).isDoing() && (ParCoolConfig.Client.getInstance().FastRunControl.get() == ControlType.PressKey && KeyBindings.getKeyFastRunning().isDown() || ParCoolConfig.Client.getInstance().FastRunControl.get() == ControlType.Toggle && this.toggleStatus || ParCoolConfig.Client.getInstance().FastRunControl.get() == ControlType.Auto);
    }

    @Override
    public void onWorkingTickInClient(Player player, Parkourability parkourability) {
        Animation animation = Animation.get(player);
        if (animation != null && !animation.hasAnimator()) {
            animation.setAnimator(new FastRunningAnimator());
        }
    }

    @Override
    public void onStartInServer(Player player, Parkourability parkourability, ByteBuffer startData) {
        this.speedModifier = this.getSpeedModifier(parkourability.getActionInfo());
    }

    @Override
    public void onStopInLocalClient(Player player) {
        Parkourability parkourability = Parkourability.get(player);
        if (parkourability == null) {
            return;
        }
        this.lastDashTick = this.getDashTick(parkourability.getAdditionalProperties());
    }

    @OnlyIn(value=Dist.CLIENT)
    public boolean canActWithRunning(Player player) {
        return ParCoolConfig.Client.Booleans.SubstituteSprintForFastRun.get() != false ? player.isSprinting() : this.isDoing();
    }

    @OnlyIn(value=Dist.CLIENT)
    public int getDashTick(AdditionalProperties properties) {
        return ParCoolConfig.Client.Booleans.SubstituteSprintForFastRun.get() != false ? properties.getSprintingTick() : this.getDoingTick();
    }

    @OnlyIn(value=Dist.CLIENT)
    public int getNotDashTick(AdditionalProperties properties) {
        return ParCoolConfig.Client.Booleans.SubstituteSprintForFastRun.get() != false ? properties.getNotSprintingTick() : this.getNotDoingTick();
    }

    public int getLastDashTick() {
        return this.lastDashTick;
    }

    public static enum ControlType {
        PressKey,
        Toggle,
        Auto;

    }
}

