package com.alrex.parcool.common.action.impl;

import com.alrex.parcool.client.RenderBehaviorEnforcer;
import com.alrex.parcool.client.animation.impl.HideInBlockAnimator;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.BehaviorEnforcer;
import com.alrex.parcool.common.action.StaminaConsumeTiming;
import com.alrex.parcool.common.action.impl.Crawl;
import com.alrex.parcool.common.action.impl.Dive;
import com.alrex.parcool.common.attachment.client.Animation;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.config.ParCoolConfig;
import com.alrex.parcool.utilities.BufferUtil;
import com.alrex.parcool.utilities.WorldUtil;
import java.nio.ByteBuffer;
import javax.annotation.Nullable;
import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.util.Mth;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import com.alrex.parcool.platform.dist.Dist;
import com.alrex.parcool.platform.dist.OnlyIn;

public class HideInBlock
extends Action {
    private static final BehaviorEnforcer.ID ID_SHOW_NAME = BehaviorEnforcer.newID();
    private static final BehaviorEnforcer.ID ID_SNEAK = BehaviorEnforcer.newID();
    @Nullable
    Vec3 hidingPoint = null;
    @Nullable
    Tuple<BlockPos, BlockPos> hidingArea = null;
    @Nullable
    Vec3 enterPoint = null;
    @Nullable
    Vec3 lookDirection = null;
    boolean hidingBlockChanged = false;
    boolean keyPressed;
    boolean startedFromDiving;

    @Nullable
    public Vec3 getLookDirection() {
        return this.lookDirection;
    }

    public boolean isStandbyInAir(Parkourability parkourability) {
        if (!this.keyPressed) {
            return false;
        }
        Dive dive = parkourability.get(Dive.class);
        return (dive.isDoing() || dive.getNotDoingTick() < 2) && parkourability.getAdditionalProperties().getLandingTick() <= 1;
    }

    @Override
    public boolean canStart(Player player, Parkourability parkourability, ByteBuffer startInfo) {
        HitResult result;
        if (player.isSprinting() || player.noPhysics || !player.onGround() || player.isInWater() || player.isPassenger() || player.isVisuallySwimming() || this.getNotDoingTick() < 6 || parkourability.get(Crawl.class).isDoing()) {
            return false;
        }
        BlockPos hideBaseBlockPos = null;
        boolean startFromDiving = false;
        if (this.isStandbyInAir(parkourability)) {
            hideBaseBlockPos = player.blockPosition().below();
            startFromDiving = true;
        } else if (KeyBindings.getKeyHideInBlock().isDown() && (!ParCoolConfig.Client.Booleans.HideInBlockSneakNeeded.get().booleanValue() || player.getPose() == Pose.CROUCHING) && (result = Minecraft.getInstance().hitResult) instanceof BlockHitResult && parkourability.isDoingNothing()) {
            hideBaseBlockPos = ((BlockHitResult)result).getBlockPos();
        }
        if (!startFromDiving && player.hurtTime > 0) {
            return false;
        }
        if (hideBaseBlockPos != null) {
            Vec3 direction;
            boolean stand;
            Tuple hideArea = WorldUtil.getHideAbleSpace((Entity)player, hideBaseBlockPos);
            if (hideArea == null) {
                return false;
            }
            Vec3 hidePoint = new Vec3(0.5 + (double)(((BlockPos)hideArea.getA()).getX() + ((BlockPos)hideArea.getB()).getX()) / 2.0, (double)Math.min(((BlockPos)hideArea.getA()).getY(), ((BlockPos)hideArea.getB()).getY()), 0.5 + (double)(((BlockPos)hideArea.getA()).getZ() + ((BlockPos)hideArea.getB()).getZ()) / 2.0);
            if (!player.position().closerThan((Position)hidePoint, 1.8)) {
                return false;
            }
            int minX = Math.min(((BlockPos)hideArea.getA()).getX(), ((BlockPos)hideArea.getB()).getX());
            int maxX = Math.max(((BlockPos)hideArea.getA()).getX(), ((BlockPos)hideArea.getB()).getX());
            int minY = Math.min(((BlockPos)hideArea.getA()).getY(), ((BlockPos)hideArea.getB()).getY());
            int maxY = Math.max(((BlockPos)hideArea.getA()).getY(), ((BlockPos)hideArea.getB()).getY());
            int minZ = Math.min(((BlockPos)hideArea.getA()).getZ(), ((BlockPos)hideArea.getB()).getZ());
            int maxZ = Math.max(((BlockPos)hideArea.getA()).getZ(), ((BlockPos)hideArea.getB()).getZ());
            hideArea = new Tuple((Object)new BlockPos(minX, minY, minZ), (Object)new BlockPos(maxX, maxY, maxZ));
            boolean bl = stand = player.getBbHeight() < (float)(((BlockPos)hideArea.getB()).getY() - ((BlockPos)hideArea.getA()).getY() + 1);
            if (stand && startFromDiving) {
                return false;
            }
            if (stand) {
                Vec3 lookAngle = player.getLookAngle();
                direction = Math.abs(lookAngle.x()) > Math.abs(lookAngle.z()) ? new Vec3(lookAngle.x() > 0.0 ? 1.0 : -1.0, 0.0, 0.0) : new Vec3(0.0, 0.0, lookAngle.z() > 0.0 ? 1.0 : -1.0);
            } else {
                boolean zLonger;
                boolean bl2 = zLonger = Math.abs(((BlockPos)hideArea.getA()).getZ() - ((BlockPos)hideArea.getB()).getZ()) > Math.abs(((BlockPos)hideArea.getA()).getX() - ((BlockPos)hideArea.getB()).getX());
                direction = zLonger ? new Vec3(0.0, 0.0, player.getLookAngle().z() > 0.0 ? 1.0 : -1.0) : new Vec3(player.getLookAngle().x() > 0.0 ? 1.0 : -1.0, 0.0, 0.0);
            }
            BufferUtil.wrap(startInfo).putBoolean(stand).putBoolean(startFromDiving).putBlockPos((BlockPos)hideArea.getA()).putBlockPos((BlockPos)hideArea.getB()).putVec3(hidePoint).putVec3(player.position()).putVec3(direction);
            return true;
        }
        return false;
    }

    @Override
    public boolean canContinue(Player player, Parkourability parkourability) {
        if (this.hidingBlockChanged) {
            this.hidingBlockChanged = false;
            return false;
        }
        return (player.hurtTime <= 0 || this.startedFromDiving && this.getDoingTick() < 10) && player.getPose() == Pose.STANDING && (this.getDoingTick() < 6 || KeyBindings.getKeyHideInBlock().isDown() || KeyBindings.getKeySneak().isDown());
    }

    @Override
    public void onStart(Player player, Parkourability parkourability, ByteBuffer startData) {
        boolean _stand = BufferUtil.getBoolean(startData);
        this.startedFromDiving = BufferUtil.getBoolean(startData);
        this.hidingArea = new Tuple((Object)BufferUtil.getBlockPos(startData), (Object)BufferUtil.getBlockPos(startData));
        this.hidingPoint = BufferUtil.getVec3(startData);
        this.enterPoint = BufferUtil.getVec3(startData);
        this.lookDirection = BufferUtil.getVec3(startData);
        if (this.startedFromDiving) {
            parkourability.getBehaviorEnforcer().setMarkerEnforcePosition(this::isDoing, () -> this.hidingPoint);
        } else {
            parkourability.getBehaviorEnforcer().setMarkerEnforcePosition(this::isDoing, () -> {
                if (this.getDoingTick() == 0) {
                    return this.hidingPoint.subtract(this.enterPoint).scale(0.75).add(this.enterPoint);
                }
                return this.hidingPoint;
            });
        }
        parkourability.getBehaviorEnforcer().addMarkerCancellingSneak(ID_SNEAK, this::isDoing);
        player.setPose(Pose.STANDING);
        player.noPhysics = true;
        player.playSound(player.level().getBlockState(new BlockPos(Mth.floor((double)this.hidingPoint.x()), Mth.floor((double)(this.hidingPoint.y() + 0.2)), Mth.floor((double)this.hidingPoint.z()))).getSoundType().getBreakSound(), 1.0f, 1.0f);
    }

    @Override
    public void onStartInLocalClient(Player player, Parkourability parkourability, ByteBuffer startData) {
        boolean stand = BufferUtil.getBoolean(startData);
        RenderBehaviorEnforcer.serMarkerEnforceCameraType(this::isDoing, () -> CameraType.THIRD_PERSON_BACK);
        parkourability.getBehaviorEnforcer().addMarkerCancellingShowName(ID_SHOW_NAME, this::isDoing);
        this.spawnOnHideParticles(player);
        Animation animation = Animation.get(player);
        animation.setAnimator(new HideInBlockAnimator(stand, this.startedFromDiving));
    }

    @Override
    public void onStartInOtherClient(Player player, Parkourability parkourability, ByteBuffer startData) {
        boolean stand = BufferUtil.getBoolean(startData);
        parkourability.getBehaviorEnforcer().addMarkerCancellingShowName(ID_SHOW_NAME, this::isDoing);
        this.spawnOnHideParticles(player);
        Animation animation = Animation.get(player);
        animation.setAnimator(new HideInBlockAnimator(stand, this.startedFromDiving));
    }

    @Override
    public void onWorkingTickInServer(Player player, Parkourability parkourability) {
        if (this.hidingPoint == null) {
            return;
        }
        player.setPos(this.hidingPoint.x(), this.hidingPoint.y(), this.hidingPoint.z());
    }

    @Override
    public void onWorkingTick(Player player, Parkourability parkourability) {
        player.setDeltaMovement(Vec3.ZERO);
        player.noPhysics = true;
        player.setSprinting(false);
        player.setPose(Pose.STANDING);
    }

    @Override
    public void onStopInLocalClient(Player player) {
        Vec3 hidePos = this.hidingPoint;
        Vec3 entPos = this.enterPoint;
        Parkourability parkourability = Parkourability.get(player);
        parkourability.getBehaviorEnforcer().setMarkerEnforcePosition(() -> this.getNotDoingTick() <= 1, () -> {
            if (this.getNotDoingTick() == 0) {
                return entPos.subtract(hidePos).scale(0.65).add(hidePos);
            }
            return entPos;
        });
        this.spawnOnHideParticles(player);
        player.playSound(player.level().getBlockState(new BlockPos(Mth.floor((double)this.hidingPoint.x()), Mth.floor((double)(this.hidingPoint.y() + 0.2)), Mth.floor((double)this.hidingPoint.z()))).getSoundType().getBreakSound(), 1.0f, 1.0f);
    }

    @Override
    public void onStopInOtherClient(Player player) {
        this.spawnOnHideParticles(player);
        player.playSound(player.level().getBlockState(new BlockPos(Mth.floor((double)this.hidingPoint.x()), Mth.floor((double)(this.hidingPoint.y() + 0.2)), Mth.floor((double)this.hidingPoint.z()))).getSoundType().getBreakSound(), 1.0f, 1.0f);
    }

    @Override
    public void onStop(Player player) {
        this.hidingPoint = null;
        this.enterPoint = null;
        this.hidingArea = null;
        this.lookDirection = null;
        player.noPhysics = false;
    }

    @OnlyIn(value=Dist.CLIENT)
    private void spawnOnHideParticles(Player player) {
        if (this.hidingArea == null) {
            return;
        }
        Level world = player.level();
        int minX = ((BlockPos)this.hidingArea.getA()).getX();
        int minY = ((BlockPos)this.hidingArea.getA()).getY();
        int minZ = ((BlockPos)this.hidingArea.getA()).getZ();
        int maxX = ((BlockPos)this.hidingArea.getB()).getX();
        int maxY = ((BlockPos)this.hidingArea.getB()).getY();
        int maxZ = ((BlockPos)this.hidingArea.getB()).getZ();
        for (int y = minY; y <= maxY; ++y) {
            for (int z = minZ; z <= maxZ; ++z) {
                BlockPos pos;
                for (int x = minX; x <= maxX && world.isLoaded(pos = new BlockPos(x, y, z)); ++x) {
                    Minecraft.getInstance().particleEngine.destroy(pos, world.getBlockState(pos));
                }
            }
        }
    }

    @Override
    public void onClientTick(Player player, Parkourability parkourability) {
        this.keyPressed = KeyBindings.getKeyHideInBlock().isDown();
    }

    @Override
    public void saveSynchronizedState(ByteBuffer buffer) {
        BufferUtil.wrap(buffer).putBoolean(this.keyPressed);
    }

    @Override
    public void restoreSynchronizedState(ByteBuffer buffer) {
        this.keyPressed = BufferUtil.getBoolean(buffer);
    }

    private boolean isHidingBlock(BlockPos pos) {
        if (this.hidingArea == null) {
            return false;
        }
        BlockPos posA = (BlockPos)this.hidingArea.getA();
        BlockPos posB = (BlockPos)this.hidingArea.getB();
        return posA.getX() <= pos.getX() && pos.getX() <= posB.getX() && posA.getY() <= pos.getY() && pos.getY() <= posB.getY() && posA.getZ() <= pos.getZ() && pos.getZ() <= posB.getZ();
    }

    @OnlyIn(value=Dist.CLIENT)
    public void notifyBlockChanged(BlockPos pos) {
        if (this.isHidingBlock(pos)) {
            this.hidingBlockChanged = true;
        }
    }

    @Override
    public StaminaConsumeTiming getStaminaConsumeTiming() {
        return StaminaConsumeTiming.None;
    }

    @Nullable
    public Tuple<BlockPos, BlockPos> getHidingArea() {
        return this.hidingArea;
    }
}

