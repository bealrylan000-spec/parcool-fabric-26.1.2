package com.alrex.parcool.common.potion;

import com.alrex.parcool.api.Effects;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.alchemy.Potion;
import com.alrex.parcool.platform.eventbus.IEventBus;
import com.alrex.parcool.platform.registries.DeferredHolder;
import com.alrex.parcool.platform.registries.DeferredRegister;

public class Potions {
    private static final DeferredRegister<Potion> POTIONS = DeferredRegister.create((ResourceKey)Registries.POTION, (String)"parcool");
    public static final DeferredHolder<Potion, Potion> POOR_ENERGY_DRINK = POTIONS.register("poor_energy_drink", () -> new Potion(new MobEffectInstance[]{new MobEffectInstance(Effects.INEXHAUSTIBLE, 2400), new MobEffectInstance(MobEffects.HUNGER, 100), new MobEffectInstance(MobEffects.POISON, 100)}));
    public static final DeferredHolder<Potion, Potion> ENERGY_DRINK = POTIONS.register("energy_drink", () -> new Potion(new MobEffectInstance[]{new MobEffectInstance(Effects.INEXHAUSTIBLE, 9600)}));

    public static void registerAll(IEventBus modBus) {
        POTIONS.register(modBus);
    }
}

