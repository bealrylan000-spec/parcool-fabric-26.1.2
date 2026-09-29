package com.alrex.parcool.common.handlers;

import com.alrex.parcool.api.unstable.action.ParCoolActionEvent;
import com.alrex.parcool.common.action.impl.BreakfallReady;
import com.alrex.parcool.common.action.impl.ChargeJump;
import com.alrex.parcool.common.action.impl.Dodge;
import com.alrex.parcool.common.action.impl.HideInBlock;
import com.alrex.parcool.common.action.impl.Roll;
import com.alrex.parcool.common.action.impl.Tap;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.common.network.payload.StartBreakfallEventPayload;
import com.alrex.parcool.config.ParCoolConfig;
import com.alrex.parcool.utilities.WorldUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import com.alrex.parcool.platform.eventbus.Event;
import com.alrex.parcool.platform.eventbus.SubscribeEvent;
import com.alrex.parcool.platform.eventbus.NeoForge;
import com.alrex.parcool.platform.event.entity.living.LivingFallEvent;
import com.alrex.parcool.platform.event.entity.living.LivingIncomingDamageEvent;
import com.alrex.parcool.platform.network.PacketDistributor;

public class PlayerDamageHandler {
    @SubscribeEvent
    public static void onAttack(LivingIncomingDamageEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity instanceof Player) {
            Player player = (Player)entity;
            Parkourability parkourability = Parkourability.get(player);
            if (parkourability == null) {
                return;
            }
            Dodge dodge = parkourability.get(Dodge.class);
            if (dodge.isDoing()) {
                if (!parkourability.getServerLimitation().get(ParCoolConfig.Server.Booleans.DodgeProvideInvulnerableFrame).booleanValue()) {
                    return;
                }
                if (event.getSource().is(DamageTypeTags.BYPASSES_ARMOR)) {
                    return;
                }
                if (dodge.getDoingTick() <= 10) {
                    event.setCanceled(true);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onFall(LivingFallEvent event) {
        LivingEntity livingEntity = event.getEntity();
        if (livingEntity instanceof ServerPlayer) {
            ServerPlayer player = (ServerPlayer)livingEntity;
            parkourability = Parkourability.get((Player)player);
            if (((Parkourability)parkourability).get(BreakfallReady.class).isDoing() && (((Parkourability)parkourability).getActionInfo().can(Tap.class) || ((Parkourability)parkourability).getActionInfo().can(Roll.class))) {
                boolean justTime = ((Parkourability)parkourability).get(BreakfallReady.class).getDoingTick() < ((Parkourability)parkourability).getLimitedValue(ParCoolConfig.Client.Integers.JustTimeBreakfallTick, ParCoolConfig.Server.Integers.MaxJustTimeBreakfallTick);
                float distance = event.getDistance();
                if (!((double)distance > ((Parkourability)parkourability).getLimitedValue(ParCoolConfig.Client.Doubles.LowestFallDistanceForBreakfall, ParCoolConfig.Server.Doubles.MinLowestFallDistanceForBreakfall))) {
                    return;
                }
                PacketDistributor.sendToPlayer((ServerPlayer)player, (CustomPacketPayload)new StartBreakfallEventPayload(justTime), (CustomPacketPayload[])new CustomPacketPayload[0]);
                double damageRemoveHeight = ((Parkourability)parkourability).getLimitedValue(ParCoolConfig.Client.Doubles.DamageCompleteRemovableHeightBreakfall, ParCoolConfig.Server.Doubles.MaxDamageCompleteRemovableHeightBreakfall);
                if ((double)distance < damageRemoveHeight || justTime && (double)distance < damageRemoveHeight * 1.34) {
                    event.setCanceled(true);
                } else {
                    float damageReductionRate = (float)((Parkourability)parkourability).getLimitedValue(ParCoolConfig.Client.Doubles.DamageReductionRateBreakfall, ParCoolConfig.Server.Doubles.MaxDamageReductionRateBreakfall);
                    event.setDamageMultiplier(event.getDamageMultiplier() * (justTime ? 0.66f * damageReductionRate : damageReductionRate));
                }
            } else {
                Tuple<BlockPos, BlockPos> area;
                HideInBlock hideInBlock = ((Parkourability)parkourability).get(HideInBlock.class);
                if (hideInBlock.isStandbyInAir((Parkourability)parkourability) && ((Parkourability)parkourability).getActionInfo().can(HideInBlock.class) && !((ParCoolActionEvent.TryToStartEvent)NeoForge.EVENT_BUS.post((Event)new ParCoolActionEvent.TryToStartEvent((Player)player, hideInBlock))).isCanceled() && !((ParCoolActionEvent.TryToStart)NeoForge.EVENT_BUS.post((Event)new ParCoolActionEvent.TryToStart((Player)player, hideInBlock))).isCanceled() && (area = WorldUtil.getHideAbleSpace((Entity)player, new BlockPos((Vec3i)player.blockPosition().below()))) != null) {
                    boolean stand;
                    boolean bl = stand = player.getBbHeight() < (float)(Math.abs(((BlockPos)area.getB()).getY() - ((BlockPos)area.getA()).getY()) + 1);
                    if (!stand) {
                        if (event.getDistance() < 10.0f) {
                            event.setCanceled(true);
                        } else {
                            event.setDamageMultiplier(event.getDamageMultiplier() * 0.4f);
                        }
                    }
                }
            }
        } else {
            parkourability = event.getEntity();
            if (parkourability instanceof Player) {
                Player player = (Player)parkourability;
                if (!player.isLocalPlayer()) {
                    return;
                }
                parkourability = Parkourability.get(player);
                if (((Parkourability)parkourability).getAdditionalProperties().getNotLandingTick() > 5 && event.getDistance() < 0.4f) {
                    ((Parkourability)parkourability).get(ChargeJump.class).onLand(player, (Parkourability)parkourability);
                }
            }
        }
    }
}

