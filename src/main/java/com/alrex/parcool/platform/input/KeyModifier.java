package com.alrex.parcool.platform.input;

/**
 * Stand-in for {@code net.neoforged.neoforge.client.settings.KeyModifier}. Vanilla's own
 * {@link net.minecraft.client.KeyMapping} constructor doesn't take a modifier key at all - NeoForge
 * adds that. This port's KeyBindings.java (hand-ported, see build notes) drops the CONTROL/ALT
 * modifiers from the two bindings that used them (enable-ParCool, open-settings) and instead
 * checks Screen.hasControlDown()/hasAltDown() inside the key's own handler, since a modifier-aware
 * KeyMapping isn't available on plain Fabric API without an extra library.
 */
public enum KeyModifier {
    NONE, SHIFT, CONTROL, ALT;
}
