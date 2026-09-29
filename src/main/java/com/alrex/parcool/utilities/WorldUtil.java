package com.alrex.parcool.utilities;

import com.alrex.parcool.common.action.impl.HangDown;
import com.alrex.parcool.common.tags.BlockTags;
import com.alrex.parcool.utilities.VectorUtil;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CrossCollisionBlock;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.EndRodBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.WallSide;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class WorldUtil {
    @Nullable
    public static Vec3 getRunnableWall(LivingEntity entity, double range) {
        double width = entity.getBbWidth() * 0.4f;
        double wallX = 0.0;
        double wallZ = 0.0;
        Vec3 pos = entity.position();
        List<AABB> boxes = Arrays.asList(new AABB(pos.x() - width, pos.y(), pos.z() - width, pos.x() + width, pos.y() + (double)entity.getBbHeight() * 0.3, pos.z() + width), new AABB(pos.x() - width, pos.y() + (double)entity.getBbHeight() * 0.85, pos.z() - width, pos.x() + width, pos.y() + (double)entity.getBbHeight(), pos.z() + width));
        if (boxes.stream().noneMatch(box -> entity.level().noCollision(box.expandTowards(range, 0.0, 0.0)))) {
            wallX += 1.0;
        }
        if (boxes.stream().noneMatch(box -> entity.level().noCollision(box.expandTowards(-range, 0.0, 0.0)))) {
            wallX -= 1.0;
        }
        if (boxes.stream().noneMatch(box -> entity.level().noCollision(box.expandTowards(0.0, 0.0, range)))) {
            wallZ += 1.0;
        }
        if (boxes.stream().noneMatch(box -> entity.level().noCollision(box.expandTowards(0.0, 0.0, -range)))) {
            wallZ -= 1.0;
        }
        if (wallX == 0.0 && wallZ == 0.0) {
            return null;
        }
        return new Vec3(wallX, 0.0, wallZ);
    }

    @Nullable
    public static Vec3 getWall(LivingEntity entity) {
        return WorldUtil.getWall(entity, (double)entity.getBbWidth() * 0.5);
    }

    @Nullable
    public static Vec3 getWall(LivingEntity entity, double range) {
        double width = (double)entity.getBbWidth() * 0.49;
        double wallX = 0.0;
        double wallZ = 0.0;
        Vec3 pos = entity.position();
        LinkedList<AABB> boxes = new LinkedList<AABB>();
        int division = 2;
        double singleYHeight = entity.getBbHeight() / 2.0f;
        for (int i = 0; i < 2; ++i) {
            boxes.add(new AABB(pos.x() - width, pos.y() + singleYHeight * (double)i, pos.z() - width, pos.x() + width, pos.y() + singleYHeight * (double)(i + 1), pos.z() + width));
        }
        if (boxes.stream().noneMatch(box -> entity.level().noCollision(box.expandTowards(range, 0.0, 0.0)))) {
            wallX += 1.0;
        }
        if (boxes.stream().noneMatch(box -> entity.level().noCollision(box.expandTowards(-range, 0.0, 0.0)))) {
            wallX -= 1.0;
        }
        if (boxes.stream().noneMatch(box -> entity.level().noCollision(box.expandTowards(0.0, 0.0, range)))) {
            wallZ += 1.0;
        }
        if (boxes.stream().noneMatch(box -> entity.level().noCollision(box.expandTowards(0.0, 0.0, -range)))) {
            wallZ -= 1.0;
        }
        if (wallX == 0.0 && wallZ == 0.0) {
            return null;
        }
        return new Vec3(wallX, 0.0, wallZ);
    }

    @Nullable
    public static Vec3 getVaultableStep(LivingEntity entity) {
        double d = (double)entity.getBbWidth() * 0.5;
        Level world = entity.getCommandSenderWorld();
        double distance = entity.getBbWidth() / 2.0f;
        double baseLine = Math.min((double)entity.getBbHeight() * 0.86, WorldUtil.getWallHeight(entity));
        double stepX = 0.0;
        double stepZ = 0.0;
        Vec3 pos = entity.position();
        AABB baseBoxBottom = new AABB(pos.x() - d, pos.y(), pos.z() - d, pos.x() + d, pos.y() + baseLine, pos.z() + d);
        AABB baseBoxTop = new AABB(pos.x() - d, pos.y() + baseLine, pos.z() - d, pos.x() + d, pos.y() + baseLine + (double)entity.getBbHeight(), pos.z() + d);
        if (!world.noCollision((Entity)entity, baseBoxBottom.expandTowards(distance, 0.0, 0.0)) && world.noCollision((Entity)entity, baseBoxTop.expandTowards(distance + 1.8, 0.0, 0.0))) {
            stepX += 1.0;
        }
        if (!world.noCollision((Entity)entity, baseBoxBottom.expandTowards(-distance, 0.0, 0.0)) && world.noCollision((Entity)entity, baseBoxTop.expandTowards(-(distance + 1.8), 0.0, 0.0))) {
            stepX -= 1.0;
        }
        if (!world.noCollision((Entity)entity, baseBoxBottom.expandTowards(0.0, 0.0, distance)) && world.noCollision((Entity)entity, baseBoxTop.expandTowards(0.0, 0.0, distance + 1.8))) {
            stepZ += 1.0;
        }
        if (!world.noCollision((Entity)entity, baseBoxBottom.expandTowards(0.0, 0.0, -distance)) && world.noCollision((Entity)entity, baseBoxTop.expandTowards(0.0, 0.0, -(distance + 1.8)))) {
            stepZ -= 1.0;
        }
        if (stepX == 0.0 && stepZ == 0.0) {
            return null;
        }
        if (stepX == 0.0 || stepZ == 0.0) {
            Vec3 result = new Vec3(stepX, 0.0, stepZ);
            Vec3 blockPosition = entity.position().add(result).add(0.0, 0.5, 0.0);
            BlockPos target = new BlockPos(Mth.floor((double)blockPosition.x()), Mth.floor((double)blockPosition.y()), Mth.floor((double)blockPosition.z()));
            if (!world.isLoaded(target)) {
                return null;
            }
            BlockState state = world.getBlockState(target);
            if (state.getBlock() instanceof StairBlock) {
                Half half = (Half)state.getValue((Property)StairBlock.HALF);
                if (half != Half.BOTTOM) {
                    return result;
                }
                Direction direction = (Direction)state.getValue((Property)StairBlock.FACING);
                if (stepZ > 0.0 && direction == Direction.SOUTH) {
                    return null;
                }
                if (stepZ < 0.0 && direction == Direction.NORTH) {
                    return null;
                }
                if (stepX > 0.0 && direction == Direction.EAST) {
                    return null;
                }
                if (stepX < 0.0 && direction == Direction.WEST) {
                    return null;
                }
            }
        }
        return new Vec3(stepX, 0.0, stepZ);
    }

    public static double getWallHeight(LivingEntity entity, Vec3 direction, double maxHeight, double accuracy) {
        double d = (double)entity.getBbWidth() * 0.49;
        direction = direction.normalize();
        Level world = entity.level();
        Vec3 pos = entity.position();
        boolean canReturn = false;
        for (double height = 0.0; height < maxHeight; height += accuracy) {
            AABB box = new AABB(pos.x() + d + (double)(direction.x() > 0.0 ? 1 : 0), pos.y() + height, pos.z() + d + (double)(direction.z() > 0.0 ? 1 : 0), pos.x() - d + (double)(direction.x() < 0.0 ? -1 : 0), pos.y() + height + accuracy, pos.z() - d + (double)(direction.z() < 0.0 ? -1 : 0));
            if (!world.noCollision((Entity)entity, box)) {
                canReturn = true;
                continue;
            }
            if (!canReturn) continue;
            return height;
        }
        return maxHeight;
    }

    public static double getWallHeight(LivingEntity entity) {
        Vec3 wall = WorldUtil.getWall(entity);
        if (wall == null) {
            return 0.0;
        }
        Level world = entity.level();
        double accuracy = entity.getBbHeight() / 18.0f;
        double d = (double)entity.getBbWidth() * 0.5;
        int loopNum = (int)Math.round((double)entity.getBbHeight() / accuracy);
        Vec3 pos = entity.position();
        boolean canReturn = false;
        for (int i = 0; i < loopNum; ++i) {
            AABB box = new AABB(pos.x() + d + (double)(wall.x() > 0.0 ? 1 : 0), pos.y() + accuracy * (double)i, pos.z() + d + (double)(wall.z() > 0.0 ? 1 : 0), pos.x() - d + (double)(wall.x() < 0.0 ? -1 : 0), pos.y() + accuracy * (double)(i + 1), pos.z() - d + (double)(wall.z() < 0.0 ? -1 : 0));
            if (!world.noCollision((Entity)entity, box)) {
                canReturn = true;
                continue;
            }
            if (!canReturn) continue;
            return accuracy * (double)i;
        }
        return entity.getBbHeight();
    }

    @Nullable
    public static HangDown.BarAxis getHangableBars(LivingEntity entity) {
        double bbWidth = entity.getBbWidth() / 4.0f;
        double bbHeight = 0.35;
        AABB bb = new AABB(entity.getX() - bbWidth, entity.getY() + (double)entity.getBbHeight(), entity.getZ() - bbWidth, entity.getX() + bbWidth, entity.getY() + (double)entity.getBbHeight() + 0.35, entity.getZ() + bbWidth);
        if (entity.getCommandSenderWorld().noCollision((Entity)entity, bb)) {
            return null;
        }
        BlockPos pos = new BlockPos(Mth.floor((double)entity.getX()), Mth.floor((double)(entity.getY() + (double)entity.getBbHeight() + 0.4)), Mth.floor((double)entity.getZ()));
        if (!entity.getCommandSenderWorld().isLoaded(pos)) {
            return null;
        }
        BlockState state = entity.getCommandSenderWorld().getBlockState(pos);
        Block block = state.getBlock();
        HangDown.BarAxis axis = null;
        if (block instanceof RotatedPillarBlock) {
            if (state.isCollisionShapeFullBlock((BlockGetter)entity.getCommandSenderWorld(), pos)) {
                return null;
            }
            Direction.Axis pillarAxis = (Direction.Axis)state.getValue((Property)RotatedPillarBlock.AXIS);
            switch (pillarAxis) {
                case X: {
                    axis = HangDown.BarAxis.X;
                    break;
                }
                case Z: {
                    axis = HangDown.BarAxis.Z;
                }
            }
        } else if (block instanceof EndRodBlock) {
            if (state.isCollisionShapeFullBlock((BlockGetter)entity.level(), pos)) {
                return null;
            }
            Direction direction = (Direction)state.getValue((Property)DirectionalBlock.FACING);
            switch (direction) {
                case EAST: 
                case WEST: {
                    axis = HangDown.BarAxis.X;
                    break;
                }
                case NORTH: 
                case SOUTH: {
                    axis = HangDown.BarAxis.Z;
                }
            }
        } else if (block instanceof CrossCollisionBlock) {
            int zCount = 0;
            int xCount = 0;
            if (((Boolean)state.getValue((Property)CrossCollisionBlock.NORTH)).booleanValue()) {
                ++zCount;
            }
            if (((Boolean)state.getValue((Property)CrossCollisionBlock.SOUTH)).booleanValue()) {
                ++zCount;
            }
            if (((Boolean)state.getValue((Property)CrossCollisionBlock.EAST)).booleanValue()) {
                ++xCount;
            }
            if (((Boolean)state.getValue((Property)CrossCollisionBlock.WEST)).booleanValue()) {
                ++xCount;
            }
            if (zCount > 0 && xCount == 0) {
                axis = HangDown.BarAxis.Z;
            }
            if (xCount > 0 && zCount == 0) {
                axis = HangDown.BarAxis.X;
            }
        } else if (block instanceof WallBlock) {
            int zCount = 0;
            int xCount = 0;
            if (state.getValue((Property)WallBlock.NORTH_WALL) != WallSide.NONE) {
                ++zCount;
            }
            if (state.getValue((Property)WallBlock.SOUTH_WALL) != WallSide.NONE) {
                ++zCount;
            }
            if (state.getValue((Property)WallBlock.EAST_WALL) != WallSide.NONE) {
                ++xCount;
            }
            if (state.getValue((Property)WallBlock.WEST_WALL) != WallSide.NONE) {
                ++xCount;
            }
            if (zCount > 0 && xCount == 0) {
                axis = HangDown.BarAxis.Z;
            }
            if (xCount > 0 && zCount == 0) {
                axis = HangDown.BarAxis.X;
            }
        }
        return axis;
    }

    public static boolean existsSpaceBelow(LivingEntity entity) {
        Vec3 center;
        Level world = entity.level();
        if (!world.isLoaded(new BlockPos(Mth.floor((double)(center = entity.position()).x()), Mth.floor((double)center.y()), Mth.floor((double)center.z())))) {
            return false;
        }
        double height = (double)entity.getBbHeight() * 1.5;
        double width = entity.getBbWidth() * 2.0f;
        AABB boundingBox = new AABB(center.x() - width, center.y() - 9.0, center.z() - width, center.x() + width, center.y() + height, center.z() + width);
        return world.noCollision(boundingBox);
    }

    public static boolean existsDivableSpace(LivingEntity entity) {
        int i;
        Level world = entity.getCommandSenderWorld();
        double width = (double)entity.getBbWidth() * 1.5;
        double height = (double)entity.getBbHeight() * 1.5;
        double wideWidth = entity.getBbWidth() * 2.0f;
        Vec3 center = entity.position();
        if (!world.isLoaded(new BlockPos(Mth.floor((double)center.x()), Mth.floor((double)center.y()), Mth.floor((double)center.z())))) {
            return false;
        }
        Vec3 diveDirection = VectorUtil.fromYawDegree(entity.getYHeadRot());
        for (int i2 = 0; i2 < 4; ++i2) {
            Vec3 centerPoint = center.add(diveDirection.scale(width * (double)i2));
            AABB box = new AABB(centerPoint.x() - width, centerPoint.y() + 0.05, centerPoint.z() - width, centerPoint.x() + width, centerPoint.y() + height, centerPoint.z() + width);
            if (world.noCollision((Entity)entity, box)) continue;
            return false;
        }
        AABB verticalWideBox = new AABB((center = center.add(diveDirection.scale(4.0))).x() - wideWidth, center.y() - 7.0, center.z() - wideWidth, center.x() + wideWidth, center.y() + height, center.z() + wideWidth);
        if (world.noCollision(verticalWideBox)) {
            return true;
        }
        BlockPos centerBlockPos = new BlockPos(Mth.floor((double)center.x()), Mth.floor((double)(center.y() - 0.5)), Mth.floor((double)center.z()));
        if (!world.isLoaded(centerBlockPos)) {
            return false;
        }
        verticalWideBox = new AABB(center.x() - wideWidth, center.y() - 2.9, center.z() - wideWidth, center.x() + wideWidth, center.y() + height, center.z() + wideWidth);
        int waterLevel = -1;
        for (i = 0; i < 6; ++i) {
            Block block = world.getBlockState(centerBlockPos.below(i)).getBlock();
            if (block == Blocks.AIR) continue;
            if (block == Blocks.WATER) {
                waterLevel = i;
                break;
            }
            return false;
        }
        if (waterLevel == -1) {
            return false;
        }
        boolean filledWithWater = true;
        while (i < waterLevel + 3) {
            BlockState state = world.getBlockState(centerBlockPos.below(i));
            if (state.getBlock() != Blocks.WATER) {
                filledWithWater = false;
                break;
            }
            ++i;
        }
        return filledWithWater && world.noCollision(verticalWideBox);
    }

    @Nullable
    public static Vec3 getGrabbableWall(LivingEntity entity) {
        double d = (double)entity.getBbWidth() * 0.5;
        Level world = entity.level();
        double distance = entity.getBbWidth() / 2.0f;
        double baseLine1 = entity.getEyeHeight() + (entity.getBbHeight() - entity.getEyeHeight()) / 2.0f;
        double baseLine2 = entity.getBbHeight() + (entity.getBbHeight() - entity.getEyeHeight()) / 2.0f;
        Vec3 wall1 = WorldUtil.getGrabbableWall(entity, distance, baseLine1);
        if (wall1 != null) {
            return wall1;
        }
        return WorldUtil.getGrabbableWall(entity, distance, baseLine2);
    }

    private static Vec3 getGrabbableWall(LivingEntity entity, double distance, double baseLine) {
        float slipperiness;
        double d = (double)entity.getBbWidth() * 0.49;
        Level world = entity.getCommandSenderWorld();
        Vec3 pos = entity.position();
        AABB baseBoxSide = new AABB(pos.x() - d, pos.y() + baseLine - (double)(entity.getBbHeight() / 6.0f), pos.z() - d, pos.x() + d, pos.y() + baseLine, pos.z() + d);
        AABB baseBoxTop = new AABB(pos.x() - d, pos.y() + baseLine, pos.z() - d, pos.x() + d, pos.y() + (double)entity.getBbHeight(), pos.z() + d);
        int xDirection = 0;
        int zDirection = 0;
        if (!world.noCollision((Entity)entity, baseBoxSide.expandTowards(distance, 0.0, 0.0)) && world.noCollision((Entity)entity, baseBoxTop.expandTowards(distance, 0.0, 0.0))) {
            ++xDirection;
        }
        if (!world.noCollision((Entity)entity, baseBoxSide.expandTowards(-distance, 0.0, 0.0)) && world.noCollision((Entity)entity, baseBoxTop.expandTowards(-distance, 0.0, 0.0))) {
            --xDirection;
        }
        if (!world.noCollision((Entity)entity, baseBoxSide.expandTowards(0.0, 0.0, distance)) && world.noCollision((Entity)entity, baseBoxTop.expandTowards(0.0, 0.0, distance))) {
            ++zDirection;
        }
        if (!world.noCollision((Entity)entity, baseBoxSide.expandTowards(0.0, 0.0, -distance)) && world.noCollision((Entity)entity, baseBoxTop.expandTowards(0.0, 0.0, -distance))) {
            --zDirection;
        }
        if (xDirection == 0 && zDirection == 0) {
            return null;
        }
        if (xDirection != 0 && zDirection != 0) {
            BlockPos blockPos1 = new BlockPos(Mth.floor((double)(entity.getX() + (double)xDirection)), Mth.floor((double)(entity.getBoundingBox().minY + baseLine - 0.3)), Mth.floor((double)entity.getZ()));
            BlockPos blockPos2 = new BlockPos(Mth.floor((double)entity.getX()), Mth.floor((double)(entity.getBoundingBox().minY + baseLine - 0.3)), Mth.floor((double)(entity.getZ() + (double)zDirection)));
            if (!entity.getCommandSenderWorld().isLoaded(blockPos1)) {
                return null;
            }
            if (!entity.getCommandSenderWorld().isLoaded(blockPos2)) {
                return null;
            }
            slipperiness = Math.min(entity.getCommandSenderWorld().getBlockState(blockPos1).getFriction((LevelReader)entity.getCommandSenderWorld(), blockPos1, (Entity)entity), entity.getCommandSenderWorld().getBlockState(blockPos2).getFriction((LevelReader)entity.getCommandSenderWorld(), blockPos2, (Entity)entity));
        } else {
            double blockX = entity.getX() + (double)xDirection;
            double blockZ = entity.getZ() + (double)zDirection;
            BlockPos blockPos = new BlockPos(Mth.floor((double)blockX), Mth.floor((double)(entity.getBoundingBox().minY + baseLine - 0.3)), Mth.floor((double)blockZ));
            if (!entity.level().isLoaded(blockPos)) {
                return null;
            }
            if (entity.level().getBlockState(blockPos).is(Blocks.AIR)) {
                if (xDirection != 0) {
                    blockZ += Math.signum(blockZ - Math.floor(blockZ) - 0.5);
                } else {
                    blockX += Math.signum(blockX - Math.floor(blockX) - 0.5);
                }
                blockPos = new BlockPos(Mth.floor((double)blockX), Mth.floor((double)(entity.getBoundingBox().minY + baseLine - 0.3)), Mth.floor((double)blockZ));
            }
            slipperiness = entity.level().getBlockState(blockPos).getFriction((LevelReader)entity.level(), blockPos, (Entity)entity);
        }
        return (double)slipperiness <= 0.9 ? new Vec3((double)xDirection, 0.0, (double)zDirection) : null;
    }

    public static boolean isHideAbleBlock(BlockState blockState) {
        return blockState.getTags().anyMatch(it -> it.equals(BlockTags.HIDE_ABLE));
    }

    private static boolean getHideAbleSpace$isHideAble(Level world, Block block, BlockPos pos) {
        return world.isLoaded(pos) && world.getBlockState(pos).is(block) && world.getBlockState(pos.above()).isAir();
    }

    @Nullable
    public static Tuple<BlockPos, BlockPos> getHideAbleSpace(Entity entity, BlockPos base) {
        Level world = entity.level();
        if (!world.isLoaded(base)) {
            return null;
        }
        BlockState state = world.getBlockState(base);
        Block block = state.getBlock();
        if (!WorldUtil.isHideAbleBlock(state)) {
            return null;
        }
        if (!world.getBlockState(base.above()).isAir()) {
            if (WorldUtil.getHideAbleSpace$isHideAble(world, block, base.above())) {
                return new Tuple((Object)base, (Object)base.above());
            }
            return null;
        }
        double entityWidth = entity.getBbWidth();
        double entityHeight = entity.getBbHeight();
        if (entityHeight >= 2.0 || entityWidth >= 1.0) {
            return null;
        }
        if (entityHeight < 1.0) {
            return new Tuple((Object)base, (Object)base);
        }
        Vec3 lookAngle = entity.getLookAngle();
        if (Math.abs(lookAngle.z()) > Math.abs(lookAngle.x())) {
            if (lookAngle.z() > 0.0) {
                if (WorldUtil.getHideAbleSpace$isHideAble(world, block, base.south())) {
                    return new Tuple((Object)base, (Object)base.south());
                }
                if (lookAngle.x() > 0.0) {
                    if (WorldUtil.getHideAbleSpace$isHideAble(world, block, base.east())) {
                        return new Tuple((Object)base, (Object)base.east());
                    }
                    if (WorldUtil.getHideAbleSpace$isHideAble(world, block, base.west())) {
                        return new Tuple((Object)base, (Object)base.west());
                    }
                } else {
                    if (WorldUtil.getHideAbleSpace$isHideAble(world, block, base.west())) {
                        return new Tuple((Object)base, (Object)base.west());
                    }
                    if (WorldUtil.getHideAbleSpace$isHideAble(world, block, base.east())) {
                        return new Tuple((Object)base, (Object)base.east());
                    }
                }
                if (WorldUtil.getHideAbleSpace$isHideAble(world, block, base.north())) {
                    return new Tuple((Object)base, (Object)base.north());
                }
            } else {
                if (WorldUtil.getHideAbleSpace$isHideAble(world, block, base.north())) {
                    return new Tuple((Object)base, (Object)base.north());
                }
                if (lookAngle.x() > 0.0) {
                    if (WorldUtil.getHideAbleSpace$isHideAble(world, block, base.east())) {
                        return new Tuple((Object)base, (Object)base.east());
                    }
                    if (WorldUtil.getHideAbleSpace$isHideAble(world, block, base.west())) {
                        return new Tuple((Object)base, (Object)base.west());
                    }
                } else {
                    if (WorldUtil.getHideAbleSpace$isHideAble(world, block, base.west())) {
                        return new Tuple((Object)base, (Object)base.west());
                    }
                    if (WorldUtil.getHideAbleSpace$isHideAble(world, block, base.east())) {
                        return new Tuple((Object)base, (Object)base.east());
                    }
                }
                if (WorldUtil.getHideAbleSpace$isHideAble(world, block, base.south())) {
                    return new Tuple((Object)base, (Object)base.south());
                }
            }
        } else if (lookAngle.x() > 0.0) {
            if (WorldUtil.getHideAbleSpace$isHideAble(world, block, base.east())) {
                return new Tuple((Object)base, (Object)base.east());
            }
            if (lookAngle.z() > 0.0) {
                if (WorldUtil.getHideAbleSpace$isHideAble(world, block, base.south())) {
                    return new Tuple((Object)base, (Object)base.south());
                }
                if (WorldUtil.getHideAbleSpace$isHideAble(world, block, base.north())) {
                    return new Tuple((Object)base, (Object)base.north());
                }
            } else {
                if (WorldUtil.getHideAbleSpace$isHideAble(world, block, base.north())) {
                    return new Tuple((Object)base, (Object)base.north());
                }
                if (WorldUtil.getHideAbleSpace$isHideAble(world, block, base.south())) {
                    return new Tuple((Object)base, (Object)base.south());
                }
            }
            if (WorldUtil.getHideAbleSpace$isHideAble(world, block, base.west())) {
                return new Tuple((Object)base, (Object)base.west());
            }
        } else {
            if (WorldUtil.getHideAbleSpace$isHideAble(world, block, base.west())) {
                return new Tuple((Object)base, (Object)base.west());
            }
            if (lookAngle.z() > 0.0) {
                if (WorldUtil.getHideAbleSpace$isHideAble(world, block, base.south())) {
                    return new Tuple((Object)base, (Object)base.south());
                }
                if (WorldUtil.getHideAbleSpace$isHideAble(world, block, base.north())) {
                    return new Tuple((Object)base, (Object)base.north());
                }
            } else {
                if (WorldUtil.getHideAbleSpace$isHideAble(world, block, base.north())) {
                    return new Tuple((Object)base, (Object)base.north());
                }
                if (WorldUtil.getHideAbleSpace$isHideAble(world, block, base.south())) {
                    return new Tuple((Object)base, (Object)base.south());
                }
            }
            if (WorldUtil.getHideAbleSpace$isHideAble(world, block, base.east())) {
                return new Tuple((Object)base, (Object)base.east());
            }
        }
        if (world.getBlockState(base.below()).is(block) && Math.abs(entity.getY() - (double)base.below().getY()) < 0.2) {
            return new Tuple((Object)base.below(), (Object)base);
        }
        return null;
    }
}

