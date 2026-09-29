package com.alrex.parcool.utilities;

import com.alrex.parcool.utilities.EasingFunctions;
import com.alrex.parcool.utilities.MathUtil;

public class Easing {
    float phase;
    float result;
    boolean calculated = false;

    public Easing(float phase) {
        this.phase = phase;
    }

    public static Easing with(float phase) {
        return new Easing(phase);
    }

    public boolean isInRange(float start, float until) {
        return start <= this.phase && this.phase < until;
    }

    public Easing sinInOut(float start, float until, float from, float to) {
        if (this.calculated) {
            return this;
        }
        if (!this.isInRange(start, until)) {
            return this;
        }
        float offset = this.phase - start;
        this.result = MathUtil.lerp(from, to, EasingFunctions.SinInOutBySquare(offset / (until - start)));
        this.calculated = true;
        return this;
    }

    public Easing cubicInOut(float start, float until, float from, float to) {
        if (this.calculated) {
            return this;
        }
        if (!this.isInRange(start, until)) {
            return this;
        }
        float offset = this.phase - start;
        this.result = MathUtil.lerp(from, to, EasingFunctions.CubicInOut(offset / (until - start)));
        this.calculated = true;
        return this;
    }

    public Easing squareIn(float start, float until, float from, float to) {
        if (this.calculated) {
            return this;
        }
        if (!this.isInRange(start, until)) {
            return this;
        }
        float offset = this.phase - start;
        float inPhase = offset / (until - start);
        this.result = MathUtil.lerp(from, to, inPhase * inPhase);
        this.calculated = true;
        return this;
    }

    public Easing squareOut(float start, float until, float from, float to) {
        if (this.calculated) {
            return this;
        }
        if (!this.isInRange(start, until)) {
            return this;
        }
        float offset = this.phase - start;
        float inPhase = 1.0f - offset / (until - start);
        this.result = MathUtil.lerp(from, to, 1.0f - inPhase * inPhase);
        this.calculated = true;
        return this;
    }

    public Easing linear(float start, float until, float from, float to) {
        if (this.calculated) {
            return this;
        }
        if (!this.isInRange(start, until)) {
            return this;
        }
        float offset = this.phase - start;
        float inPhase = offset / (until - start);
        this.result = MathUtil.lerp(from, to, inPhase);
        this.calculated = true;
        return this;
    }

    public Easing noChange(float start, float until, float value) {
        if (this.calculated) {
            return this;
        }
        if (!this.isInRange(start, until)) {
            return this;
        }
        this.result = value;
        this.calculated = true;
        return this;
    }

    public float get() {
        if (this.calculated) {
            return this.result;
        }
        return this.phase;
    }
}

