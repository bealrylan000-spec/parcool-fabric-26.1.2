package com.alrex.parcool.common.attachment.client;

import com.alrex.parcool.platform.attachment.AttachmentHolder;

import com.alrex.parcool.api.unstable.animation.AnimationOption;
import com.alrex.parcool.api.unstable.animation.AnimationPart;
import com.alrex.parcool.api.unstable.animation.ParCoolAnimationInfoEvent;
import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.PassiveCustomAnimation;
import com.alrex.parcool.client.animation.PlayerModelRotator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.attachment.ClientAttachments;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.config.ParCoolConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import com.alrex.parcool.platform.dist.Dist;
import com.alrex.parcool.platform.dist.OnlyIn;
import com.alrex.parcool.platform.eventbus.Event;
import com.alrex.parcool.platform.event.client.RenderFrameEvent;
import com.alrex.parcool.platform.event.client.ViewportEvent;
import com.alrex.parcool.platform.eventbus.NeoForge;

@OnlyIn(value=Dist.CLIENT)
public class Animation {
    private Animator animator = null;
    private AnimationOption option = new AnimationOption();
    private final PassiveCustomAnimation passiveAnimation = new PassiveCustomAnimation();

    public static Animation get(Player player) {
        return (Animation)AttachmentHolder.getData(player, ClientAttachments.ANIMATION);
    }

    public void setAnimator(Animator animator) {
        if (!ParCoolConfig.Client.Booleans.EnableAnimation.get().booleanValue()) {
            return;
        }
        if (!((Boolean)ParCoolConfig.Client.getInstance().canAnimate(animator.getClass()).get()).booleanValue()) {
            return;
        }
        this.animator = animator;
    }

    public boolean animatePre(Player player, PlayerModelTransformer modelTransformer) {
        Parkourability parkourability = Parkourability.get(player);
        if (this.animator != null && this.animator.shouldRemoved(player, parkourability)) {
            this.animator = null;
        }
        if (this.animator == null) {
            return false;
        }
        modelTransformer.setOption(this.option);
        if (this.shouldCancelAnimation(player)) {
            return false;
        }
        return this.animator.animatePre(player, parkourability, modelTransformer);
    }

    public void animatePost(Player player, PlayerModelTransformer modelTransformer) {
        Parkourability parkourability = Parkourability.get(player);
        if (this.shouldCancelAnimation(player)) {
            return;
        }
        if (this.animator == null) {
            this.passiveAnimation.animate(player, parkourability, modelTransformer);
            return;
        }
        this.animator.animatePost(player, parkourability, modelTransformer);
    }

    public boolean rotatePre(AbstractClientPlayer player, PlayerModelRotator rotator) {
        Parkourability parkourability = Parkourability.get((Player)player);
        if (this.animator != null && this.animator.shouldRemoved((Player)player, parkourability)) {
            this.animator = null;
        }
        if (this.animator == null) {
            return false;
        }
        if (this.shouldCancelAnimation((Player)player) || this.option.isCanceled(AnimationPart.ROTATION)) {
            return false;
        }
        return this.animator.rotatePre((Player)player, parkourability, rotator);
    }

    public void rotatePost(AbstractClientPlayer player, PlayerModelRotator rotator) {
        Parkourability parkourability = Parkourability.get((Player)player);
        if (this.shouldCancelAnimation((Player)player) || this.option.isCanceled(AnimationPart.ROTATION)) {
            return;
        }
        if (this.animator == null) {
            this.passiveAnimation.rotate((Player)player, parkourability, rotator);
            return;
        }
        this.animator.rotatePost((Player)player, parkourability, rotator);
    }

    public void cameraSetup(ViewportEvent.ComputeCameraAngles event, LocalPlayer player, Parkourability parkourability) {
        if (this.animator == null) {
            return;
        }
        if (this.option.isCanceled(AnimationPart.CAMERA)) {
            return;
        }
        if (this.animator.shouldRemoved((Player)player, parkourability)) {
            this.animator = null;
            return;
        }
        this.animator.onCameraSetUp(event, (Player)player, parkourability);
    }

    public void tick(AbstractClientPlayer player, Parkourability parkourability) {
        this.passiveAnimation.tick((Player)player, parkourability);
        if (this.animator != null) {
            this.animator.tick((Player)player);
        }
    }

    public void onRenderTick(RenderFrameEvent event, Player player, Parkourability parkourability) {
        if (this.animator != null) {
            this.animator.onRenderTick(event, player, parkourability);
        }
        if (event instanceof RenderFrameEvent.Pre) {
            this.updateAnimationInfo((AbstractClientPlayer)player);
        }
    }

    public void updateAnimationInfo(AbstractClientPlayer player) {
        ParCoolAnimationInfoEvent animationEvent = new ParCoolAnimationInfoEvent(player, this.animator);
        NeoForge.EVENT_BUS.post((Event)animationEvent);
        this.option = animationEvent.getOption();
    }

    public boolean shouldCancelAnimation(Player player) {
        if (player.isLocalPlayer() && Minecraft.getInstance().options.getCameraType().isFirstPerson() && !ParCoolConfig.Client.Booleans.EnableFPVAnimation.get().booleanValue()) {
            return true;
        }
        return this.option.isAnimationCanceled();
    }

    public boolean hasAnimator() {
        return this.animator != null;
    }

    public void removeAnimator() {
        this.animator = null;
    }
}

