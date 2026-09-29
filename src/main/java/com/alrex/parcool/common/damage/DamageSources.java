package com.alrex.parcool.common.damage;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;

public class DamageSources {
    public static final ResourceKey<DamageType> WALL_SLIDE = DamageSources.register("parcool.wall_slide");

    private static ResourceKey<DamageType> register(String name) {
        return ResourceKey.create((ResourceKey)Registries.DAMAGE_TYPE, (ResourceLocation)ResourceLocation.fromNamespaceAndPath((String)"parcool", (String)name));
    }
}

