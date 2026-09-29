package com.alrex.parcool.platform.event.entity.living;

import com.alrex.parcool.platform.eventbus.Event;
import com.alrex.parcool.platform.eventbus.ICancellableEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;

/** Stand-in for {@code net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent}. */
public final class LivingIncomingDamageEvent extends Event implements ICancellableEvent {
    private final LivingEntity entity;
    private final DamageSource source;
    private float amount;

    public LivingIncomingDamageEvent(LivingEntity entity, DamageSource source, float amount) {
        this.entity = entity;
        this.source = source;
        this.amount = amount;
    }

    public LivingEntity getEntity() {
        return entity;
    }

    public DamageSource getSource() {
        return source;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }
}
