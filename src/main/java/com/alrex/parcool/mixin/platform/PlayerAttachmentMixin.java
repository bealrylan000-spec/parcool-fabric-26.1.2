package com.alrex.parcool.mixin.platform;

import com.alrex.parcool.common.attachment.Attachments;
import com.alrex.parcool.platform.attachment.AttachmentStore;
import com.alrex.parcool.platform.attachment.AttachmentType;
import com.mojang.serialization.DataResult;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * Adds NeoForge-style "attachment" storage to Player. Every attachment ParCool uses is plain
 * runtime state EXCEPT the stamina attachment (Attachments.STAMINA), which NeoForge persists to
 * the entity's saved NBT - reproduced here via readAdditionalSaveData/addAdditionalSaveData.
 *
 * NOTE: verify these two method names against your local, mapped 26.1.2 sources if the build
 * fails here - they've been stable vanilla method names for a very long time, but Mojang's naming
 * can still shift between major versions.
 */
@Mixin(Player.class)
public abstract class PlayerAttachmentMixin implements AttachmentStore {

    @Unique
    private final Map<AttachmentType<?>, Object> parcool$attachments = new HashMap<>();

    @Override
    public Map<AttachmentType<?>, Object> parcool$attachments() {
        return parcool$attachments;
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void parcool$saveStamina(CompoundTag tag, CallbackInfo ci) {
        AttachmentType<?> type = Attachments.staminaType();
        Object value = parcool$attachments.get(type);
        if (value == null) return;
        @SuppressWarnings("unchecked")
        var codec = (com.mojang.serialization.Codec<Object>) type.codec();
        DataResult<Tag> encoded = codec.encodeStart(NbtOps.INSTANCE, value);
        encoded.resultOrPartial(err -> System.err.println("[ParCool] Failed to save stamina: " + err))
                .ifPresent(t -> tag.put("parcool_stamina", t));
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void parcool$loadStamina(CompoundTag tag, CallbackInfo ci) {
        if (!tag.contains("parcool_stamina")) return;
        AttachmentType<?> type = Attachments.staminaType();
        @SuppressWarnings("unchecked")
        var codec = (com.mojang.serialization.Codec<Object>) type.codec();
        codec.parse(NbtOps.INSTANCE, tag.get("parcool_stamina"))
                .resultOrPartial(err -> System.err.println("[ParCool] Failed to load stamina: " + err))
                .ifPresent(v -> parcool$attachments.put(type, v));
    }
}
