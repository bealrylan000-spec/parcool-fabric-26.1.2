package com.alrex.parcool.platform.event.entity.living;

import com.alrex.parcool.platform.eventbus.Event;
import com.alrex.parcool.platform.eventbus.ICancellableEvent;
import net.minecraft.world.entity.LivingEntity;

/** Stand-in for {@code net.neoforged.neoforge.event.entity.living.LivingFallEvent}. */
public final class LivingFallEvent extends Event implements ICancellableEvent {
    private final LivingEntity entity;
    private final float distance;
    private float damageMultiplier;

    public LivingFallEvent(LivingEntity entity, float distance, float damageMultiplier) {
        this.entity = entity;
        this.distance = distance;
        this.damageMultiplier = damageMultiplier;
    }

    public LivingEntity getEntity() {
        return entity;
    }

    public float getDistance() {
        return distance;
    }

    public float getDamageMultiplier() {
        return damageMultiplier;
    }

    public void setDamageMultiplier(float damageMultiplier) {
        this.damageMultiplier = damageMultiplier;
    }
}
