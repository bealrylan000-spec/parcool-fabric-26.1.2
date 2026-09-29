package com.alrex.parcool.common.zipline.impl;

import com.alrex.parcool.common.zipline.Zipline;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class StraightZipline
extends Zipline {
    private final float slope = (float)(this.getOffsetToEndFromStart().y() * Mth.fastInvSqrt((double)(this.getOffsetToEndFromStart().x() * this.getOffsetToEndFromStart().x() + this.getOffsetToEndFromStart().z() * this.getOffsetToEndFromStart().z())));

    public StraightZipline(Vec3 point1, Vec3 point2) {
        super(point1, point2);
    }

    public Vec3 getMidPoint(double t) {
        return this.getMidPointOffsetFromStart(t).add(this.getStartPos());
    }

    @Override
    public Vec3 getMidPointOffsetFromStart(float t) {
        return this.getOffsetToEndFromStart().scale((double)t);
    }

    public Vec3 getMidPointOffsetFromStart(double t) {
        return this.getOffsetToEndFromStart().scale(t);
    }

    @Override
    public float getSlope(float t) {
        return this.slope;
    }

    @Override
    public float getParameter(Vec3 position) {
        return (float)this.getParameterD(position);
    }

    public double getParameterD(Vec3 position) {
        double xOffset = this.getOffsetToEndFromStart().x();
        double yOffset = this.getOffsetToEndFromStart().y();
        double zOffset = this.getOffsetToEndFromStart().z();
        double baseXOffset = this.getStartPos().x() - position.x;
        double baseYOffset = this.getStartPos().y() - position.y;
        double baseZOffset = this.getStartPos().z() - position.z;
        return -(xOffset * baseXOffset + yOffset * baseYOffset + zOffset * baseZOffset) / (xOffset * xOffset + yOffset * yOffset + zOffset * zOffset);
    }

    @Override
    public double getMovedPositionByParameterApproximately(float currentT, float movement) {
        float deltaXZ = movement * Mth.invSqrt((float)(this.slope * this.slope + 1.0f));
        return (double)currentT + (double)deltaXZ / this.getHorizontalDistance();
    }

    @Override
    public double getSquaredDistanceApproximately(Vec3 position, double yDistanceScale) {
        double t = this.getParameterD(position);
        Vec3 mostNearPoint = this.getMidPoint(t);
        double xOffset = mostNearPoint.x - position.x;
        double zOffset = mostNearPoint.z - position.z;
        double yOffset = (mostNearPoint.y - position.y) * yDistanceScale;
        return xOffset * xOffset + zOffset * zOffset + yOffset * yOffset;
    }
}

