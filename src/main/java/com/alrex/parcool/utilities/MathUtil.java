package com.alrex.parcool.utilities;

public class MathUtil {
    public static float squaring(float value) {
        return value * value;
    }

    public static float lerp(float start, float end, float factor) {
        return start + (end - start) * factor;
    }

    public static float normalizeRadian(float angle) {
        return (float)((double)angle - Math.PI * 2 * Math.floor(((double)angle + Math.PI) / (Math.PI * 2)));
    }

    public static float normalizeDegree(float angle) {
        return (float)((double)angle - 360.0 * Math.floor((angle + 180.0f) / 360.0f));
    }
}

