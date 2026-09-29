package com.alrex.parcool.api.unstable.animation;

import com.alrex.parcool.api.unstable.animation.AnimationPart;
import java.util.EnumMap;
import com.alrex.parcool.platform.dist.Dist;
import com.alrex.parcool.platform.dist.OnlyIn;

@OnlyIn(value=Dist.CLIENT)
public class AnimationOption {
    private final EnumMap<AnimationPart, Boolean> animationMap = new EnumMap(AnimationPart.class);
    private boolean animationCanceled = false;

    public AnimationOption() {
        for (AnimationPart part : AnimationPart.values()) {
            this.animationMap.put(part, true);
        }
    }

    public void cancel(AnimationPart part) {
        this.animationMap.put(part, false);
    }

    public void cancelAnimation() {
        for (AnimationPart part : AnimationPart.values()) {
            this.animationMap.put(part, false);
        }
        this.animationCanceled = true;
    }

    public boolean isCanceled(AnimationPart part) {
        if (this.animationCanceled) {
            return true;
        }
        Boolean value = this.animationMap.get((Object)part);
        return Boolean.FALSE.equals(value);
    }

    public boolean isAnimationCanceled() {
        return this.animationCanceled;
    }
}

