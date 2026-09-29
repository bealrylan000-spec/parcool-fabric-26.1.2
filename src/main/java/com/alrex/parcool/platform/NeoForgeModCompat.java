package com.alrex.parcool.platform;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;

/**
 * Stand-in for NeoForge's NeoForgeMod (only the SWIM_SPEED attribute, used by FastSwim.java).
 * Neither vanilla nor Fabric API ship a generic swim-speed attribute since vanilla hardcodes
 * swim speed in its movement code - this registers the attribute itself (so the mod compiles and
 * the attribute shows up / is readable/writable), but you'll still need one small mixin that
 * actually multiplies swim velocity by this attribute's value at the point where vanilla computes
 * it. That vanilla method's exact name needs checking against your local decompiled 26.1.2
 * sources - grep the LivingEntity/Player travel() family for where "swimming" speed is applied.
 * Until that mixin is added, FastSwim's speed boost attribute exists but has no gameplay effect.
 */
public final class NeoForgeModCompat {
    private NeoForgeModCompat() {}

    public static final Holder<Attribute> SWIM_SPEED = Registry.registerForHolder(
            BuiltInRegistries.ATTRIBUTE,
            ResourceLocation.withDefaultNamespace("generic.swim_speed"),
            new RangedAttribute("attribute.name.generic.swim_speed", 1.0, 0.0, 1024.0).setSyncable(true)
    );
}
