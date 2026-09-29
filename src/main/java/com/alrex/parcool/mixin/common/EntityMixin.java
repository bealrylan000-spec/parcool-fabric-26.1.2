package com.alrex.parcool.mixin.common;

import com.alrex.parcool.common.action.impl.HideInBlock;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.config.ParCoolConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={Entity.class})
public abstract class EntityMixin {
    @Shadow
    public boolean noPhysics;

    @Shadow
    public abstract void setBoundingBox(AABB var1);

    @Shadow
    public abstract AABB getBoundingBox();

    @Shadow
    public abstract void setPos(double var1, double var3, double var5);

    @Inject(method={"getEyeHeight()F"}, at={@At(value="HEAD")}, cancellable=true)
    public void onGetEyeHeight(CallbackInfoReturnable<Float> cir) {
        EntityMixin entityMixin = this;
        if (!(entityMixin instanceof Player)) {
            return;
        }
        Player player = (Player)entityMixin;
        Parkourability parkourability = Parkourability.get(player);
        if (parkourability == null) {
            return;
        }
        HideInBlock ability = parkourability.get(HideInBlock.class);
        Tuple<BlockPos, BlockPos> area = ability.getHidingArea();
        if (ability.isDoing() && area != null) {
            float eyeHeight;
            int areaHeight = ((BlockPos)area.getB()).getY() - ((BlockPos)area.getA()).getY() + 1;
            if ((float)areaHeight < (eyeHeight = player.getDimensions(Pose.STANDING).height() * 0.85f)) {
                cir.setReturnValue((Object)Float.valueOf(eyeHeight));
            } else {
                cir.setReturnValue((Object)Float.valueOf((float)areaHeight + 0.2f));
            }
            return;
        }
    }

    @Inject(method={"isInWall"}, at={@At(value="HEAD")}, cancellable=true)
    public void onIsInWall(CallbackInfoReturnable<Boolean> cir) {
        EntityMixin entityMixin = this;
        if (!(entityMixin instanceof Player)) {
            return;
        }
        Player player = (Player)entityMixin;
        Parkourability parkourability = Parkourability.get(player);
        if (parkourability == null) {
            return;
        }
        HideInBlock hideInBlock = parkourability.get(HideInBlock.class);
        if (hideInBlock.isDoing() || hideInBlock.getNotDoingTick() < 2) {
            cir.setReturnValue((Object)false);
        }
    }

    @Inject(method={"move"}, at={@At(value="HEAD")}, cancellable=true)
    public void onMove(MoverType type, Vec3 pos, CallbackInfo ci) {
        EntityMixin entityMixin = this;
        if (!(entityMixin instanceof Player)) {
            return;
        }
        Player player = (Player)entityMixin;
        Parkourability parkourability = Parkourability.get(player);
        if (parkourability == null) {
            return;
        }
        Vec3 enforcedPos = parkourability.getBehaviorEnforcer().getEnforcedPosition();
        if (enforcedPos != null) {
            ci.cancel();
            Vec3 dMove = enforcedPos.subtract(player.position());
            this.noPhysics = true;
            this.setBoundingBox(this.getBoundingBox().move(dMove));
            this.setPos(player.getX() + dMove.x, player.getY() + dMove.y, player.getZ() + dMove.z);
        }
    }

    @Inject(method={"onGround"}, at={@At(value="HEAD")}, cancellable=true)
    public void onOnGround(CallbackInfoReturnable<Boolean> cir) {
        EntityMixin entityMixin = this;
        if (!(entityMixin instanceof Player)) {
            return;
        }
        Player player = (Player)entityMixin;
        Parkourability parkourability = Parkourability.get(player);
        if (parkourability == null) {
            return;
        }
        if (parkourability.getAdditionalProperties().isInAirByJumping()) {
            return;
        }
        if (parkourability.getAdditionalProperties().getActualNotLandingTick() < parkourability.getLimitedValue(ParCoolConfig.Client.Integers.CoyoteTime, ParCoolConfig.Server.Integers.MaxCoyoteTime)) {
            cir.setReturnValue((Object)true);
        }
    }
}

