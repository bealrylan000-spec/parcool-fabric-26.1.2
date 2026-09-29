package com.alrex.parcool.platform.registries;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * Drop-in replacement for NeoForge's DeferredRegister, backed by vanilla {@link Registry}.
 * Entries are registered when {@link #register(Object)} is called (ParCool's XXX.registerAll(bus)
 * methods do this once from the entrypoint) or, as a safety net, on the first get().
 */
public class DeferredRegister<T> {
    protected final ResourceKey<? extends Registry<T>> registryKey;
    protected final String namespace;
    private final List<Entry<?>> entries = new ArrayList<>();

    protected DeferredRegister(ResourceKey<? extends Registry<T>> registryKey, String namespace) {
        this.registryKey = registryKey;
        this.namespace = namespace;
    }

    public static <T> DeferredRegister<T> create(ResourceKey<? extends Registry<T>> registryKey, String namespace) {
        return new DeferredRegister<>(registryKey, namespace);
    }

    public static DataComponents createDataComponents(ResourceKey<? extends Registry<DataComponentType<?>>> key, String namespace) {
        return new DataComponents(key, namespace);
    }

    @SuppressWarnings("unchecked")
    public <I extends T> DeferredHolder<T, I> register(String name, Supplier<? extends I> supplier) {
        Entry<I> entry = new Entry<>(Identifier.fromNamespaceAndPath(namespace, name), supplier);
        entries.add(entry);
        return (DeferredHolder<T, I>) Proxy.newProxyInstance(
                DeferredHolder.class.getClassLoader(),
                new Class<?>[]{DeferredHolder.class},
                (proxy, method, args) -> {
                    if (method.getName().equals("get") && method.getParameterCount() == 0) {
                        return entry.value();
                    }
                    if (method.getDeclaringClass() == Object.class) {
                        return switch (method.getName()) {
                            case "hashCode" -> System.identityHashCode(proxy);
                            case "equals" -> proxy == args[0];
                            default -> "DeferredHolder[" + entry.id + "]";
                        };
                    }
                    try {
                        return method.invoke(entry.holder(), args);
                    } catch (java.lang.reflect.InvocationTargetException e) {
                        throw e.getCause();
                    }
                });
    }

    /** Registers everything declared so far. The argument (NeoForge's mod event bus) is ignored. */
    public void register(Object ignoredBus) {
        for (Entry<?> e : entries) e.ensureRegistered();
    }

    /**
     * Maps each ResourceKey<Registry<?>> constant ParCool actually uses to its concrete
     * BuiltInRegistries field. Hardcoded rather than looked up dynamically via a "registry of
     * registries" API, because vanilla's exact method name for that (get/getValue/getOptional)
     * has shifted across versions and isn't worth guessing when the twelve keys ParCool needs are
     * known up front and BuiltInRegistries' own field names have been stable for years.
     */
    @SuppressWarnings("unchecked")
    private Registry<T> registry() {
        Registry<?> r;
        if (registryKey == net.minecraft.core.registries.Registries.BLOCK) r = BuiltInRegistries.BLOCK;
        else if (registryKey == net.minecraft.core.registries.Registries.ITEM) r = BuiltInRegistries.ITEM;
        else if (registryKey == net.minecraft.core.registries.Registries.ATTRIBUTE) r = BuiltInRegistries.ATTRIBUTE;
        else if (registryKey == net.minecraft.core.registries.Registries.MOB_EFFECT) r = BuiltInRegistries.MOB_EFFECT;
        else if (registryKey == net.minecraft.core.registries.Registries.POTION) r = BuiltInRegistries.POTION;
        else if (registryKey == net.minecraft.core.registries.Registries.SOUND_EVENT) r = BuiltInRegistries.SOUND_EVENT;
        else if (registryKey == net.minecraft.core.registries.Registries.ENTITY_TYPE) r = BuiltInRegistries.ENTITY_TYPE;
        else if (registryKey == net.minecraft.core.registries.Registries.BLOCK_ENTITY_TYPE) r = BuiltInRegistries.BLOCK_ENTITY_TYPE;
        else if (registryKey == net.minecraft.core.registries.Registries.DATA_COMPONENT_TYPE) r = BuiltInRegistries.DATA_COMPONENT_TYPE;
        else if (registryKey == net.minecraft.core.registries.Registries.RECIPE_SERIALIZER) r = BuiltInRegistries.RECIPE_SERIALIZER;
        else if (registryKey == net.minecraft.core.registries.Registries.CREATIVE_MODE_TAB) r = BuiltInRegistries.CREATIVE_MODE_TAB;
        else if (registryKey == net.minecraft.core.registries.Registries.COMMAND_ARGUMENT_TYPE) r = BuiltInRegistries.COMMAND_ARGUMENT_TYPE;
        else throw new IllegalStateException("ParCool platform DeferredRegister: unmapped registry key " + registryKey
                + " - add it to DeferredRegister.registry().");
        return (Registry<T>) r;
    }

    private final class Entry<I extends T> {
        final Identifier id;
        final Supplier<? extends I> supplier;
        Holder.Reference<T> holder;
        I value;

        Entry(Identifier id, Supplier<? extends I> supplier) {
            this.id = id;
            this.supplier = supplier;
        }

        void ensureRegistered() {
            if (holder != null) return;
            I built = supplier.get();
            holder = Registry.registerForHolder(registry(), id, built);
            value = built;
        }

        I value() { ensureRegistered(); return value; }
        Holder.Reference<T> holder() { ensureRegistered(); return holder; }
    }

    /** NeoForge's DeferredRegister.DataComponents helper. */
    public static class DataComponents extends DeferredRegister<DataComponentType<?>> {
        protected DataComponents(ResourceKey<? extends Registry<DataComponentType<?>>> key, String ns) {
            super(key, ns);
        }

        public <V> DeferredHolder<DataComponentType<?>, DataComponentType<V>> registerComponentType(
                String name, UnaryOperator<DataComponentType.Builder<V>> builder) {
            return register(name, () -> builder.apply(DataComponentType.builder()).build());
        }
    }
}
