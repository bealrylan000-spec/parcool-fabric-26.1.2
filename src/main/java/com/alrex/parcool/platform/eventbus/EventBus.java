package com.alrex.parcool.platform.eventbus;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Minimal reflection-based re-implementation of NeoForge's event bus, just wide enough to run
 * ParCool's own {@code @SubscribeEvent} methods and {@code addListener} calls. Listeners for a
 * given event class also receive posts of any subclass registered under it? No: NeoForge (and
 * this shim) key strictly by the exact declared parameter type, matching how ParCool's own
 * events (ParCoolActionEvent.*, etc.) are declared and consumed one concrete class at a time.
 */
public final class EventBus implements IEventBus {

    private final Map<Class<?>, List<Consumer<Object>>> listeners = new ConcurrentHashMap<>();

    @Override
    @SuppressWarnings("unchecked")
    public <T extends Event> void addListener(Class<T> eventType, Consumer<T> listener) {
        listeners.computeIfAbsent(eventType, k -> new ArrayList<>())
                .add((Consumer<Object>) listener);
    }

    @Override
    public void register(Object target) {
        Class<?> clazz = (target instanceof Class<?>) ? (Class<?>) target : target.getClass();
        Object instance = (target instanceof Class<?>) ? null : target;

        for (Method method : clazz.getDeclaredMethods()) {
            if (!method.isAnnotationPresent(SubscribeEvent.class)) continue;
            if (method.getParameterCount() != 1) continue;
            Class<?> paramType = method.getParameterTypes()[0];
            if (!Event.class.isAssignableFrom(paramType)) continue;

            boolean isStatic = Modifier.isStatic(method.getModifiers());
            if (!isStatic && instance == null) {
                // Class was passed to register() but the method is an instance method - NeoForge
                // requires an instance in this case too, so skip (mirrors real bus behaviour).
                continue;
            }
            method.setAccessible(true);
            final Object receiver = isStatic ? null : instance;
            listeners.computeIfAbsent(paramType, k -> new ArrayList<>())
                    .add(event -> {
                        try {
                            method.invoke(receiver, event);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            throw new RuntimeException(
                                    "ParCool platform EventBus: failed to invoke " + method, e);
                        }
                    });
        }
    }

    @Override
    public <T extends Event> T post(T event) {
        List<Consumer<Object>> subs = listeners.get(event.getClass());
        if (subs != null) {
            for (Consumer<Object> c : subs) {
                c.accept(event);
            }
        }
        return event;
    }
}
