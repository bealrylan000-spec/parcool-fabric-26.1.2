package com.alrex.parcool.mixin.client;

import com.alrex.parcool.common.attachment.common.Parkourability;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={LivingEntityRenderer.class})
public abstract class LivingRendererMixin<T extends LivingEntity> {

    @Inject(method={"shouldShowName(Lnet/minecraft/world/entity/LivingEntity;D)Z"}, at={@At(value="HEAD")}, cancellable=true)
    protected void onShouldShowName(T entity, double distanceToCameraSq, CallbackInfoReturnable<Boolean> cir) {
        if (entity instanceof Player) {
            Player player = (Player)entity;
            Parkourability parkourability = Parkourability.get(player);
            if (parkourability == null) {
                return;
            }
            if (parkourability.getBehaviorEnforcer().cancelShowingName()) {
                cir.setReturnValue((Object)false);
            }
        }
    }
}

