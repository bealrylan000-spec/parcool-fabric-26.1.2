package com.alrex.parcool.client.animation;

import com.alrex.parcool.client.animation.PlayerModelRotator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.common.action.impl.ClingToCliff;
import com.alrex.parcool.common.action.impl.Dive;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.config.ParCoolConfig;
import com.alrex.parcool.utilities.EasingFunctions;
import com.alrex.parcool.utilities.MathUtil;
import com.alrex.parcool.utilities.VectorUtil;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class PassiveCustomAnimation {
    private int fallingAnimationTick = 0;
    private static final int FallingStartLine = 14;
    private int flyingAnimationLevelOld = 0;
    private int flyingAnimationLevel = 0;
    private static final int flyingMaxLevel = 20;

    public void tick(Player player, Parkourability parkourability) {
        this.flyingAnimationLevelOld = this.flyingAnimationLevel++;
        if (KeyBindings.isKeyForwardDown().booleanValue() && player.getAbilities().flying) {
            if (this.flyingAnimationLevel > 20) {
                this.flyingAnimationLevel = 20;
            }
        } else {
            --this.flyingAnimationLevel;
            if (this.flyingAnimationLevel < 0) {
                this.flyingAnimationLevel = 0;
            }
        }
        this.fallingAnimationTick = !(player.onGround() || !(player.fallDistance > 1.0f) || player.getAbilities().flying || player.isFallFlying() || parkourability.get(ClingToCliff.class).isDoing()) ? ++this.fallingAnimationTick : 0;
    }

    public void animate(Player player, Parkourability parkourability, PlayerModelTransformer transformer) {
        if (this.fallingAnimationTick >= 14 && ParCoolConfig.Client.Booleans.EnableFallingAnimation.get().booleanValue() && !parkourability.get(Dive.class).isDoing()) {
            this.animateFalling(parkourability, transformer);
        }
    }

    public void rotate(Player player, Parkourability parkourability, PlayerModelRotator rotator) {
    }

    private void animateFalling(Parkourability parkourability, PlayerModelTransformer transformer) {
        float phase = ((float)this.fallingAnimationTick + transformer.getPartialTick() - 14.0f) / 14.0f;
        float factor = phase > 1.0f ? 1.0f : EasingFunctions.SinInOutBySquare(phase);
        transformer.addRotateRightArm(0.0f, 0.0f, (float)Math.toRadians(80.0f * factor)).addRotateLeftArm(0.0f, 0.0f, (float)Math.toRadians(-80.0f * factor)).addRotateRightLeg(0.0f, 0.0f, (float)Math.toRadians(10.0f * factor)).addRotateLeftLeg(0.0f, 0.0f, (float)Math.toRadians(-10.0f * factor)).makeArmsMoveDynamically(factor).makeLegsShakingDynamically(factor).end();
    }

    private void animateCreativeFlying(Player player, PlayerModelTransformer transformer) {
        float angle = this.getAngleCreativeFlying(player, transformer.getPartialTick());
        float factor = this.getFactorCreativeFlying(transformer.getPartialTick());
        if (this.flyingAnimationLevel > 0) {
            transformer.rotateAdditionallyHeadPitch(-angle).rotateRightArm((float)Math.toRadians(-170.0f * factor), (float)Math.toRadians(90.0f * factor), 0.0f).rotateLeftArm((float)Math.toRadians(-170.0f * factor), (float)Math.toRadians(-90.0f * factor), 0.0f).makeArmsNatural().rotateLeftLeg(0.0f, 0.0f, 0.0f).rotateRightLeg(0.0f, 0.0f, 0.0f).makeLegsLittleMoving().end();
        }
    }

    private void rotateCreativeFlying(Player player, PlayerModelRotator rotator) {
        rotator.startBasedCenter().rotatePitchFrontward(this.getAngleCreativeFlying(player, rotator.getPartialTick())).end();
    }

    private void spawnSweatParticle(Player player) {
        Level level = player.level();
        RandomSource rand = player.getRandom();
        Vec3 particleSpeed = player.getDeltaMovement().scale(0.6);
        level.addParticle((ParticleOptions)ParticleTypes.DRIPPING_WATER, player.getX() + (double)(player.getBbWidth() * (rand.nextFloat() - 0.5f)), player.getY() + (double)(player.getBbHeight() * rand.nextFloat()), player.getZ() + (double)(player.getBbWidth() * (rand.nextFloat() - 0.5f)), particleSpeed.x(), particleSpeed.y() - 1.0, particleSpeed.z());
    }

    private float getAngleCreativeFlying(Player player, float partial) {
        float xRot = (float)VectorUtil.toPitchDegree(player.getDeltaMovement());
        return (xRot + 90.0f) * this.getFactorCreativeFlying(partial);
    }

    private float getFactorCreativeFlying(float partial) {
        return EasingFunctions.SinInOutBySquare(MathUtil.lerp(this.flyingAnimationLevelOld, this.flyingAnimationLevel, partial) / 20.0f);
    }
}

