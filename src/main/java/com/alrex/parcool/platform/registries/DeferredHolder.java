package com.alrex.parcool.platform.registries;

import net.minecraft.core.Holder;

import java.util.function.Supplier;

/**
 * Stand-in for NeoForge's DeferredHolder: both a vanilla {@link Holder} (so it can be passed to
 * APIs like {@code Player.getAttribute(Holder<Attribute>)}) and a {@link Supplier} of the value.
 * Instances are created by {@link DeferredRegister} as a dynamic proxy that forwards every Holder
 * method to the real registry holder, so this stays valid across Holder API changes.
 */
public interface DeferredHolder<R, T extends R> extends Holder<R>, Supplier<T> {
}
