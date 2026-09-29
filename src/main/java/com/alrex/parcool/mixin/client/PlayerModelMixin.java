package com.alrex.parcool.mixin.client;

import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.attachment.client.Animation;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={PlayerModel.class})
public abstract class PlayerModelMixin<T extends LivingEntity>
extends HumanoidModel<T> {
    @Shadow
    @Final
    private boolean slim;
    @Shadow
    @Final
    public ModelPart jacket;
    @Shadow
    @Final
    public ModelPart rightPants;
    @Shadow
    @Final
    public ModelPart rightSleeve;
    @Shadow
    @Final
    public ModelPart leftPants;
    @Shadow
    @Final
    public ModelPart leftSleeve;
    @Shadow
    @Final
    private ModelPart ear;
    @Unique
    private PlayerModelTransformer parCool$transformer = null;

    public PlayerModelMixin(ModelPart p_i1148_1_) {
        super(p_i1148_1_);
    }

    @Inject(method={"Lnet/minecraft/client/model/PlayerModel;setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V"}, at={@At(value="HEAD")}, cancellable=true)
    protected void onSetupAnimHead(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo info) {
        if (!(entity instanceof Player)) {
            return;
        }
        Player player = (Player)entity;
        PlayerModel model = (PlayerModel)this;
        this.parCool$transformer = new PlayerModelTransformer(player, model, this.slim, ageInTicks, limbSwing, limbSwingAmount, netHeadYaw, headPitch);
        this.parCool$transformer.reset();
        Animation animation = Animation.get(player);
        if (animation == null) {
            return;
        }
        boolean shouldCancel = animation.animatePre(player, this.parCool$transformer);
        this.parCool$transformer.copyFromBodyToWear();
        if (shouldCancel) {
            this.parCool$transformer = null;
            info.cancel();
        }
    }

    @Inject(method={"Lnet/minecraft/client/model/PlayerModel;setupAnim(Lnet/minecraft/world/entity/LivingEntity;FFFFF)V"}, at={@At(value="TAIL")})
    protected void onSetupAnimTail(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo info) {
        if (!(entity instanceof Player)) {
            return;
        }
        Player player = (Player)entity;
        Animation animation = Animation.get(player);
        if (animation == null) {
            this.parCool$transformer = null;
            return;
        }
        if (this.parCool$transformer != null) {
            animation.animatePost(player, this.parCool$transformer);
            this.parCool$transformer.copyFromBodyToWear();
            this.parCool$transformer = null;
        }
    }
}

