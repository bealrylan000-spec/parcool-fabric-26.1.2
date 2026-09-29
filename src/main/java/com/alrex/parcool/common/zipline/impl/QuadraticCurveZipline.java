package com.alrex.parcool.common.zipline.impl;

import com.alrex.parcool.common.zipline.Zipline;
import net.minecraft.world.phys.Vec3;

public class QuadraticCurveZipline
extends Zipline {
    public QuadraticCurveZipline(Vec3 point1, Vec3 point2) {
        super(point1, point2);
    }

    @Override
    public Vec3 getMidPointOffsetFromStart(float t) {
        double x = this.getOffsetToEndFromStart().x() * (double)t;
        double z = this.getOffsetToEndFromStart().z() * (double)t;
        double y = this.getOffsetToEndFromStart().y() * (double)t * (double)t;
        return new Vec3(x, y, z);
    }

    @Override
    public float getSlope(float t) {
        return (float)((double)(2.0f * t) * this.getOffsetToEndFromStart().y() / this.getHorizontalDistance());
    }

    @Override
    public float getParameter(Vec3 position) {
        double offsetX = this.getOffsetToEndFromStart().x();
        double offsetZ = this.getOffsetToEndFromStart().z();
        return (float)(((position.x() - this.getStartPos().x()) * offsetX + (position.z() - this.getStartPos().z()) * offsetZ) / (this.getHorizontalDistance() * this.getHorizontalDistance()));
    }

    private static double getDistanceFrom0(double xzLen, double a) {
        double r = Math.sqrt(1.0 + 4.0 * a * a * xzLen * xzLen);
        return 0.5 * (xzLen * r + Math.log(Math.abs(2.0 * a * xzLen + r)) / (2.0 * a));
    }

    private static double getDistanceFrom0Derivative(double xzLen, double a) {
        return Math.sqrt(1.0 + 4.0 * a * a * xzLen * xzLen);
    }

    @Override
    public double getMovedPositionByParameterApproximately(float currentT, float movement) {
        double xzLength = this.getHorizontalDistance();
        double a = this.getOffsetToEndFromStart().y() / (xzLength * xzLength);
        if (Math.abs(a) < 0.005) {
            return (double)movement / xzLength + (double)currentT;
        }
        double destination = QuadraticCurveZipline.getDistanceFrom0((double)currentT * xzLength, a) + (double)movement;
        double interim = (double)currentT * xzLength;
        for (int i = 0; i < 20; ++i) {
            double oldInterim = interim;
            if (!(Math.abs(oldInterim - (interim -= (QuadraticCurveZipline.getDistanceFrom0(interim, a) - destination) / QuadraticCurveZipline.getDistanceFrom0Derivative(interim, a))) < 0.001)) continue;
            return interim / xzLength;
        }
        return interim / xzLength;
    }

    @Override
    public double getSquaredDistanceApproximately(Vec3 position, double yDistanceScale) {
        float t = this.getParameter(position);
        Vec3 simplifiedNearestPoint = this.getMidPoint(t);
        double xOffset = simplifiedNearestPoint.x - position.x;
        double zOffset = simplifiedNearestPoint.z - position.z;
        double yOffset = (simplifiedNearestPoint.y - position.y) * yDistanceScale;
        return xOffset * xOffset + zOffset * zOffset + yOffset * yOffset;
    }

    public double getAccurateDistance(Vec3 position) {
        throw new UnsupportedOperationException("Not Implemented");
    }
}

