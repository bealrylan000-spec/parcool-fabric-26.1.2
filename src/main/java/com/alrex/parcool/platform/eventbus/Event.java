package com.alrex.parcool.platform.eventbus;

/**
 * Stand-in for {@code net.neoforged.bus.api.Event}. The real NeoForge base class only exposes
 * cancellation when the concrete event also implements {@link ICancellableEvent}; we keep the
 * same shape here (state lives on the base class, but {@code setCanceled}/{@code isCanceled}
 * are only meaningful - and only called by ParCool's own code - on subtypes that also implement
 * ICancellableEvent) so the original call sites (`event.isCanceled()`, `event.setCanceled(true)`)
 * keep compiling unchanged.
 */
public abstract class Event {
    private boolean canceled = false;

    public boolean isCanceled() {
        return canceled;
    }

    public void setCanceled(boolean canceled) {
        this.canceled = canceled;
    }
}
