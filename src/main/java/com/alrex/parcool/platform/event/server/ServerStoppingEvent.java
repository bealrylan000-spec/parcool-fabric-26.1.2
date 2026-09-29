package com.alrex.parcool.platform.event.server;

import com.alrex.parcool.platform.eventbus.Event;
import net.minecraft.server.MinecraftServer;

public final class ServerStoppingEvent extends Event {
    private final MinecraftServer server;
    public ServerStoppingEvent(MinecraftServer server) { this.server = server; }
    public MinecraftServer getServer() { return server; }
}
