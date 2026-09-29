package com.alrex.parcool.client.animation.impl;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.PlayerModelRotator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.common.action.impl.RideZipline;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.utilities.VectorUtil;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import com.alrex.parcool.platform.event.client.RenderFrameEvent;
import org.joml.Vector3f;

public class RideZiplineAnimator
extends Animator {
    private double oldAngleRadian = 0.0;
    private double currentAngleRadian = 0.0;

    @Override
    public void tick(Player player) {
        super.tick(player);
        Parkourability parkourability = Parkourability.get(player);
        if (parkourability == null) {
            return;
        }
        RideZipline action = parkourability.get(RideZipline.class);
        if (!action.isDoing()) {
            return;
        }
        this.oldAngleRadian = this.currentAngleRadian;
        double acceleration = action.getAcceleration();
        double slope = action.getSlope();
        double gravity = player.getAttributeValue(Attributes.GRAVITY);
        double invSqrt = Mth.invSqrt((double)(slope * slope + 1.0));
        double xz = -acceleration * invSqrt;
        double y = gravity + acceleration * slope * invSqrt;
        this.currentAngleRadian = Mth.lerp((double)0.1, (double)this.oldAngleRadian, (double)Math.atan2(xz, y));
    }

    @Override
    public boolean shouldRemoved(Player player, Parkourability parkourability) {
        return !parkourability.get(RideZipline.class).isDoing();
    }

    @Override
    public void animatePost(Player player, Parkourability parkourability, PlayerModelTransformer transformer) {
        Vec3 offset = parkourability.get(RideZipline.class).getEndOffsetFromStart();
        if (offset == null) {
            return;
        }
        double angleDifference = VectorUtil.toYawRadian(player.getLookAngle()) - VectorUtil.toYawRadian(new Vec3(offset.x(), 0.0, offset.z()));
        double angleCos = Math.cos(angleDifference);
        double angleSin = Math.sin(angleDifference);
        double angleCosAbs = Math.abs(angleCos);
        transformer.translateRightArm(0.6f, -2.2f, 0.0f).translateLeftArm(-0.6f, -2.2f, 0.0f).rotateRightArm((float)(-Math.PI), 0.0f, (float)Math.toRadians(Mth.lerp((double)angleCosAbs, (double)10.0, (double)15.0))).rotateLeftArm((float)(-Math.PI), 0.0f, (float)Math.toRadians(Mth.lerp((double)angleCosAbs, (double)-10.0, (double)-15.0))).rotateRightLeg((float)(-this.currentAngleRadian * angleCos), 0.0f, (float)(-this.currentAngleRadian * angleSin)).rotateLeftLeg((float)(-this.currentAngleRadian * angleCos), 0.0f, (float)(-this.currentAngleRadian * angleSin)).makeLegsLittleMoving().end();
    }

    @Override
    public void onRenderTick(RenderFrameEvent event, Player player, Parkourability parkourability) {
        player.yBodyRot = player.getYRot();
        player.yBodyRotO = player.yRotO;
    }

    @Override
    public void rotatePost(Player player, Parkourability parkourability, PlayerModelRotator rotator) {
        Vec3 offset = parkourability.get(RideZipline.class).getEndOffsetFromStart();
        if (offset == null) {
            return;
        }
        Vec3 rotationAxis = new Vec3(0.0, 0.0, 1.0).yRot((float)(1.5707963267948966 + VectorUtil.toYawRadian(player.getLookAngle()) - VectorUtil.toYawRadian(new Vec3(offset.x(), 0.0, offset.z())))).normalize();
        double angle = Mth.lerp((double)rotator.getPartialTick(), (double)this.oldAngleRadian, (double)this.currentAngleRadian);
        rotator.startBasedTop().rotate((float)angle, new Vector3f((float)rotationAxis.x(), 0.0f, (float)rotationAxis.z())).end();
    }
}

