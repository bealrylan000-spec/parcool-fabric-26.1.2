package com.alrex.parcool.platform.event.entity.living;

import com.alrex.parcool.platform.eventbus.Event;
import net.minecraft.world.entity.LivingEntity;

/** Stand-in for {@code net.neoforged.neoforge.event.entity.living.LivingEvent}. */
public abstract class LivingEvent extends Event {
    private final LivingEntity entity;

    protected LivingEvent(LivingEntity entity) {
        this.entity = entity;
    }

    public LivingEntity getEntity() {
        return entity;
    }

    public static final class LivingJumpEvent extends LivingEvent {
        public LivingJumpEvent(LivingEntity entity) { super(entity); }
    }

    /**
     * Stand-in for NeoForge's LivingVisibilityEvent. NeoForge's own version composes multiplier
     * contributions from every listener; this simplified version just lets each listener multiply
     * the running value directly via {@link #modifyVisibility(double)}, which is the only thing
     * PlayerVisibilityHandler needs. There is no built-in Fabric hook that reduces mob-sight range
     * for a specific entity - wire this event up wherever your local mixin computes visibility
     * (e.g. a mixin on the AI "can I see this target" check), or accept that this specific NeoForge
     * feature (hidden players are harder for mobs to notice) is a known gap until you add that hook.
     */
    public static final class LivingVisibilityEvent extends LivingEvent {
        private double visibilityModifier = 1.0;

        public LivingVisibilityEvent(LivingEntity entity, double baseVisibility) {
            super(entity);
            this.visibilityModifier = baseVisibility;
        }

        public double getVisibilityModifier() {
            return visibilityModifier;
        }

        public void modifyVisibility(double mod) {
            this.visibilityModifier *= mod;
        }
    }
}
