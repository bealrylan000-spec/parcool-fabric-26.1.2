package com.alrex.parcool.client.animation.impl;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.PlayerModelRotator;
import com.alrex.parcool.client.animation.PlayerModelTransformer;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.common.action.impl.Dive;
import com.alrex.parcool.common.action.impl.SkyDive;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.utilities.Easing;
import com.alrex.parcool.utilities.EasingFunctions;
import com.alrex.parcool.utilities.MathUtil;
import javax.annotation.Nullable;
import net.minecraft.world.entity.player.Player;

public class DiveAnimationHostAnimator
extends Animator {
    final DiveAnimator diveAnimator;
    @Nullable
    SkyDiveAnimator skyDiveAnimator = null;
    final boolean fromInAir;
    static final int MaxTransitionStartedInAirTick = 10;
    static final int MaxTransitionTick = 6;
    boolean oldSkyDiveDoing = false;
    private boolean transitioning = false;
    private int transitionTick = 0;

    public DiveAnimationHostAnimator(double ySpeed, boolean fromInAir) {
        this.diveAnimator = new DiveAnimator(ySpeed);
        this.fromInAir = fromInAir;
    }

    @Override
    public void tick(Player player) {
        super.tick(player);
        this.diveAnimator.tick(player);
        if (this.skyDiveAnimator != null) {
            this.skyDiveAnimator.tick(player);
        }
        if (this.transitioning) {
            ++this.transitionTick;
            if (this.transitionTick >= 6) {
                this.transitionTick = 0;
                this.transitioning = false;
            }
        }
    }

    @Override
    public boolean shouldRemoved(Player player, Parkourability parkourability) {
        return !parkourability.get(Dive.class).isDoing();
    }

    void checkTransition(Parkourability parkourability) {
        boolean doing = parkourability.get(SkyDive.class).isDoing();
        if (doing != this.oldSkyDiveDoing) {
            this.startTransition();
        }
        this.oldSkyDiveDoing = doing;
    }

    @Override
    public void animatePost(Player player, Parkourability parkourability, PlayerModelTransformer transformer) {
        if (this.fromInAir && this.getTick() < 10) {
            float phase = ((float)this.getTick() + transformer.getPartialTick()) / 10.0f;
            this.diveAnimator.animatePost(player, parkourability, transformer, new Easing(phase).squareOut(0.0f, 1.0f, 0.0f, 1.0f).get());
            float legAngle = -45.0f * (float)Math.toRadians(new Easing(phase).squareOut(0.0f, 0.4f, 0.0f, 1.0f).sinInOut(0.4f, 1.0f, 1.0f, 0.0f).get());
            transformer.addRotateLeftLeg(legAngle, 0.0f, 0.0f).addRotateRightLeg(legAngle, 0.0f, 0.0f).end();
        } else {
            this.checkTransition(parkourability);
            if (parkourability.get(SkyDive.class).isDoing()) {
                if (this.skyDiveAnimator == null) {
                    this.skyDiveAnimator = new SkyDiveAnimator(this.diveAnimator.getPitchAngle());
                }
                float factor = this.getTransitionFactor(transformer.getPartialTick());
                if (this.transitioning) {
                    this.diveAnimator.animatePost(player, parkourability, transformer);
                }
                this.skyDiveAnimator.animatePost(player, parkourability, transformer, factor);
            } else if (this.transitioning && this.skyDiveAnimator != null) {
                float factor = this.getTransitionFactor(transformer.getPartialTick());
                this.skyDiveAnimator.animatePost(player, parkourability, transformer);
                this.diveAnimator.animatePost(player, parkourability, transformer, factor);
            } else {
                this.skyDiveAnimator = null;
                this.diveAnimator.animatePost(player, parkourability, transformer);
            }
        }
    }

    @Override
    public void rotatePost(Player player, Parkourability parkourability, PlayerModelRotator rotator) {
        if (this.fromInAir && this.getTick() < 10) {
            float factor = new Easing(((float)this.getTick() + rotator.getPartialTick()) / 10.0f).squareOut(0.0f, 1.0f, 0.0f, 1.0f).get();
            this.diveAnimator.rotate(player, parkourability, rotator, factor, 0.0f);
        } else {
            this.checkTransition(parkourability);
            if (parkourability.get(SkyDive.class).isDoing()) {
                if (this.skyDiveAnimator == null) {
                    this.skyDiveAnimator = new SkyDiveAnimator(this.diveAnimator.getPitchAngle());
                }
                this.skyDiveAnimator.rotatePost(player, parkourability, rotator);
            } else if (this.transitioning && this.skyDiveAnimator != null) {
                float factor = this.getTransitionFactor(rotator.getPartialTick());
                this.diveAnimator.rotate(player, parkourability, rotator, factor, this.skyDiveAnimator.getPitchAngle());
            } else {
                this.skyDiveAnimator = null;
                this.diveAnimator.rotatePost(player, parkourability, rotator);
            }
        }
    }

    private void startTransition() {
        this.transitioning = true;
        this.transitionTick = 0;
    }

    private float getTransitionFactor(float partialTick) {
        float factor = this.transitioning ? ((float)this.transitionTick + partialTick) / 6.0f : 1.0f;
        return factor;
    }

    public static class SkyDiveAnimator
    extends Animator {
        private int forwardAngleCount = 0;
        private int forwardAngleCountOld = 0;
        private int rightAngleCount = 0;
        private int rightAngleCountOld = 0;
        private final int maxCount = 8;
        private final float startPitchAngle;
        private float pitchAngle;

        public SkyDiveAnimator(float startPitchAngleDegree) {
            this.startPitchAngle = startPitchAngleDegree;
        }

        @Override
        public boolean shouldRemoved(Player player, Parkourability parkourability) {
            return false;
        }

        @Override
        public void tick(Player player) {
            super.tick(player);
            this.forwardAngleCountOld = this.forwardAngleCount;
            this.rightAngleCountOld = this.rightAngleCount;
            if (KeyBindings.isKeyForwardDown().booleanValue()) {
                if (KeyBindings.isKeyBackDown().booleanValue()) {
                    if (this.forwardAngleCount > 0) {
                        --this.forwardAngleCount;
                    }
                    if (this.forwardAngleCount < 0) {
                        ++this.forwardAngleCount;
                    }
                } else if (this.forwardAngleCount < 8) {
                    ++this.forwardAngleCount;
                }
            } else if (KeyBindings.isKeyBackDown().booleanValue()) {
                if (this.forwardAngleCount > -8) {
                    --this.forwardAngleCount;
                }
            } else {
                if (this.forwardAngleCount > 0) {
                    --this.forwardAngleCount;
                }
                if (this.forwardAngleCount < 0) {
                    ++this.forwardAngleCount;
                }
            }
            if (KeyBindings.isKeyRightDown().booleanValue()) {
                if (KeyBindings.isKeyLeftDown().booleanValue()) {
                    if (this.rightAngleCount > 0) {
                        --this.rightAngleCount;
                    }
                    if (this.rightAngleCount < 0) {
                        ++this.rightAngleCount;
                    }
                } else if (this.rightAngleCount < 8) {
                    ++this.rightAngleCount;
                }
            } else if (KeyBindings.isKeyLeftDown().booleanValue()) {
                if (this.rightAngleCount > -8) {
                    --this.rightAngleCount;
                }
            } else {
                if (this.rightAngleCount > 0) {
                    --this.rightAngleCount;
                }
                if (this.rightAngleCount < 0) {
                    ++this.rightAngleCount;
                }
            }
        }

        @Override
        public void animatePost(Player player, Parkourability parkourability, PlayerModelTransformer transformer) {
            this.animatePost(player, parkourability, transformer, 1.0f);
        }

        public void animatePost(Player player, Parkourability parkourability, PlayerModelTransformer transformer, float factor) {
            float forwardAngleFactor = this.getForwardAngleFactor(transformer.getPartialTick());
            float rightAngleFactor = this.getRightAngleFactor(transformer.getPartialTick());
            transformer.rotateHeadPitch(-20.0f).rotateRightArm((float)Math.toRadians(20.0f - 10.0f * rightAngleFactor), 0.0f, (float)Math.toRadians(70.0f - 30.0f * forwardAngleFactor), factor).rotateLeftArm((float)Math.toRadians(20.0f + 10.0f * rightAngleFactor), 0.0f, (float)Math.toRadians(-(70.0f - 30.0f * forwardAngleFactor)), factor).rotateRightLeg((float)Math.toRadians(20.0f - 10.0f * rightAngleFactor), 0.0f, (float)Math.toRadians(25.0f - 10.0f * forwardAngleFactor), factor).rotateLeftLeg((float)Math.toRadians(20.0f + 10.0f * rightAngleFactor), 0.0f, (float)Math.toRadians(-(25.0f - 10.0f * forwardAngleFactor)), factor).makeArmsMoveDynamically(0.06f).makeLegsMoveDynamically(0.06f).end();
        }

        private float getForwardAngleFactor(float partial) {
            float phase = this.forwardAngleCount > 0 ? MathUtil.lerp(this.forwardAngleCountOld, this.forwardAngleCount, partial) / 8.0f : (this.forwardAngleCount < 0 ? MathUtil.lerp(this.forwardAngleCountOld, this.forwardAngleCount, partial) / 8.0f : 0.0f);
            if (phase > 1.0f) {
                phase = 1.0f;
            }
            if (phase < -1.0f) {
                phase = -1.0f;
            }
            if (phase > 0.0f) {
                return EasingFunctions.CubicInOut(phase);
            }
            if (phase < 0.0f) {
                return -EasingFunctions.CubicInOut(-phase);
            }
            return 0.0f;
        }

        private float getRightAngleFactor(float partial) {
            float phase = this.rightAngleCount > 0 ? MathUtil.lerp(this.rightAngleCountOld, this.rightAngleCount, partial) / 8.0f : (this.rightAngleCount < 0 ? MathUtil.lerp(this.rightAngleCountOld, this.rightAngleCount, partial) / 8.0f : 0.0f);
            if (phase > 1.0f) {
                phase = 1.0f;
            }
            if (phase < -1.0f) {
                phase = -1.0f;
            }
            if (phase > 0.0f) {
                return 1.0f - (1.0f - phase) * (1.0f - phase);
            }
            if (phase < 0.0f) {
                phase = -phase;
                return -1.0f + (1.0f - phase) * (1.0f - phase);
            }
            return 0.0f;
        }

        @Override
        public void rotatePost(Player player, Parkourability parkourability, PlayerModelRotator rotator) {
            float forwardAngleFactor = this.getForwardAngleFactor(rotator.getPartialTick());
            float rightAngleFactor = this.getRightAngleFactor(rotator.getPartialTick());
            float basePitchAngle = this.getTick() >= 6 ? 90.0f : MathUtil.lerp(this.startPitchAngle, 90.0f, ((float)this.getTick() + rotator.getPartialTick()) / 6.0f);
            this.pitchAngle = basePitchAngle + 24.0f * forwardAngleFactor;
            rotator.startBasedCenter().rotatePitchFrontward(this.pitchAngle).rotateYawRightward(-24.0f * rightAngleFactor).end();
        }

        float getPitchAngle() {
            return this.pitchAngle;
        }
    }

    public static class DiveAnimator
    extends Animator {
        private final double initialYSpeed;
        private float pitchAngle = 0.0f;
        private float oldFactor = 0.0f;

        public DiveAnimator(double startYSpeed) {
            this.initialYSpeed = startYSpeed;
        }

        private float getFactor(double yMovement) {
            return (float)Math.max(0.0, 2.0 / (1.0 + Math.exp(yMovement / this.initialYSpeed - 1.0)) - 0.9621);
        }

        @Override
        public boolean shouldRemoved(Player player, Parkourability parkourability) {
            return false;
        }

        @Override
        public void animatePost(Player player, Parkourability parkourability, PlayerModelTransformer transformer) {
            this.animatePost(player, parkourability, transformer, 1.0f);
        }

        void animatePost(Player player, Parkourability parkourability, PlayerModelTransformer transformer, float factor) {
            double ySpeed = parkourability.get(Dive.class).getPlayerYSpeed(transformer.getPartialTick());
            float bodyFactor = this.getFactor(ySpeed);
            transformer.rotateHeadPitch(-50.0f * bodyFactor).rotateRightArm(0.0f, 0.0f, (float)Math.toRadians(195.0f * bodyFactor), factor).rotateLeftArm(0.0f, 0.0f, (float)Math.toRadians(-195.0f * bodyFactor), factor).rotateRightLeg((float)Math.toRadians(-180.0f * (bodyFactor - this.oldFactor)), 0.0f, 0.0f, factor).rotateLeftLeg((float)Math.toRadians(-180.0f * (bodyFactor - this.oldFactor)), 0.0f, 0.0f, factor).end();
            this.oldFactor = bodyFactor;
        }

        @Override
        public void rotatePost(Player player, Parkourability parkourability, PlayerModelRotator rotator) {
            this.rotate(player, parkourability, rotator, 1.0f, 0.0f);
        }

        public void rotate(Player player, Parkourability parkourability, PlayerModelRotator rotator, float factor, float transitionBaseAngle) {
            double ySpeed = parkourability.get(Dive.class).getPlayerYSpeed(rotator.getPartialTick());
            float angleFactor = this.getFactor(ySpeed);
            this.pitchAngle = 180.0f * angleFactor;
            rotator.startBasedCenter().rotatePitchFrontward(MathUtil.lerp(transitionBaseAngle, this.pitchAngle, factor)).end();
        }

        private float getPitchAngle() {
            return this.pitchAngle;
        }
    }
}

