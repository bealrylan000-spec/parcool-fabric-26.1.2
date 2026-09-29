package com.alrex.parcool.api;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import com.alrex.parcool.platform.eventbus.IEventBus;
import com.alrex.parcool.platform.registries.DeferredHolder;
import com.alrex.parcool.platform.registries.DeferredRegister;

public class Attributes {
    private static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create((ResourceKey)Registries.ATTRIBUTE, (String)"parcool");
    public static final DeferredHolder<Attribute, Attribute> MAX_STAMINA = ATTRIBUTES.register("max_stamina", () -> new RangedAttribute("parcool.max_stamina", 2000.0, 10.0, 10000.0).setSyncable(true));
    public static final DeferredHolder<Attribute, Attribute> STAMINA_RECOVERY = ATTRIBUTES.register("stamina_recovery", () -> new RangedAttribute("parcool.stamina_recovery", 20.0, 0.0, 10000.0).setSyncable(true));

    public static void registerAll(IEventBus bus) {
        ATTRIBUTES.register(bus);
    }
}

