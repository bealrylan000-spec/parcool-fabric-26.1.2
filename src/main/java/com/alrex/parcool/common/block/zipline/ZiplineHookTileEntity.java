package com.alrex.parcool.common.block.zipline;

import com.alrex.parcool.common.block.zipline.ZiplineHookBlock;
import com.alrex.parcool.common.block.zipline.ZiplineInfo;
import com.alrex.parcool.common.entity.zipline.ZiplineRopeEntity;
import com.alrex.parcool.common.item.Items;
import com.alrex.parcool.common.item.zipline.ZiplineRopeItem;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.FastColor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class ZiplineHookTileEntity
extends BlockEntity {
    private final TreeMap<BlockPos, ZiplineInfo> connections = new TreeMap();
    private final TreeMap<BlockPos, ZiplineRopeEntity> connectionEntities = new TreeMap();

    public ZiplineHookTileEntity(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
        super(p_155228_, p_155229_, p_155230_);
    }

    public Set<BlockPos> getConnectionPoints() {
        return this.connections.keySet();
    }

    private TreeMap<BlockPos, ZiplineInfo> getConnectionInfo() {
        return this.connections;
    }

    public List<ItemStack> removeAllConnection() {
        if (this.level == null) {
            return Collections.EMPTY_LIST;
        }
        this.getConnectionPoints().stream().filter(arg_0 -> ((Level)this.level).isLoaded(arg_0)).map(arg_0 -> ((Level)this.level).getBlockEntity(arg_0)).map(it -> it instanceof ZiplineHookTileEntity ? (ZiplineHookTileEntity)((Object)it) : null).filter(Objects::nonNull).forEach(it -> it.onPairHookRegistrationRemoved(this));
        List itemStacks = Collections.EMPTY_LIST;
        if (!this.level.isClientSide()) {
            this.connectionEntities.values().forEach(it -> it.remove(Entity.RemovalReason.DISCARDED));
            itemStacks = this.getConnectionInfo().values().stream().map(it -> {
                ItemStack stack = new ItemStack(() -> Items.ZIPLINE_ROPE.get());
                ZiplineRopeItem.setColor(stack, FastColor.ARGB32.color((int)255, (int)it.getColor()));
                return stack;
            }).collect(Collectors.toList());
        }
        this.connectionEntities.clear();
        this.getConnectionInfo().clear();
        this.setChanged();
        return itemStacks;
    }

    private void onPairHookRegistrationRemoved(ZiplineHookTileEntity removedPair) {
        this.getConnectionPoints().remove(removedPair.getBlockPos());
        this.connectionEntities.remove(removedPair.getBlockPos());
        this.setChanged();
    }

    private void onPairHookUnloaded(ZiplineHookTileEntity removedPair) {
        this.connectionEntities.remove(removedPair.getBlockPos());
    }

    public void onChunkUnloaded() {
        super.onChunkUnloaded();
        if (this.level != null) {
            this.getConnectionPoints().stream().filter(arg_0 -> ((Level)this.level).isLoaded(arg_0)).map(arg_0 -> ((Level)this.level).getBlockEntity(arg_0)).map(it -> it instanceof ZiplineHookTileEntity ? (ZiplineHookTileEntity)((Object)it) : null).filter(Objects::nonNull).forEach(it -> it.onPairHookUnloaded(this));
            if (!this.level.isClientSide()) {
                this.connectionEntities.values().forEach(it -> it.remove(Entity.RemovalReason.DISCARDED));
            }
            this.connectionEntities.clear();
        }
    }

    public Vec3 getActualZiplinePoint(@Nullable BlockPos connected) {
        BlockState state;
        Block block;
        if (this.level == null) {
            new Vec3((double)this.getBlockPos().getX() + 0.5, (double)this.getBlockPos().getY() + 0.5, (double)this.getBlockPos().getZ() + 0.5);
        }
        if ((block = (state = this.level.getBlockState(this.getBlockPos())).getBlock()) instanceof ZiplineHookBlock) {
            return ((ZiplineHookBlock)block).getActualZiplinePoint(this.getBlockPos(), state);
        }
        return new Vec3((double)this.getBlockPos().getX() + 0.5, (double)this.getBlockPos().getY() + 0.5, (double)this.getBlockPos().getZ() + 0.5);
    }

    public boolean connectTo(ZiplineHookTileEntity target, ZiplineInfo info) {
        if (this == target) {
            return false;
        }
        if (this.level != null && !this.level.isClientSide()) {
            if (this.getConnectionPoints().stream().anyMatch(arg_0 -> ((BlockPos)target.getBlockPos()).equals(arg_0))) {
                return false;
            }
            ZiplineRopeEntity ropeEntity = this.spawnRope(this.level, target, info);
            if (ropeEntity != null) {
                this.getConnectionInfo().put(target.getBlockPos(), info);
                this.setChanged();
                target.getConnectionInfo().put(this.getBlockPos(), info);
                target.setChanged();
                return true;
            }
        }
        return false;
    }

    @Nullable
    private ZiplineRopeEntity spawnRope(Level level, ZiplineHookTileEntity target, ZiplineInfo info) {
        if (level.isClientSide()) {
            return null;
        }
        if (target.connectionEntities.containsKey(this.getBlockPos())) {
            return null;
        }
        ZiplineRopeEntity entity = new ZiplineRopeEntity(level, this.getBlockPos(), target.getBlockPos(), info);
        boolean result = level.addFreshEntity((Entity)entity);
        if (result) {
            this.connectionEntities.put(target.getBlockPos(), entity);
            target.connectionEntities.put(this.getBlockPos(), entity);
        }
        return result ? entity : null;
    }

    private void saveTo(CompoundTag nbt) {
        ListTag connections = new ListTag();
        for (Map.Entry<BlockPos, ZiplineInfo> infoEntry : this.getConnectionInfo().entrySet()) {
            CompoundTag entryTag = new CompoundTag();
            BlockPos pos = this.getBlockPos();
            entryTag.putInt("rX", infoEntry.getKey().getX() - pos.getX());
            entryTag.putInt("rY", infoEntry.getKey().getY() - pos.getY());
            entryTag.putInt("rZ", infoEntry.getKey().getZ() - pos.getZ());
            entryTag.put("Info", infoEntry.getValue().save());
            connections.add((Object)entryTag);
        }
        nbt.put("Connection", (Tag)connections);
    }

    private void restoreFrom(CompoundTag nbt) {
        Tag connections = nbt.get("Connection");
        if (!(connections instanceof ListTag)) {
            return;
        }
        ListTag listConnections = (ListTag)connections;
        this.getConnectionInfo().clear();
        for (Tag entry : listConnections) {
            BlockPos pos;
            if (!(entry instanceof CompoundTag)) continue;
            CompoundTag cTag = (CompoundTag)entry;
            if (cTag.contains("rX") && cTag.contains("rY") && cTag.contains("rZ")) {
                pos = this.getBlockPos().offset(cTag.getInt("rX"), cTag.getInt("rY"), cTag.getInt("rZ"));
            } else {
                if (!cTag.contains("X") || !cTag.contains("Y") || !cTag.contains("Z")) continue;
                pos = new BlockPos(cTag.getInt("X"), cTag.getInt("Y"), cTag.getInt("Z"));
            }
            ZiplineInfo info = ZiplineInfo.load(cTag.get("Info"));
            this.getConnectionInfo().put(pos, info);
        }
    }

    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        this.saveTo(tag);
    }

    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        this.restoreFrom(tag);
    }

    @Nonnull
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag nbt = super.getUpdateTag(registries);
        this.saveTo(nbt);
        return nbt;
    }

    public void handleUpdateTag(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        super.handleUpdateTag(tag, lookupProvider);
        this.restoreFrom(tag);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, BlockEntity entity) {
        if (!(entity instanceof ZiplineHookTileEntity)) {
            return;
        }
        ZiplineHookTileEntity self = (ZiplineHookTileEntity)entity;
        if (level != null && !level.isClientSide()) {
            self.connectionEntities.values().removeIf(it -> !it.isAlive());
            if (self.connectionEntities.size() < self.getConnectionPoints().size()) {
                List<ZiplineHookTileEntity> tileEntities = self.getConnectionPoints().stream().filter(it -> !self.connectionEntities.containsKey(it)).filter(arg_0 -> ((Level)level).isLoaded(arg_0)).map(arg_0 -> ((Level)level).getBlockEntity(arg_0)).map(it -> it instanceof ZiplineHookTileEntity ? (ZiplineHookTileEntity)((Object)it) : null).filter(Objects::nonNull).toList();
                tileEntities.forEach(it -> {
                    if (it.getConnectionPoints().contains(self.getBlockPos())) {
                        self.spawnRope(level, (ZiplineHookTileEntity)((Object)it), self.getConnectionInfo().get(it.getBlockPos()));
                    } else {
                        self.getConnectionPoints().remove(it.getBlockPos());
                        self.setChanged();
                    }
                });
            }
        }
    }
}

