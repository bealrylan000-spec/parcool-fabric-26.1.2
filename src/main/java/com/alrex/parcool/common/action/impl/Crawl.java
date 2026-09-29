package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.client.animation.impl.CrawlAnimator;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.client.input.KeyRecorder;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.StaminaConsumeTiming;
import com.alrex.parcool.common.action.impl.ClingToCliff;
import com.alrex.parcool.common.action.impl.Dive;
import com.alrex.parcool.common.action.impl.HideInBlock;
import com.alrex.parcool.common.action.impl.RideZipline;
import com.alrex.parcool.common.action.impl.Roll;
import com.alrex.parcool.common.action.impl.Tap;
import com.alrex.parcool.common.action.impl.Vault;
import com.alrex.parcool.common.attachment.client.Animation;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.config.ParCoolConfig;
import java.nio.ByteBuffer;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import com.alrex.parcool.platform.dist.Dist;
import com.alrex.parcool.platform.dist.OnlyIn;

public class Crawl
extends Action {
    public boolean toggleStatus = false;

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public boolean canStart(Player player, Parkourability parkourability, ByteBuffer startInfo) {
        Pose pose = player.getPose();
        return !(!this.isActionInvoked(player) || !this.disambiguateCommands(player, pose) || parkourability.isDoingAny(Roll.class, Tap.class, ClingToCliff.class, Dive.class) != false || parkourability.get(Vault.class).getNotDoingTick() < 8 || parkourability.get(HideInBlock.class).isDoing() || parkourability.get(RideZipline.class).isDoing() || player.getVehicle() != null || pose != Pose.STANDING && pose != Pose.CROUCHING || player.isInWaterOrBubble() || player.isFallFlying() || player.onClimbable() || !player.onGround() && ParCoolConfig.Client.Booleans.EnableCrawlInAir.get() == false);
    }

    private boolean isActionInvoked(Player player) {
        return ParCoolConfig.Client.getInstance().CrawlControl.get() == ControlType.PressKey && KeyRecorder.keyCrawlState.isPressed() || ParCoolConfig.Client.getInstance().CrawlControl.get() == ControlType.Toggle && this.toggleStatus;
    }

    private boolean disambiguateCommands(Player player, Pose pose) {
        return pose == Pose.CROUCHING || !KeyRecorder.keyDodge.isPressed();
    }

    @Override
    public void onClientTick(Player player, Parkourability parkourability) {
        if (player.isLocalPlayer()) {
            if (ParCoolConfig.Client.getInstance().CrawlControl.get() == ControlType.Toggle) {
                if (KeyRecorder.keyCrawlState.isPressed()) {
                    this.toggleStatus = !this.toggleStatus;
                }
            } else {
                this.toggleStatus = false;
            }
        }
    }

    @Override
    public boolean canContinue(Player player, Parkourability parkourability) {
        if (player.canPlayerFitWithinBlocksAndEntitiesWhen(Pose.STANDING)) {
            switch (((ControlType)((Object)ParCoolConfig.Client.getInstance().CrawlControl.get())).ordinal()) {
                case 1: {
                    if (this.toggleStatus) break;
                    return false;
                }
                case 0: {
                    if (KeyBindings.getKeyCrawl().isDown()) break;
                    return false;
                }
            }
        }
        return !parkourability.get(Roll.class).isDoing() && !parkourability.get(Tap.class).isDoing() && !parkourability.get(ClingToCliff.class).isDoing() && !parkourability.get(Dive.class).isDoing() && parkourability.get(Vault.class).getNotDoingTick() >= 8 && player.getVehicle() == null && !player.isInWaterOrBubble() && !player.isFallFlying() && !player.onClimbable() && (player.onGround() || ParCoolConfig.Client.Booleans.EnableCrawlInAir.get() != false);
    }

    @Override
    public void onWorkingTickInClient(Player player, Parkourability parkourability) {
        Animation animation = Animation.get(player);
        if (!animation.hasAnimator()) {
            animation.setAnimator(new CrawlAnimator());
        }
    }

    @Override
    public StaminaConsumeTiming getStaminaConsumeTiming() {
        return StaminaConsumeTiming.None;
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

    public static enum ControlType {
        PressKey,
        Toggle;

    }
}

