package com.alrex.parcool.mixin.common;

import com.alrex.parcool.api.unstable.action.ParCoolActionEvent;
import com.alrex.parcool.common.action.impl.ChargeJump;
import com.alrex.parcool.common.action.impl.ClimbPoles;
import com.alrex.parcool.common.action.impl.ClimbUp;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.common.tags.BlockTags;
import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CrossCollisionBlock;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.EndRodBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;
import com.alrex.parcool.platform.eventbus.Event;
import com.alrex.parcool.platform.eventbus.NeoForge;
import com.alrex.parcool.platform.NeoForgeConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={LivingEntity.class})
public abstract class LivingEntityMixin
extends Entity {
    public LivingEntityMixin(EntityType<?> p_i48580_1_, Level p_i48580_2_) {
        super(p_i48580_1_, p_i48580_2_);
    }

    @Inject(method={"Lnet/minecraft/world/entity/LivingEntity;onClimbable()Z"}, at={@At(value="HEAD")}, cancellable=true)
    public void onClimbable(CallbackInfoReturnable<Boolean> cir) {
        if (this.isSpectator()) {
            cir.setReturnValue((Object)false);
        } else {
            LivingEntity entity = (LivingEntity)this;
            if (!(entity instanceof Player)) {
                return;
            }
            Player player = (Player)entity;
            Parkourability parkourability = Parkourability.get(player);
            if (parkourability == null) {
                return;
            }
            if (!parkourability.getActionInfo().can(ClimbPoles.class) || ((ParCoolActionEvent.TryToStartEvent)NeoForge.EVENT_BUS.post((Event)new ParCoolActionEvent.TryToStartEvent(player, parkourability.get(ClimbPoles.class)))).isCanceled() || ((ParCoolActionEvent.TryToStart)NeoForge.EVENT_BUS.post((Event)new ParCoolActionEvent.TryToStart(player, parkourability.get(ClimbPoles.class)))).isCanceled()) {
                return;
            }
            if (parkourability.get(ClimbUp.class).isDoing()) {
                return;
            }
            ChargeJump chargeJump = parkourability.get(ChargeJump.class);
            if (chargeJump.isDoing() || chargeJump.isCharging()) {
                return;
            }
            BlockPos blockpos = this.blockPosition();
            BlockState blockstate = this.getBlockStateOn();
            boolean onLadder = this.parCool$isLivingOnCustomLadder(blockstate, entity.level(), blockpos, entity);
            if (onLadder) {
                cir.setReturnValue((Object)true);
            }
        }
    }

    @Unique
    public boolean parCool$isLivingOnCustomLadder(@Nonnull BlockState state, @Nonnull Level world, @Nonnull BlockPos pos, @Nonnull LivingEntity entity) {
        boolean isSpectator;
        boolean bl = isSpectator = entity instanceof Player && entity.isSpectator();
        if (isSpectator) {
            return false;
        }
        if (!((Boolean)NeoForgeConfig.SERVER.fullBoundingBoxLadders.get()).booleanValue()) {
            return this.parCool$isCustomLadder(state, world, pos, entity);
        }
        AABB bb = entity.getBoundingBox();
        int mX = Mth.floor((double)bb.minX);
        int mY = Mth.floor((double)bb.minY);
        int mZ = Mth.floor((double)bb.minZ);
        int y2 = mY;
        while ((double)y2 < bb.maxY) {
            int x2 = mX;
            while ((double)x2 < bb.maxX) {
                int z2 = mZ;
                while ((double)z2 < bb.maxZ) {
                    BlockPos tmp = new BlockPos(x2, y2, z2);
                    if (!world.isLoaded(pos)) {
                        return false;
                    }
                    state = world.getBlockState(tmp);
                    if (this.parCool$isCustomLadder(state, world, tmp, entity)) {
                        return true;
                    }
                    ++z2;
                }
                ++x2;
            }
            ++y2;
        }
        return false;
    }

    @Unique
    private boolean parCool$isCustomLadder(BlockState state, Level world, BlockPos pos, LivingEntity entity) {
        Block block = state.getBlock();
        if (block instanceof CrossCollisionBlock) {
            boolean stacked;
            int zCount = 0;
            int xCount = 0;
            if (((Boolean)state.getValue((Property)CrossCollisionBlock.NORTH)).booleanValue()) {
                ++zCount;
            }
            if (((Boolean)state.getValue((Property)CrossCollisionBlock.SOUTH)).booleanValue()) {
                ++zCount;
            }
            if (((Boolean)state.getValue((Property)CrossCollisionBlock.EAST)).booleanValue()) {
                ++xCount;
            }
            if (((Boolean)state.getValue((Property)CrossCollisionBlock.WEST)).booleanValue()) {
                ++xCount;
            }
            boolean bl = stacked = world.isLoaded(pos.above()) && world.getBlockState(pos.above()).getBlock() instanceof CrossCollisionBlock;
            if (!stacked && world.isLoaded(pos.below()) && world.getBlockState(pos.below()).getBlock() instanceof CrossCollisionBlock) {
                stacked = true;
            }
            return (zCount + xCount <= 1 || zCount == 1 && xCount == 1) && stacked;
        }
        if (block instanceof RotatedPillarBlock) {
            boolean stacked;
            boolean bl = stacked = world.isLoaded(pos.above()) && world.getBlockState(pos.above()).getBlock() instanceof RotatedPillarBlock;
            if (!stacked && world.isLoaded(pos.below()) && world.getBlockState(pos.below()).getBlock() instanceof RotatedPillarBlock) {
                stacked = true;
            }
            return !state.isCollisionShapeFullBlock((BlockGetter)world, pos) && stacked && ((Direction.Axis)state.getValue((Property)RotatedPillarBlock.AXIS)).isVertical();
        }
        if (block instanceof EndRodBlock) {
            Direction direction = (Direction)state.getValue((Property)DirectionalBlock.FACING);
            return !state.isCollisionShapeFullBlock((BlockGetter)world, pos) && (direction == Direction.UP || direction == Direction.DOWN);
        }
        return state.getTags().anyMatch(it -> it.equals(BlockTags.POLE_CLIMBABLE));
    }

    @Inject(method={"setSprinting"}, at={@At(value="HEAD")}, cancellable=true)
    public void onSetSprinting(boolean sprint, CallbackInfo ci) {
        Parkourability parkourability;
        LivingEntityMixin livingEntityMixin = this;
        if (!(livingEntityMixin instanceof Player)) {
            return;
        }
        Player player = (Player)livingEntityMixin;
        if (player.isLocalPlayer() && (parkourability = Parkourability.get(player)) != null && parkourability.getBehaviorEnforcer().cancelSprint()) {
            ci.cancel();
        }
    }
}

