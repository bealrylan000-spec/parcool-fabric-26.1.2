package com.alrex.parcool.platform.event.client;

import com.alrex.parcool.platform.eventbus.Event;
import com.alrex.parcool.platform.eventbus.ICancellableEvent;
import net.minecraft.client.KeyMapping;

/** Stand-in for {@code net.neoforged.neoforge.client.event.InputEvent}. */
public abstract class InputEvent extends Event {

    /**
     * Stand-in for InputEvent.InteractionKeyMappingTriggered (fired for "use item" / "attack" key
     * presses so mods can cancel the vanilla interaction and/or the arm swing). There's no
     * ready-made Fabric API callback for this specific vanilla hook point; wire this up from a
     * mixin at the head of {@code Minecraft.startUseItem()} / the attack-key handling in
     * {@code MultiPlayerGameMode} (exact method names need verifying against your local, mapped
     * 26.1.2 sources) and call {@link com.alrex.parcool.platform.eventbus.NeoForge#EVENT_BUS}
     * .post(...) with the right constructor arguments from there.
     */
    public static final class InteractionKeyMappingTriggered extends InputEvent implements ICancellableEvent {
        private final KeyMapping keyMapping;
        private final boolean useItem;
        private boolean swingHand = true;

        public InteractionKeyMappingTriggered(KeyMapping keyMapping, boolean useItem) {
            this.keyMapping = keyMapping;
            this.useItem = useItem;
        }

        public KeyMapping getKeyMapping() {
            return keyMapping;
        }

        public boolean isUseItem() {
            return useItem;
        }

        public boolean isAttack() {
            return !useItem;
        }

        public boolean shouldSwingHand() {
            return swingHand;
        }

        public void setSwingHand(boolean swingHand) {
            this.swingHand = swingHand;
        }
    }
}
