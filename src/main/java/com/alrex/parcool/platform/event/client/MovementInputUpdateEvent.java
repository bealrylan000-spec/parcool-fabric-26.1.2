package com.alrex.parcool.platform.event.client;

import com.alrex.parcool.platform.eventbus.Event;

/**
 * Stand-in for {@code net.neoforged.neoforge.client.event.MovementInputUpdateEvent}. ParCool's
 * KeyRecorder only uses this as a "some client input got processed this tick" pulse, reading all
 * of its state from Minecraft.getInstance() itself - so no fields are needed here, it's just a
 * trigger fired every client tick alongside ClientTickEvent (see ParCoolFabricClient).
 */
public final class MovementInputUpdateEvent extends Event {
}
