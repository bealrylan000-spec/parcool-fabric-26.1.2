package com.alrex.parcool.common.block.zipline;

import com.alrex.parcool.common.block.BlockStateProperties;
import com.alrex.parcool.common.block.zipline.ZiplineHookBlock;
import com.mojang.serialization.MapCodec;
import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class IronZiplineHookBlock
extends ZiplineHookBlock {
    public static final BooleanProperty ORTHOGONAL = BlockStateProperties.ORTHOGONAL;
    protected static final VoxelShape[] SHAPES = new VoxelShape[Direction.values().length];
    private static final MapCodec<IronZiplineHookBlock> CODEC;

    public VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter getter, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
        return SHAPES[((Direction)state.getValue((Property)FACING)).ordinal()];
    }

    @Nonnull
    protected MapCodec<? extends DirectionalBlock> codec() {
        return CODEC;
    }

    public IronZiplineHookBlock(BlockBehaviour.Properties p_i48440_1_) {
        super(p_i48440_1_);
        this.registerDefaultState((BlockState)this.defaultBlockState().setValue((Property)ORTHOGONAL, (Comparable)Boolean.valueOf(false)));
    }

    @Override
    public Vec3 getActualZiplinePoint(BlockPos pos, BlockState state) {
        Direction direction = (Direction)state.getValue((Property)FACING);
        return new Vec3((double)pos.getX() + 0.5 - (double)direction.getStepX() * 0.2, (double)pos.getY() + 0.5 - (double)direction.getStepY() * 0.2, (double)pos.getZ() + 0.5 - (double)direction.getStepZ() * 0.2);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        super.createBlockStateDefinition(stateBuilder);
        stateBuilder.add(new Property[]{ORTHOGONAL});
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        boolean orthogonal;
        BlockState state = super.getStateForPlacement(context);
        Player player = context.getPlayer();
        if (player == null) {
            orthogonal = false;
        } else {
            Vec3 lookVec = player.getLookAngle();
            switch (context.getClickedFace()) {
                case NORTH: 
                case SOUTH: {
                    orthogonal = Math.abs(lookVec.y()) < Math.abs(lookVec.x());
                    break;
                }
                case EAST: 
                case WEST: {
                    orthogonal = Math.abs(lookVec.y()) < Math.abs(lookVec.z());
                    break;
                }
                default: {
                    orthogonal = Math.abs(lookVec.z()) < Math.abs(lookVec.x());
                }
            }
        }
        return (BlockState)state.setValue((Property)ORTHOGONAL, (Comparable)Boolean.valueOf(orthogonal));
    }

    static {
        IronZiplineHookBlock.SHAPES[Direction.DOWN.ordinal()] = Block.box((double)6.0, (double)11.0, (double)6.0, (double)10.0, (double)16.0, (double)10.0);
        IronZiplineHookBlock.SHAPES[Direction.UP.ordinal()] = Block.box((double)6.0, (double)0.0, (double)6.0, (double)10.0, (double)5.0, (double)10.0);
        IronZiplineHookBlock.SHAPES[Direction.SOUTH.ordinal()] = Block.box((double)6.0, (double)6.0, (double)0.0, (double)10.0, (double)10.0, (double)5.0);
        IronZiplineHookBlock.SHAPES[Direction.NORTH.ordinal()] = Block.box((double)6.0, (double)6.0, (double)11.0, (double)10.0, (double)10.0, (double)16.0);
        IronZiplineHookBlock.SHAPES[Direction.WEST.ordinal()] = Block.box((double)11.0, (double)6.0, (double)6.0, (double)16.0, (double)10.0, (double)10.0);
        IronZiplineHookBlock.SHAPES[Direction.EAST.ordinal()] = Block.box((double)0.0, (double)6.0, (double)6.0, (double)5.0, (double)10.0, (double)10.0);
        CODEC = IronZiplineHookBlock.simpleCodec(IronZiplineHookBlock::new);
    }
}

