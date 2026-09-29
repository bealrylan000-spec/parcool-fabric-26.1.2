package com.alrex.parcool.platform.event.entity;

import com.alrex.parcool.platform.eventbus.Event;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Stand-in for {@code net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent}.
 *
 * NeoForge fires this once (at mod-init time, for every registered EntityType) so mods can add
 * attributes to entity types they don't own - this is exactly how ParCool adds MAX_STAMINA and
 * STAMINA_RECOVERY to the vanilla Player. Vanilla/Fabric has no equivalent hook for the built-in
 * "player" EntityType because {@code Player.createAttributes()} builds its AttributeSupplier
 * directly in vanilla code. See PlayerAttributeMixin in this project: it injects into that method,
 * fires this event with an empty map, and adds everything collected here to the builder before
 * it's finalized. If you need this for other entity types too, extend that mixin's target list.
 */
public final class EntityAttributeModificationEvent extends Event {

    public record Entry(Holder<Attribute> attribute, double baseValue) {}

    private final Map<EntityType<?>, List<Entry>> pending;

    public EntityAttributeModificationEvent(Map<EntityType<?>, List<Entry>> pending) {
        this.pending = pending;
    }

    /** Matches NeoForge's {@code add(EntityType<?>, Holder<Attribute>)} (uses the attribute's own default). */
    public void add(EntityType<?> type, Holder<Attribute> attribute) {
        add(type, attribute, attribute.value().getDefaultValue());
    }

    public void add(EntityType<?> type, Holder<Attribute> attribute, double baseValue) {
        pending.computeIfAbsent(type, t -> new ArrayList<>()).add(new Entry(attribute, baseValue));
    }
}
