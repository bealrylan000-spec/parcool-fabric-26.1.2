package com.alrex.parcool.platform.eventbus;

import java.util.function.Consumer;

/**
 * Stand-in for {@code net.neoforged.bus.api.IEventBus}.
 *
 * <p>Difference from the real NeoForge interface: {@link #addListener} takes an explicit
 * {@code Class<T>} token. NeoForge's real bus infers T from the lambda/method-reference's
 * generic signature via bytecode inspection (the "TypeTools" trick) so it can be called with
 * just a bare method reference; reproducing that reliably without an extra library isn't worth
 * it here since, across the whole ParCool codebase, {@code addListener} is only ever called from
 * two files (the main entrypoint and EventBusForgeRegistry) - both of which are hand-ported in
 * this project and already updated to pass the event class explicitly.
 */
public interface IEventBus {
    <T extends Event> void addListener(Class<T> eventType, Consumer<T> listener);

    /**
     * Scans {@code target} (a Class for a static-only registration, or an instance for one that
     * also has instance {@code @SubscribeEvent} methods) for methods annotated
     * {@link SubscribeEvent} and wires each one up as a listener for its single parameter type.
     */
    void register(Object target);

    <T extends Event> T post(T event);
}
