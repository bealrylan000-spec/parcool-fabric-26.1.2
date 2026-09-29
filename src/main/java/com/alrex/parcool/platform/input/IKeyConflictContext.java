package com.alrex.parcool.platform.input;

import com.mojang.blaze3d.platform.InputConstants;

/** Stand-in for {@code net.neoforged.neoforge.client.settings.IKeyConflictContext}. */
public interface IKeyConflictContext {
    boolean isActive();
    boolean conflicts(IKeyConflictContext other);
}
