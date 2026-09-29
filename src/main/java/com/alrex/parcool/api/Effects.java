package com.alrex.parcool.api;

import com.alrex.parcool.common.potion.effects.InexhaustibleEffect;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffect;
import com.alrex.parcool.platform.eventbus.IEventBus;
import com.alrex.parcool.platform.registries.DeferredHolder;
import com.alrex.parcool.platform.registries.DeferredRegister;

public class Effects {
    private static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create((ResourceKey)Registries.MOB_EFFECT, (String)"parcool");
    public static final DeferredHolder<MobEffect, MobEffect> INEXHAUSTIBLE = EFFECTS.register("inexhaustible", InexhaustibleEffect::new);

    public static void registerAll(IEventBus modBus) {
        EFFECTS.register(modBus);
    }
}

