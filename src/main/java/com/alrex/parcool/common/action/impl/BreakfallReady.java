package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.platform.attachment.AttachmentHolder;

import com.alrex.parcool.api.SoundEvents;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.StaminaConsumeTiming;
import com.alrex.parcool.common.action.impl.Crawl;
import com.alrex.parcool.common.action.impl.Roll;
import com.alrex.parcool.common.action.impl.Tap;
import com.alrex.parcool.common.attachment.Attachments;
import com.alrex.parcool.common.attachment.client.LocalStamina;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.common.attachment.common.ReadonlyStamina;
import com.alrex.parcool.config.ParCoolConfig;
import java.nio.ByteBuffer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import com.alrex.parcool.platform.dist.Dist;
import com.alrex.parcool.platform.dist.OnlyIn;

public class BreakfallReady
extends Action {
    @OnlyIn(value=Dist.CLIENT)
    public void startBreakfall(Player player, Parkourability parkourability, boolean justTimed) {
        if (!(player instanceof LocalPlayer)) {
            return;
        }
        LocalPlayer localPlayer = (LocalPlayer)player;
        boolean playSound = false;
        if (justTimed && ParCoolConfig.Client.Booleans.EnableJustTimeEffectOfBreakfall.get().booleanValue()) {
            if (ParCoolConfig.Client.Booleans.EnableActionSounds.get().booleanValue()) {
                player.playSound((SoundEvent)SoundEvents.BREAKFALL_JUST_TIME.get(), 1.0f, 1.0f);
            }
            if (ParCoolConfig.Client.Booleans.EnableActionParticles.get().booleanValue() && ParCoolConfig.Client.Booleans.EnableActionParticlesOfJustTimeBreakfall.get().booleanValue()) {
                Vec3 pos = player.position();
                RandomSource rand = player.getRandom();
                for (int i = 0; i < 12; ++i) {
                    player.level().addParticle((ParticleOptions)ParticleTypes.END_ROD, pos.x(), pos.y() + (double)(player.getBbHeight() / 2.0f), pos.z(), (rand.nextDouble() - 0.5) * 0.5, (rand.nextDouble() - 0.5) * 0.5, (rand.nextDouble() - 0.5) * 0.5);
                }
            }
        } else if (ParCoolConfig.Client.Booleans.EnableActionSounds.get().booleanValue()) {
            playSound = true;
        }
        LocalStamina stamina = LocalStamina.get(localPlayer);
        if (KeyBindings.isAnyMovingKeyDown().booleanValue() && parkourability.getActionInfo().can(Roll.class) || !parkourability.getActionInfo().can(Tap.class)) {
            stamina.consume(localPlayer, (int)((justTimed ? 0.25f : 1.0f) * (float)parkourability.getActionInfo().getStaminaConsumptionOf(Roll.class)));
            if (playSound) {
                player.playSound((SoundEvent)SoundEvents.ROLL.get(), 1.0f, 1.0f);
            }
            parkourability.get(Roll.class).startRoll(player);
        } else {
            stamina.consume(localPlayer, (int)((justTimed ? 0.25f : 1.0f) * (float)parkourability.getActionInfo().getStaminaConsumptionOf(Tap.class)));
            if (playSound) {
                player.playSound((SoundEvent)SoundEvents.SAFETY_TAP.get(), 1.0f, 1.0f);
            }
            parkourability.get(Tap.class).startTap(player);
        }
    }

    @Override
    public boolean canStart(Player player, Parkourability parkourability, ByteBuffer startInfo) {
        return this.canContinue(player, parkourability);
    }

    @Override
    public boolean canContinue(Player player, Parkourability parkourability) {
        return KeyBindings.getKeyBreakfall().isDown() && !((ReadonlyStamina)AttachmentHolder.getData(player, Attachments.STAMINA)).isExhausted() && !parkourability.get(Crawl.class).isDoing() && !player.isInWaterOrBubble() && (!player.onGround() || parkourability.getAdditionalProperties().getLandingTick() < 3);
    }

    @Override
    public StaminaConsumeTiming getStaminaConsumeTiming() {
        return StaminaConsumeTiming.None;
    }
}

