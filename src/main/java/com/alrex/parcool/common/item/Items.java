package com.alrex.parcool.common.item;

import com.alrex.parcool.common.block.Blocks;
import com.alrex.parcool.common.item.zipline.ZiplineRopeItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import com.alrex.parcool.platform.dist.Dist;
import com.alrex.parcool.platform.dist.OnlyIn;
import com.alrex.parcool.platform.eventbus.IEventBus;
import com.alrex.parcool.platform.event.lifecycle.FMLClientSetupEvent;
import com.alrex.parcool.platform.registries.DeferredHolder;
import com.alrex.parcool.platform.registries.DeferredRegister;

public class Items {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create((ResourceKey)Registries.ITEM, (String)"parcool");
    public static final DeferredHolder<Item, Item> PARCOOL_GUIDE = ITEMS.register("parcool_guide", () -> new Item(new Item.Properties().stacksTo(1)));
    public static final DeferredHolder<Item, Item> WOODEN_ZIPLINE_HOOK = ITEMS.register("wooden_zipline_hook", () -> new BlockItem((Block)Blocks.WOODEN_ZIPLINE_HOOK.get(), new Item.Properties()));
    public static final DeferredHolder<Item, Item> IRON_ZIPLINE_HOOK = ITEMS.register("iron_zipline_hook", () -> new BlockItem((Block)Blocks.IRON_ZIPLINE_HOOK.get(), new Item.Properties()));
    public static final DeferredHolder<Item, Item> ZIPLINE_ROPE = ITEMS.register("zipline_rope", () -> new ZiplineRopeItem(new Item.Properties()));

    public static void registerAll(IEventBus modBus) {
        ITEMS.register(modBus);
    }

    @OnlyIn(value=Dist.CLIENT)
    public static void registerColors(FMLClientSetupEvent event) {
        ItemLike[] itemLikeArray = new ItemLike[1];
        itemLikeArray[0] = () -> ZIPLINE_ROPE.get();
        Minecraft.getInstance().getItemColors().register((ItemColor)new ZiplineRopeItem.RopeColor(), itemLikeArray);
    }
}

