package com.alrex.parcool.platform.event.tick;

import com.alrex.parcool.platform.eventbus.Event;

/** Stand-in for {@code net.neoforged.neoforge.client.event.ClientTickEvent}. */
public abstract class ClientTickEvent extends Event {
    public static final class Pre extends ClientTickEvent {}
    public static final class Post extends ClientTickEvent {}
}
