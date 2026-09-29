package com.alrex.parcool.utilities;

public class EasingFunctions {
    public static float CubicInOut(float phase) {
        if ((double)phase < 0.5) {
            return 4.0f * phase * phase * phase;
        }
        phase = 1.0f - phase;
        return 1.0f - 4.0f * phase * phase * phase;
    }

    public static float SinInOutBySquare(float phase) {
        if ((double)phase < 0.5) {
            return 2.0f * phase * phase;
        }
        return 1.0f - 2.0f * (phase - 1.0f) * (phase - 1.0f);
    }
}

