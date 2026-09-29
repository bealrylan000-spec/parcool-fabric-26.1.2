package com.alrex.parcool.common.zipline.impl;

import com.alrex.parcool.common.zipline.Zipline;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class GeneralQuadraticCurveZipline
extends Zipline {
    private final double tAtVertex;
    private final double distOfXZToVertex;
    private final double getMidPointOffsetFromStart$a;
    private final double getDistanceFrom0$offset;
    private final double getMovedPositionByParameterApproximately$a;

    public GeneralQuadraticCurveZipline(Vec3 point1, Vec3 point2, double lowestPointOffset) {
        super(point1, point2);
        double straightDistance = Math.hypot(this.getHorizontalDistance(), this.getOffsetToEndFromStart().y());
        double yOffsetAtVertex = Math.abs(lowestPointOffset);
        this.tAtVertex = Math.abs(this.getOffsetToEndFromStart().y()) < 0.005 ? 0.5 : (Math.sqrt(yOffsetAtVertex * (yOffsetAtVertex + this.getOffsetToEndFromStart().y())) - yOffsetAtVertex) / this.getOffsetToEndFromStart().y();
        this.distOfXZToVertex = this.tAtVertex * this.getHorizontalDistance();
        this.getMidPointOffsetFromStart$a = Math.abs(this.tAtVertex - 0.5) < 0.005 ? 4.0 * yOffsetAtVertex : this.getOffsetToEndFromStart().y() / (1.0 - 2.0 * this.tAtVertex);
        this.getMovedPositionByParameterApproximately$a = this.getOffsetToEndFromStart().y() / (this.getHorizontalDistance() * this.getHorizontalDistance());
        this.getDistanceFrom0$offset = this.getDistance(-this.distOfXZToVertex, this.getMovedPositionByParameterApproximately$a);
    }

    @Override
    public Vec3 getMidPointOffsetFromStart(float t) {
        return new Vec3(this.getOffsetToEndFromStart().x() * (double)t, this.getMidPointOffsetFromStart$a * (double)t * ((double)t - 2.0 * this.tAtVertex), this.getOffsetToEndFromStart().z() * (double)t);
    }

    @Override
    public float getSlope(float t) {
        return (float)(2.0 * ((double)t - this.tAtVertex) * this.getMidPointOffsetFromStart$a / this.getHorizontalDistance());
    }

    @Override
    public float getParameter(Vec3 position) {
        double offsetX = this.getOffsetToEndFromStart().x();
        double offsetZ = this.getOffsetToEndFromStart().z();
        return (float)(((position.x() - this.getStartPos().x()) * offsetX + (position.z() - this.getStartPos().z()) * offsetZ) / (this.getHorizontalDistance() * this.getHorizontalDistance()));
    }

    private double getDistance(double xzLen, double a) {
        double r = Math.sqrt(1.0 + 4.0 * a * a * xzLen * xzLen);
        return 0.5 * (xzLen * r + Math.log(Math.abs(2.0 * a * xzLen + r)) / (2.0 * a));
    }

    private double getDistanceFrom0(double xzLen, double a) {
        return this.getDistance(xzLen - this.distOfXZToVertex, a) - this.getDistanceFrom0$offset;
    }

    private double getDistanceFrom0Derivative(double xzLen, double a) {
        return Math.sqrt(1.0 + 4.0 * a * a * (xzLen -= this.distOfXZToVertex) * xzLen);
    }

    @Override
    public double getMovedPositionByParameterApproximately(float currentT, float movement) {
        double xzLength = this.getHorizontalDistance();
        double a = this.getMovedPositionByParameterApproximately$a;
        if (Math.abs(a) < 0.005) {
            return (double)movement / xzLength + (double)currentT;
        }
        double destination = this.getDistanceFrom0((double)currentT * xzLength, a) + (double)movement;
        double interim = (double)currentT * xzLength;
        for (int i = 0; i < 20; ++i) {
            double oldInterim = interim;
            if (!(Math.abs(oldInterim - (interim -= (this.getDistanceFrom0(interim, a) - destination) / this.getDistanceFrom0Derivative(interim, a))) < 0.001)) continue;
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

    @Override
    public boolean isPossiblyHangable(Vec3 position) {
        return new AABB(this.getStartPos().x(), this.getMidPoint((float)this.tAtVertex).y(), this.getStartPos().z(), this.getEndPos().x(), this.getEndPos().y(), this.getEndPos().z()).inflate(1.0).contains(position);
    }
}

