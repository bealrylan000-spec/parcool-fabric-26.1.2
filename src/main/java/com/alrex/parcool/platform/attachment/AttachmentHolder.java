package com.alrex.parcool.platform.attachment;

import net.minecraft.world.entity.Entity;

import java.util.function.Supplier;

/**
 * Static accessors replacing NeoForge's {@code entity.getData(type)} / {@code entity.setData(type, v)}
 * instance methods (Fabric can't add methods to Entity at compile time). The port rewrites
 * {@code player.getData(X)} to {@code AttachmentHolder.getData(player, X)}.
 */
public final class AttachmentHolder {
    private AttachmentHolder() {}

    @SuppressWarnings("unchecked")
    public static <T> T getData(Entity entity, Supplier<AttachmentType<T>> type) {
        AttachmentType<T> t = type.get();
        return (T) ((AttachmentStore) entity).parcool$attachments().computeIfAbsent(t, k -> t.createDefault());
    }

    @SuppressWarnings("unchecked")
    public static <T> void setData(Entity entity, Supplier<AttachmentType<T>> type, Object value) {
        ((AttachmentStore) entity).parcool$attachments().put(type.get(), value);
    }

    public static boolean hasData(Entity entity, Supplier<? extends AttachmentType<?>> type) {
        return ((AttachmentStore) entity).parcool$attachments().containsKey(type.get());
    }
}
