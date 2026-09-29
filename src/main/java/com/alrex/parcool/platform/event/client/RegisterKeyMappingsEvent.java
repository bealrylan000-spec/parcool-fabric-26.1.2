package com.alrex.parcool.platform.event.client;

import com.alrex.parcool.platform.eventbus.Event;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;

/** Stand-in for {@code net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent}. */
public final class RegisterKeyMappingsEvent extends Event {
    public void register(KeyMapping mapping) {
        KeyMappingHelper.registerKeyMapping(mapping);
    }
}
