package com.alrex.parcool.platform.event.client;

import com.alrex.parcool.platform.eventbus.Event;

/** Stand-in for {@code net.neoforged.neoforge.client.event.RenderFrameEvent}. */
public abstract class RenderFrameEvent extends Event {
    private final float partialTick;

    protected RenderFrameEvent(float partialTick) {
        this.partialTick = partialTick;
    }

    public float getPartialTick() {
        return partialTick;
    }

    public static final class Pre extends RenderFrameEvent {
        public Pre(float partialTick) { super(partialTick); }
    }

    public static final class Post extends RenderFrameEvent {
        public Post(float partialTick) { super(partialTick); }
    }
}
