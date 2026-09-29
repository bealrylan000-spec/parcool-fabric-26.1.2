package com.alrex.parcool.common.entity.zipline;

import com.alrex.parcool.common.block.zipline.ZiplineHookTileEntity;
import com.alrex.parcool.common.block.zipline.ZiplineInfo;
import com.alrex.parcool.common.entity.EntityTypes;
import com.alrex.parcool.common.zipline.Zipline;
import com.alrex.parcool.common.zipline.ZiplineType;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class ZiplineRopeEntity
extends Entity {
    private static final EntityDataAccessor<BlockPos> DATA_START_POS = SynchedEntityData.defineId(ZiplineRopeEntity.class, (EntityDataSerializer)EntityDataSerializers.BLOCK_POS);
    private static final EntityDataAccessor<BlockPos> DATA_END_POS = SynchedEntityData.defineId(ZiplineRopeEntity.class, (EntityDataSerializer)EntityDataSerializers.BLOCK_POS);
    private static final EntityDataAccessor<Integer> DATA_COLOR = SynchedEntityData.defineId(ZiplineRopeEntity.class, (EntityDataSerializer)EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> DATA_ZIP_TYPE = SynchedEntityData.defineId(ZiplineRopeEntity.class, (EntityDataSerializer)EntityDataSerializers.INT);
    private EntityDimensions size;
    private BlockPos zipline_start;
    private BlockPos zipline_end;
    private ZiplineType zip_type;
    private Zipline zipline;
    @Nullable
    private AABB cullingBB;

    public ZiplineRopeEntity(EntityType<?> p_i48580_1_, Level p_i48580_2_) {
        super(p_i48580_1_, p_i48580_2_);
    }

    public ZiplineRopeEntity(Level world, BlockPos start, BlockPos end, ZiplineInfo info) {
        super((EntityType)EntityTypes.ZIPLINE_ROPE.get(), world);
        this.setStartPos(start);
        this.setEndPos(end);
        this.setColor(info.getColor());
        this.setZiplineType(info.getType());
        this.setPos((double)(end.getX() + start.getX()) / 2.0 + 0.5, Math.min(end.getY(), start.getY()), (double)(end.getZ() + start.getZ()) / 2.0 + 0.5);
        this.noPhysics = true;
        this.cullingBB = new AABB((double)start.getX(), (double)start.getY(), (double)start.getZ(), (double)end.getX(), (double)end.getY(), (double)end.getZ()).inflate(1.0);
    }

    public Zipline getZipline() {
        BlockPos start = this.getStartPos();
        BlockPos end = this.getEndPos();
        ZiplineType type = this.getZiplineType();
        if (!(this.zipline != null && start.equals((Object)this.zipline_start) && end.equals((Object)this.zipline_end) && type.equals((Object)this.zip_type))) {
            Vec3 endPos;
            Vec3 startPos;
            this.zipline_start = start;
            this.zipline_end = end;
            this.zip_type = type;
            this.cullingBB = new AABB((double)start.getX(), (double)start.getY(), (double)start.getZ(), (double)end.getX(), (double)end.getY(), (double)end.getZ()).inflate(1.0);
            BlockEntity startEntity = this.level().getBlockEntity(start);
            BlockEntity endEntity = this.level().getBlockEntity(end);
            boolean delayInit = false;
            if (startEntity instanceof ZiplineHookTileEntity) {
                startPos = ((ZiplineHookTileEntity)startEntity).getActualZiplinePoint(end);
            } else {
                startPos = new Vec3((double)start.getX() + 0.5, (double)start.getY() + 0.5, (double)start.getZ() + 0.5);
                delayInit = true;
            }
            if (endEntity instanceof ZiplineHookTileEntity) {
                endPos = ((ZiplineHookTileEntity)endEntity).getActualZiplinePoint(start);
            } else {
                endPos = new Vec3((double)end.getX() + 0.5, (double)end.getY() + 0.5, (double)end.getZ() + 0.5);
                delayInit = true;
            }
            if (delayInit) {
                return type.getZipline(startPos, endPos);
            }
            this.zipline = type.getZipline(startPos, endPos);
        }
        return this.zipline;
    }

    public boolean shouldRender(double x, double y, double z) {
        double baseZOffset;
        double baseYOffset;
        BlockPos start = this.getStartPos();
        BlockPos end = this.getEndPos();
        if (start == BlockPos.ZERO && end == BlockPos.ZERO) {
            return false;
        }
        double xOffset = end.getX() - start.getX();
        double yOffset = end.getY() - start.getY();
        double zOffset = end.getZ() - start.getZ();
        double baseXOffset = (double)start.getX() - x;
        double t = -(xOffset * baseXOffset + yOffset * (baseYOffset = (double)start.getY() - y) + zOffset * (baseZOffset = (double)start.getZ() - z)) / (xOffset * xOffset + yOffset * yOffset + zOffset * zOffset);
        Vec3 mostNearPoint = new Vec3(xOffset * t + (double)start.getX(), yOffset * t + (double)start.getY(), zOffset * t + (double)start.getZ());
        double distanceSqr = mostNearPoint.distanceToSqr(x, y, z);
        return distanceSqr < 5184.0;
    }

    @Nonnull
    public AABB getBoundingBoxForCulling() {
        if (this.cullingBB == null) {
            return super.getBoundingBoxForCulling();
        }
        return this.cullingBB;
    }

    @Nonnull
    public EntityDimensions getDimensions(@Nonnull Pose p_213305_1_) {
        if (this.size == null) {
            return EntityDimensions.fixed((float)0.1f, (float)0.1f);
        }
        return this.size;
    }

    public void move(MoverType p_19973_, Vec3 p_19974_) {
    }

    public BlockPos getStartPos() {
        return (BlockPos)this.getEntityData().get(DATA_START_POS);
    }

    public BlockPos getEndPos() {
        return (BlockPos)this.getEntityData().get(DATA_END_POS);
    }

    private void setStartPos(BlockPos start) {
        this.getEntityData().set(DATA_START_POS, (Object)start);
    }

    private void setEndPos(BlockPos end) {
        this.getEntityData().set(DATA_END_POS, (Object)end);
    }

    public int getColor() {
        return (Integer)this.getEntityData().get(DATA_COLOR);
    }

    private void setColor(int color) {
        this.getEntityData().set(DATA_COLOR, (Object)color);
    }

    public ZiplineType getZiplineType() {
        return ZiplineType.values()[(Integer)this.getEntityData().get(DATA_ZIP_TYPE) % ZiplineType.values().length];
    }

    private void setZiplineType(ZiplineType type) {
        this.getEntityData().set(DATA_ZIP_TYPE, (Object)type.ordinal());
    }

    @Nonnull
    public InteractionResult interact(Player player, InteractionHand p_19979_) {
        return InteractionResult.sidedSuccess((boolean)player.level().isClientSide());
    }

    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_START_POS, (Object)BlockPos.ZERO);
        builder.define(DATA_END_POS, (Object)BlockPos.ZERO);
        builder.define(DATA_COLOR, (Object)-11763738);
        builder.define(DATA_ZIP_TYPE, (Object)ZiplineType.STANDARD.ordinal());
    }

    public void readAdditionalSaveData(@Nonnull CompoundTag compoundNBT) {
        this.setStartPos(new BlockPos(compoundNBT.getInt("Tile1_X"), compoundNBT.getInt("Tile1_Y"), compoundNBT.getInt("Tile1_Z")));
        this.setEndPos(new BlockPos(compoundNBT.getInt("Tile2_X"), compoundNBT.getInt("Tile2_Y"), compoundNBT.getInt("Tile2_Z")));
    }

    public void addAdditionalSaveData(@Nonnull CompoundTag compoundNBT) {
        BlockPos startPos = this.getStartPos();
        BlockPos endPos = this.getEndPos();
        compoundNBT.putInt("Tile1_X", startPos.getX());
        compoundNBT.putInt("Tile1_X", startPos.getY());
        compoundNBT.putInt("Tile1_X", startPos.getZ());
        compoundNBT.putInt("Tile2_X", endPos.getX());
        compoundNBT.putInt("Tile2_X", endPos.getY());
        compoundNBT.putInt("Tile2_X", endPos.getZ());
    }
}

