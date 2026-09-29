package com.alrex.parcool.common.block;

import com.alrex.parcool.common.block.zipline.IronZiplineHookBlock;
import com.alrex.parcool.common.block.zipline.WoodenZiplineHookBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import com.alrex.parcool.platform.eventbus.IEventBus;
import com.alrex.parcool.platform.registries.DeferredHolder;
import com.alrex.parcool.platform.registries.DeferredRegister;

public class Blocks {
    private static final DeferredRegister<Block> REGISTER = DeferredRegister.create((ResourceKey)Registries.BLOCK, (String)"parcool");
    public static final DeferredHolder<Block, Block> WOODEN_ZIPLINE_HOOK = REGISTER.register("wooden_zipline_hook", () -> new WoodenZiplineHookBlock(BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(1.0f, 3.0f).sound(SoundType.WOOD)));
    public static final DeferredHolder<Block, Block> IRON_ZIPLINE_HOOK = REGISTER.register("iron_zipline_hook", () -> new IronZiplineHookBlock(BlockBehaviour.Properties.of().mapColor(MapColor.METAL).strength(1.0f, 3.0f).noCollission().sound(SoundType.CHAIN)));

    public static void registerAll(IEventBus bus) {
        REGISTER.register(bus);
    }
}

