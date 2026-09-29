package com.alrex.parcool.platform.dist;

/** Stand-in for {@code net.neoforged.api.distmarker.Dist}. */
public enum Dist {
    CLIENT,
    DEDICATED_SERVER;

    public boolean isClient() {
        return this == CLIENT;
    }
}
