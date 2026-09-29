package com.alrex.parcool.platform.attachment;

import com.mojang.serialization.Codec;

import java.util.function.Supplier;

/** Minimal replacement for NeoForge's AttachmentType (only what ParCool uses). */
public final class AttachmentType<T> {
    private final String id;
    private final Supplier<T> factory;
    private final Codec<T> codec; // null = not persisted

    private AttachmentType(String id, Supplier<T> factory, Codec<T> codec) {
        this.id = id;
        this.factory = factory;
        this.codec = codec;
    }

    public static <T> AttachmentType<T> of(String id, Supplier<T> factory) {
        return new AttachmentType<>(id, factory, null);
    }

    public static <T> AttachmentType<T> persistent(String id, Supplier<T> factory, Codec<T> codec) {
        return new AttachmentType<>(id, factory, codec);
    }

    public String id() { return id; }
    public T createDefault() { return factory.get(); }
    public Codec<T> codec() { return codec; }
}
