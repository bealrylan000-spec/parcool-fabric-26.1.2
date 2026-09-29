package com.alrex.parcool.common.block.zipline;

import com.alrex.parcool.common.block.zipline.ZiplineHookBlock;
import com.mojang.serialization.MapCodec;
import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class WoodenZiplineHookBlock
extends ZiplineHookBlock {
    protected static final VoxelShape[] SHAPES = new VoxelShape[Direction.values().length];
    private static final MapCodec<WoodenZiplineHookBlock> CODEC;

    @Nonnull
    protected MapCodec<? extends DirectionalBlock> codec() {
        return CODEC;
    }

    public WoodenZiplineHookBlock(BlockBehaviour.Properties p_i48440_1_) {
        super(p_i48440_1_);
    }

    @Nonnull
    public VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter getter, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
        return SHAPES[((Direction)state.getValue((Property)FACING)).ordinal()];
    }

    static {
        WoodenZiplineHookBlock.SHAPES[Direction.UP.ordinal()] = Shapes.or((VoxelShape)Block.box((double)6.0, (double)6.0, (double)6.0, (double)10.0, (double)8.0, (double)10.0), (VoxelShape)Block.box((double)5.0, (double)0.0, (double)5.0, (double)11.0, (double)6.0, (double)11.0));
        WoodenZiplineHookBlock.SHAPES[Direction.DOWN.ordinal()] = Shapes.or((VoxelShape)Block.box((double)6.0, (double)8.0, (double)6.0, (double)10.0, (double)10.0, (double)10.0), (VoxelShape)Block.box((double)5.0, (double)10.0, (double)5.0, (double)11.0, (double)16.0, (double)11.0));
        WoodenZiplineHookBlock.SHAPES[Direction.SOUTH.ordinal()] = Shapes.or((VoxelShape)Block.box((double)6.0, (double)6.0, (double)6.0, (double)10.0, (double)10.0, (double)8.0), (VoxelShape)Block.box((double)5.0, (double)5.0, (double)0.0, (double)11.0, (double)11.0, (double)6.0));
        WoodenZiplineHookBlock.SHAPES[Direction.NORTH.ordinal()] = Shapes.or((VoxelShape)Block.box((double)6.0, (double)6.0, (double)8.0, (double)10.0, (double)10.0, (double)10.0), (VoxelShape)Block.box((double)5.0, (double)5.0, (double)10.0, (double)11.0, (double)11.0, (double)16.0));
        WoodenZiplineHookBlock.SHAPES[Direction.WEST.ordinal()] = Shapes.or((VoxelShape)Block.box((double)8.0, (double)6.0, (double)6.0, (double)8.0, (double)10.0, (double)10.0), (VoxelShape)Block.box((double)10.0, (double)5.0, (double)5.0, (double)16.0, (double)11.0, (double)11.0));
        WoodenZiplineHookBlock.SHAPES[Direction.EAST.ordinal()] = Shapes.or((VoxelShape)Block.box((double)6.0, (double)6.0, (double)6.0, (double)8.0, (double)10.0, (double)10.0), (VoxelShape)Block.box((double)0.0, (double)5.0, (double)5.0, (double)6.0, (double)11.0, (double)11.0));
        CODEC = WoodenZiplineHookBlock.simpleCodec(WoodenZiplineHookBlock::new);
    }
}

