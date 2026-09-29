package com.alrex.parcool.platform.eventbus;

/**
 * Stand-in for {@code net.neoforged.neoforge.common.NeoForge}. Only the {@code EVENT_BUS} field
 * is used by the original mod (as {@code NeoForge.EVENT_BUS.post(...)} /
 * {@code NeoForge.EVENT_BUS.addListener(...)}).
 */
public final class NeoForge {
    public static final IEventBus EVENT_BUS = new EventBus();

    private NeoForge() {}
}
