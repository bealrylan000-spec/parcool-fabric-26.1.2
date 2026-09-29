package com.alrex.parcool.common.block;

import com.alrex.parcool.common.block.Blocks;
import com.alrex.parcool.common.block.zipline.ZiplineHookTileEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import com.alrex.parcool.platform.eventbus.IEventBus;
import com.alrex.parcool.platform.registries.DeferredHolder;
import com.alrex.parcool.platform.registries.DeferredRegister;

public class TileEntities {
    private static final DeferredRegister<BlockEntityType<?>> REGISTER = DeferredRegister.create((ResourceKey)Registries.BLOCK_ENTITY_TYPE, (String)"parcool");
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ZiplineHookTileEntity>> ZIPLINE_HOOK = REGISTER.register("zipline_hook", () -> BlockEntityType.Builder.of((pos, state) -> new ZiplineHookTileEntity((BlockEntityType)ZIPLINE_HOOK.get(), pos, state), (Block[])new Block[]{(Block)Blocks.WOODEN_ZIPLINE_HOOK.get(), (Block)Blocks.IRON_ZIPLINE_HOOK.get()}).build(null));

    public static void registerAll(IEventBus bus) {
        REGISTER.register(bus);
    }
}

