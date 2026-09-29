package com.alrex.parcool.api.client.gui;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import com.alrex.parcool.platform.eventbus.Event;
import com.alrex.parcool.platform.eventbus.ICancellableEvent;

public class ParCoolHUDEvent
extends Event {

    public static class RenderEvent
    extends ParCoolHUDEvent
    implements ICancellableEvent {
        private final GuiGraphicsExtractor graphics;
        private final DeltaTracker partialTick;

        public RenderEvent(GuiGraphicsExtractor s, DeltaTracker partialTick) {
            this.graphics = s;
            this.partialTick = partialTick;
        }

        public GuiGraphicsExtractor getGuiGraphicsExtractor() {
            return this.graphics;
        }

        public DeltaTracker getDeltaTracker() {
            return this.partialTick;
        }
    }
}

