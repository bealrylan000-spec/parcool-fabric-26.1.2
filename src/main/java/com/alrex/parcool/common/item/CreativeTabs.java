package com.alrex.parcool.common.item;

import com.alrex.parcool.common.item.Items;
import com.alrex.parcool.common.item.zipline.ZiplineRopeItem;
import java.util.Arrays;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import com.alrex.parcool.platform.eventbus.IEventBus;
import com.alrex.parcool.platform.registries.DeferredHolder;
import com.alrex.parcool.platform.registries.DeferredRegister;

public class CreativeTabs {
    private static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create((ResourceKey)Registries.CREATIVE_MODE_TAB, (String)"parcool");
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> ITEMS = TABS.register("items", () -> CreativeModeTab.builder().icon(() -> new ItemStack((ItemLike)Items.PARCOOL_GUIDE.get())).title((Component)Component.translatable((String)"itemGroup.ParCool")).hideTitle().displayItems((params, output) -> {
        output.accept((ItemLike)Items.IRON_ZIPLINE_HOOK.get());
        output.accept((ItemLike)Items.WOODEN_ZIPLINE_HOOK.get());
        output.accept((ItemLike)Items.ZIPLINE_ROPE.get());
        Arrays.stream(DyeColor.values()).map(color -> {
            ItemStack coloredRope = new ItemStack((ItemLike)Items.ZIPLINE_ROPE.get());
            ZiplineRopeItem.setColor(coloredRope, color.getTextureDiffuseColor());
            return coloredRope;
        }).forEach(arg_0 -> ((CreativeModeTab.Output)output).accept(arg_0));
    }).build());

    public static void registerAll(IEventBus bus) {
        TABS.register(bus);
    }
}

