package com.alrex.parcool.platform.input;

/** Stand-in for {@code net.neoforged.neoforge.client.settings.KeyConflictContext}. */
public enum KeyConflictContext implements IKeyConflictContext {
    UNIVERSAL, IN_GAME, GUI;

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public boolean conflicts(IKeyConflictContext other) {
        return this == UNIVERSAL || other == UNIVERSAL || this == other;
    }
}
