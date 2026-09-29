package com.alrex.parcool.utilities;

public class ColorUtil {
    public static int getColorCodeFromARGB(int a, int r, int g, int b) {
        return a * 0x1000000 + r * 65536 + g * 256 + b;
    }

    public static int multiple(int value, double scale) {
        int a = (value & 0xFF000000) >> 24 & 0xFF;
        int r = (value & 0xFF0000) >> 16;
        int g = (value & 0xFF00) >> 8;
        int b = value & 0xFF;
        r = (int)Math.min(255.0, (double)r * scale);
        g = (int)Math.min(255.0, (double)g * scale);
        b = (int)Math.min(255.0, (double)b * scale);
        return ColorUtil.getColorCodeFromARGB(a, r, g, b);
    }
}

