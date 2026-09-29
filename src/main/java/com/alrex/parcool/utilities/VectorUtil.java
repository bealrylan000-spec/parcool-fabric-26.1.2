package com.alrex.parcool.utilities;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class VectorUtil {
    public static double toYawDegree(Vec3 vec) {
        return Math.atan2(vec.z, vec.x) * 180.0 / Math.PI - 90.0;
    }

    public static double toYawRadian(Vec3 vec) {
        return Math.atan2(vec.z(), vec.x()) - 1.5707963267948966;
    }

    public static double toPitchDegree(Vec3 vec) {
        return -(Math.atan2(vec.y(), Math.sqrt(vec.x() * vec.x() + vec.z() * vec.z())) * 180.0 / Math.PI);
    }

    public static Vec3 fromYawDegree(double degree) {
        return new Vec3(-Math.sin(Math.toRadians(degree)), 0.0, Math.cos(Math.toRadians(degree)));
    }

    public static Vec3 rotateYDegrees(Vec3 vector, float baseAngle) {
        float angle = baseAngle * ((float)Math.PI / 180);
        return new Vec3(vector.x * (double)Mth.cos((float)angle) - vector.z * (double)Mth.sin((float)angle), vector.y, vector.x * (double)Mth.sin((float)angle) + vector.z * (double)Mth.cos((float)angle));
    }

    public static float toYaw(Vec3 vector) {
        return (float)Math.toDegrees(Math.atan2(-vector.x, vector.z));
    }

    public static boolean isZero(Vec3 vector) {
        return vector.x == 0.0 && vector.y == 0.0 && vector.z == 0.0;
    }

    public static boolean isZero(Vec2 vector) {
        return vector.x == 0.0f && vector.y == 0.0f;
    }
}

