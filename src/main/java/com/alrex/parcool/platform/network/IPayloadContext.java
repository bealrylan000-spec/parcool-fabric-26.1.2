package com.alrex.parcool.platform.network;

import net.minecraft.world.entity.player.Player;

/** Stand-in for NeoForge's IPayloadContext - just enough for ParCool's payload handlers. */
public interface IPayloadContext {
    Player player();
    void enqueueWork(Runnable task);
}
