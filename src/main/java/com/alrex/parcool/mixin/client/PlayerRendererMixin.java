package com.alrex.parcool.mixin.client;

import com.alrex.parcool.client.animation.PlayerModelRotator;
import com.alrex.parcool.common.attachment.client.Animation;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={PlayerRenderer.class})
public abstract class PlayerRendererMixin
extends LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {
    @Unique
    private PlayerModelRotator parCool$rotator = null;

    public PlayerRendererMixin(EntityRendererProvider.Context p_174289_, PlayerModel<AbstractClientPlayer> p_174290_, float p_174291_) {
        super(p_174289_, p_174290_, p_174291_);
    }

    @Inject(method={"Lnet/minecraft/client/renderer/entity/player/PlayerRenderer;setupRotations(Lnet/minecraft/client/player/AbstractClientPlayer;Lcom/mojang/blaze3d/vertex/PoseStack;FFFF)V"}, at={@At(value="RETURN")})
    protected void onSetupRotationsTail(AbstractClientPlayer player, PoseStack poseStack, float bob, float yBodyRot, float partialTick, float scale, CallbackInfo ci) {
        Animation animation = Animation.get((Player)player);
        if (animation == null) {
            return;
        }
        if (this.parCool$rotator != null) {
            animation.rotatePost(player, this.parCool$rotator);
            this.parCool$rotator = null;
        }
    }

    @Inject(method={"Lnet/minecraft/client/renderer/entity/player/PlayerRenderer;setupRotations(Lnet/minecraft/client/player/AbstractClientPlayer;Lcom/mojang/blaze3d/vertex/PoseStack;FFFF)V"}, at={@At(value="HEAD")}, cancellable=true)
    protected void onSetupRotationsHead(AbstractClientPlayer player, PoseStack poseStack, float bob, float yBodyRot, float partialTick, float scale, CallbackInfo ci) {
        Animation animation = Animation.get((Player)player);
        if (animation == null) {
            return;
        }
        this.parCool$rotator = new PlayerModelRotator(poseStack, (Player)player, partialTick, yBodyRot);
        if (animation.rotatePre(player, this.parCool$rotator)) {
            this.parCool$rotator = null;
            ci.cancel();
        }
    }
}

