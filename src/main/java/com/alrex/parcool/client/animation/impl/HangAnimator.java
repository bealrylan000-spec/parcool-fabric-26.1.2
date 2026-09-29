package com.alrex.parcool.client.animation.impl;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.PlayerModelRotator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.action.impl.HangDown;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.config.ParCoolConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import com.alrex.parcool.platform.event.client.ViewportEvent;

public class HangAnimator
extends Animator {
    @Override
    public boolean shouldRemoved(Player player, Parkourability parkourability) {
        return !parkourability.get(HangDown.class).isDoing();
    }

    @Override
    public void animatePost(Player player, Parkourability parkourability, PlayerModelTransformer transformer) {
        HangDown hangDown = parkourability.get(HangDown.class);
        HangDown.BarAxis axis = hangDown.getHangingBarAxis();
        if (axis == null) {
            return;
        }
        boolean orthogonal = hangDown.isOrthogonalToBar();
        transformer.translateRightArm(0.3f, -2.0f, 0.0f).translateLeftArm(-0.3f, -2.0f, 0.0f);
        if (orthogonal) {
            float zAngle = (float)Math.toRadians(10.0 + 20.0 * Math.sin(24.0f * hangDown.getArmSwingAmount()));
            transformer.rotateRightArm((float)Math.PI, 0.0f, -zAngle).rotateLeftArm((float)Math.PI, 0.0f, zAngle);
        } else {
            float xAngle = (float)Math.toRadians(180.0 + 25.0 * Math.sin(24.0f * hangDown.getArmSwingAmount()));
            transformer.rotateRightArm(xAngle, 0.0f, (float)Math.toRadians(15.0)).rotateLeftArm(-xAngle, 0.0f, -((float)Math.toRadians(15.0)));
        }
        transformer.rotateRightLeg(0.0f, 0.0f, 0.0f).rotateLeftLeg(0.0f, 0.0f, 0.0f).makeLegsLittleMoving().end();
    }

    private float getRotateAngle(HangDown hangDown, double partialTick) {
        return (float)(-hangDown.getBodySwingAngleFactor() * 40.0 * Math.sin(((double)hangDown.getDoingTick() + partialTick) / 10.0 * Math.PI));
    }

    @Override
    public void rotatePost(Player player, Parkourability parkourability, PlayerModelRotator rotator) {
        HangDown hangDown = parkourability.get(HangDown.class);
        rotator.startBasedTop().rotatePitchFrontward(this.getRotateAngle(hangDown, rotator.getPartialTick())).end();
    }

    @Override
    public void onCameraSetUp(ViewportEvent.ComputeCameraAngles event, Player clientPlayer, Parkourability parkourability) {
        if (!(clientPlayer.isLocalPlayer() && Minecraft.getInstance().options.getCameraType().isFirstPerson() && ParCoolConfig.Client.Booleans.EnableCameraAnimationOfHangDown.get().booleanValue())) {
            return;
        }
        HangDown hangDown = parkourability.get(HangDown.class);
        event.setPitch(clientPlayer.getViewXRot((float)event.getPartialTick()) + this.getRotateAngle(hangDown, event.getPartialTick()));
    }
}

