package com.alrex.parcool.client.animation;

import com.alrex.parcool.client.animation.Animator;
import com.alrex.parcool.client.animation.impl.BackwardWallJumpAnimator;
import com.alrex.parcool.client.animation.impl.CatLeapAnimator;
import com.alrex.parcool.client.animation.impl.ChargeJumpAnimator;
import com.alrex.parcool.client.animation.impl.ClimbUpAnimator;
import com.alrex.parcool.client.animation.impl.ClingToCliffAnimator;
import com.alrex.parcool.client.animation.impl.CrawlAnimator;
import com.alrex.parcool.client.animation.impl.DiveAnimationHostAnimator;
import com.alrex.parcool.client.animation.impl.DiveIntoWaterAnimator;
import com.alrex.parcool.client.animation.impl.DodgeAnimator;
import com.alrex.parcool.client.animation.impl.FastRunningAnimator;
import com.alrex.parcool.client.animation.impl.FastSwimAnimator;
import com.alrex.parcool.client.animation.impl.FlippingAnimator;
import com.alrex.parcool.client.animation.impl.HangAnimator;
import com.alrex.parcool.client.animation.impl.HideInBlockAnimator;
import com.alrex.parcool.client.animation.impl.HorizontalWallRunAnimator;
import com.alrex.parcool.client.animation.impl.JumpChargingAnimator;
import com.alrex.parcool.client.animation.impl.JumpFromBarAnimator;
import com.alrex.parcool.client.animation.impl.KongVaultAnimator;
import com.alrex.parcool.client.animation.impl.RideZiplineAnimator;
import com.alrex.parcool.client.animation.impl.RollAnimator;
import com.alrex.parcool.client.animation.impl.SlidingAnimator;
import com.alrex.parcool.client.animation.impl.SpeedVaultAnimator;
import com.alrex.parcool.client.animation.impl.TapAnimator;
import com.alrex.parcool.client.animation.impl.VerticalWallRunAnimator;
import com.alrex.parcool.client.animation.impl.WallJumpAnimator;
import com.alrex.parcool.client.animation.impl.WallSlideAnimator;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class AnimatorList {
    public static final List<Class<? extends Animator>> ANIMATORS = Arrays.asList(BackwardWallJumpAnimator.class, CatLeapAnimator.class, ChargeJumpAnimator.class, ClimbUpAnimator.class, ClingToCliffAnimator.class, CrawlAnimator.class, DiveAnimationHostAnimator.class, DiveIntoWaterAnimator.class, DodgeAnimator.class, FastRunningAnimator.class, FastSwimAnimator.class, FlippingAnimator.class, HideInBlockAnimator.class, HorizontalWallRunAnimator.class, JumpChargingAnimator.class, JumpFromBarAnimator.class, HangAnimator.class, KongVaultAnimator.class, RideZiplineAnimator.class, RollAnimator.class, SlidingAnimator.class, SpeedVaultAnimator.class, TapAnimator.class, VerticalWallRunAnimator.class, WallJumpAnimator.class, WallSlideAnimator.class);
    private static final HashMap<Class<? extends Animator>, Short> INDEX_MAP = new HashMap((int)((double)ANIMATORS.size() * 1.5));

    public static short getIndex(Class<? extends Animator> animator) {
        return INDEX_MAP.getOrDefault(animator, (short)-1);
    }

    static {
        for (Class<? extends Animator> animator : ANIMATORS) {
            INDEX_MAP.put(animator, (short)ANIMATORS.indexOf(animator));
        }
    }
}

