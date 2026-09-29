package com.alrex.parcool.common.block.zipline;

import com.alrex.parcool.api.SoundEvents;
import com.alrex.parcool.common.block.TileEntities;
import com.alrex.parcool.common.block.zipline.ZiplineHookTileEntity;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public abstract class ZiplineHookBlock
extends DirectionalBlock
implements EntityBlock {
    public ZiplineHookBlock(BlockBehaviour.Properties p_i48440_1_) {
        super(p_i48440_1_);
        this.registerDefaultState((BlockState)this.defaultBlockState().setValue((Property)FACING, (Comparable)Direction.UP));
    }

    public Vec3 getActualZiplinePoint(BlockPos pos, BlockState state) {
        return new Vec3((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateBuilder) {
        stateBuilder.add(new Property[]{FACING});
    }

    public PushReaction getPistonPushReaction(BlockState p_149656_1_) {
        return PushReaction.DESTROY;
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction direction = context.getClickedFace();
        return (BlockState)this.defaultBlockState().setValue((Property)FACING, (Comparable)direction);
    }

    public void onRemove(@Nonnull BlockState state, @Nonnull Level world, @Nonnull BlockPos pos, @Nonnull BlockState p_196243_4_, boolean p_196243_5_) {
        BlockEntity tileEntity;
        if (!world.isClientSide() && (tileEntity = world.getBlockEntity(pos)) instanceof ZiplineHookTileEntity) {
            ZiplineHookTileEntity ziplineHookTileEntity = (ZiplineHookTileEntity)tileEntity;
            List<ItemStack> itemStacks = ziplineHookTileEntity.removeAllConnection();
            itemStacks.forEach(it -> Containers.dropItemStack((Level)world, (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), (ItemStack)it));
        }
        super.onRemove(state, world, pos, p_196243_4_, p_196243_5_);
    }

    public BlockState updateShape(BlockState state, Direction direction, BlockState state1, LevelAccessor levelAccessor, BlockPos pos, BlockPos pos1) {
        Direction facing = (Direction)state.getValue((Property)FACING);
        return direction == facing.getOpposite() && !this.canSurvive(state, (LevelReader)levelAccessor, pos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, direction, state1, levelAccessor, pos, pos1);
    }

    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        Direction facing = (Direction)state.getValue((Property)FACING);
        BlockPos supportingBlock = pos.relative(facing.getOpposite());
        return ZiplineHookBlock.canSupportCenter((LevelReader)world, (BlockPos)supportingBlock, (Direction)facing);
    }

    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
    }

    protected ItemInteractionResult useItemOn(ItemStack stack, @Nonnull BlockState state, @Nonnull Level level, @Nonnull BlockPos pos, @Nonnull Player player, @Nonnull InteractionHand hand, @Nonnull BlockHitResult hitResult) {
        BlockEntity tileEntity;
        if (stack.getItem() instanceof ShearsItem && (tileEntity = player.level().getBlockEntity(pos)) instanceof ZiplineHookTileEntity) {
            ZiplineHookTileEntity ziplineHookTileEntity = (ZiplineHookTileEntity)tileEntity;
            if (ziplineHookTileEntity.getConnectionPoints().isEmpty()) {
                return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
            }
            List<ItemStack> itemStacks = ziplineHookTileEntity.removeAllConnection();
            if (!itemStacks.isEmpty()) {
                player.playSound((SoundEvent)SoundEvents.ZIPLINE_REMOVE.get(), 1.0f, 1.0f);
            }
            if (player.level().isClientSide()) {
                return ItemInteractionResult.SUCCESS;
            }
            itemStacks.forEach(it -> Containers.dropItemStack((Level)player.level(), (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), (ItemStack)it));
            if (!itemStacks.isEmpty() && stack.isDamageableItem()) {
                stack.hurtAndBreak(1, (LivingEntity)player, hand == InteractionHand.MAIN_HAND ? EquipmentSlot.MAINHAND : EquipmentSlot.OFFHAND);
            }
            return ItemInteractionResult.CONSUME;
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    @Nullable
    public BlockEntity newBlockEntity(@Nonnull BlockPos blockPos, @Nonnull BlockState blockState) {
        return new ZiplineHookTileEntity((BlockEntityType)TileEntities.ZIPLINE_HOOK.get(), blockPos, blockState);
    }

    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@Nonnull Level level, @Nonnull BlockState state, @Nonnull BlockEntityType<T> type) {
        return type == TileEntities.ZIPLINE_HOOK.get() ? ZiplineHookTileEntity::tick : null;
    }
}

