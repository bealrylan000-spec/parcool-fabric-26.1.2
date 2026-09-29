package com.alrex.parcool.platform.event.client;

import com.alrex.parcool.platform.eventbus.Event;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;

/** Stand-in for {@code net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent}. */
public final class RegisterKeyMappingsEvent extends Event {
    public void register(KeyMapping mapping) {
        KeyBindingHelper.registerKeyBinding(mapping);
    }
}
