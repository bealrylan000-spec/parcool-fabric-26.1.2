package com.alrex.parcool.platform.event.tick;

import com.alrex.parcool.platform.eventbus.Event;
import net.minecraft.server.MinecraftServer;

/** Stand-in for {@code net.neoforged.neoforge.event.tick.ServerTickEvent}. */
public abstract class ServerTickEvent extends Event {
    private final MinecraftServer server;

    protected ServerTickEvent(MinecraftServer server) {
        this.server = server;
    }

    public MinecraftServer getServer() {
        return server;
    }

    public static final class Pre extends ServerTickEvent {
        public Pre(MinecraftServer server) { super(server); }
    }

    public static final class Post extends ServerTickEvent {
        public Post(MinecraftServer server) { super(server); }
    }
}
