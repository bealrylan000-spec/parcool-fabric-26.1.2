package com.alrex.parcool.common.item;

import com.alrex.parcool.common.item.component.ZiplineColorComponent;
import com.alrex.parcool.common.item.component.ZiplinePositionComponent;
import com.alrex.parcool.common.item.component.ZiplineTensionComponent;
import java.util.function.Supplier;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import com.alrex.parcool.platform.eventbus.IEventBus;
import com.alrex.parcool.platform.registries.DeferredRegister;

public class DataComponents {
    private static final DeferredRegister.DataComponents COMPONENTS = DeferredRegister.createDataComponents((ResourceKey)Registries.DATA_COMPONENT_TYPE, (String)"parcool");
    public static final Supplier<DataComponentType<ZiplineColorComponent>> ZIPLINE_COLOR = COMPONENTS.registerComponentType("zipline_color", builder -> builder.persistent(ZiplineColorComponent.CODEC).networkSynchronized(ZiplineColorComponent.STREAM_CODEC));
    public static final Supplier<DataComponentType<ZiplinePositionComponent>> ZIPLINE_POSITION = COMPONENTS.registerComponentType("zipline_pos", builder -> builder.persistent(ZiplinePositionComponent.CODEC).networkSynchronized(ZiplinePositionComponent.STREAM_CODEC));
    public static final Supplier<DataComponentType<ZiplineTensionComponent>> ZIPLINE_TENSION = COMPONENTS.registerComponentType("zipline_tension", builder -> builder.persistent(ZiplineTensionComponent.CODEC).networkSynchronized(ZiplineTensionComponent.STREAM_CODEC));

    public static void registerAll(IEventBus bus) {
        COMPONENTS.register(bus);
    }
}

