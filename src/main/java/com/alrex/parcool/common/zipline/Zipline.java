package com.alrex.parcool.common.zipline;

import com.alrex.parcool.common.entity.zipline.ZiplineRopeEntity;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public abstract class Zipline {
    public static final double MAXIMUM_HORIZONTAL_DISTANCE = 72.0;
    public static final double MAXIMUM_VERTICAL_DISTANCE = 36.72;
    private final Vec3 startPos;
    private final Vec3 endPos;
    private final Vec3 endOffsetFromStart;
    private final double horizontalDistance;

    protected Zipline(Vec3 point1, Vec3 point2) {
        if (point1.y() <= point2.y()) {
            this.startPos = point1;
            this.endPos = point2;
        } else {
            this.startPos = point2;
            this.endPos = point1;
        }
        this.endOffsetFromStart = this.endPos.subtract(this.startPos);
        this.horizontalDistance = Math.hypot(this.endOffsetFromStart.x(), this.endOffsetFromStart.z());
    }

    public Vec3 getStartPos() {
        return this.startPos;
    }

    public Vec3 getEndPos() {
        return this.endPos;
    }

    public Vec3 getOffsetToEndFromStart() {
        return this.endOffsetFromStart;
    }

    public double getHorizontalDistance() {
        return this.horizontalDistance;
    }

    @Nullable
    public static ZiplineRopeEntity getHangableZipline(Level world, Player player) {
        return Zipline.getHangableZipline(world, player, null);
    }

    @Nullable
    public static ZiplineRopeEntity getHangableZipline(Level world, Player player, @Nullable ZiplineRopeEntity except) {
        double d = 38.44;
        List entities = world.getEntitiesOfClass(ZiplineRopeEntity.class, player.getBoundingBox().inflate(38.44, 37.72, 38.44));
        double catchRange = player.getBbWidth();
        double yDeltaMovement = player.getDeltaMovement().y();
        double yDistanceScale = Mth.clamp((double)(0.7 / (Math.abs(yDeltaMovement) + 0.7)), (double)0.4, (double)1.0);
        Vec3 grabPos = player.position().add(0.0, (double)player.getBbHeight() * 1.11, 0.0);
        for (ZiplineRopeEntity ziplineEntity : entities) {
            double distSqr;
            Zipline zipline;
            if (except == ziplineEntity || ziplineEntity.getStartPos().equals((Object)BlockPos.ZERO) && ziplineEntity.getEndPos().equals((Object)BlockPos.ZERO) || !(zipline = ziplineEntity.getZipline()).isPossiblyHangable(grabPos) || !((distSqr = zipline.getSquaredDistanceApproximately(grabPos, yDistanceScale)) < catchRange * catchRange)) continue;
            return ziplineEntity;
        }
        return null;
    }

    public boolean conflictsWithSomething(Level world) {
        int count = (int)Math.floor(this.getHorizontalDistance());
        for (int i = 1; i < count - 1; ++i) {
            Vec3 midPoint = this.getMidPoint((float)i / (float)count);
            double d = 0.2;
            if (world.noCollision(new AABB(midPoint.subtract(0.2, 0.2, 0.2), midPoint.add(0.2, 0.2, 0.2)))) continue;
            return true;
        }
        return false;
    }

    public Vec3 getMidPoint(float t) {
        return this.getMidPointOffsetFromStart(t).add(this.getStartPos());
    }

    public abstract Vec3 getMidPointOffsetFromStart(float var1);

    public abstract float getSlope(float var1);

    public abstract float getParameter(Vec3 var1);

    public abstract double getMovedPositionByParameterApproximately(float var1, float var2);

    public double getSquaredDistanceApproximately(Vec3 position) {
        return this.getSquaredDistanceApproximately(position, 1.0);
    }

    public abstract double getSquaredDistanceApproximately(Vec3 var1, double var2);

    public boolean isPossiblyHangable(Vec3 position) {
        return new AABB(this.getStartPos().x(), this.getStartPos().y(), this.getStartPos().z(), this.getEndPos().x(), this.getEndPos().y(), this.getEndPos().z()).inflate(1.0).contains(position);
    }
}

