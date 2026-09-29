package com.alrex.parcool.platform;

/**
 * Stand-in for NeoForge's global NeoForgeConfig.SERVER options. ParCool only reads
 * `fullBoundingBoxLadders`; NeoForge's own default for that flag is `false` (vanilla-like ladder
 * detection). Fixed here rather than user-configurable - fold into ParCoolConfig if you want it
 * adjustable.
 */
public final class NeoForgeConfig {
    private NeoForgeConfig() {}
    public static final Server SERVER = new Server();
    public static final class Server {
        public final java.util.function.Supplier<Boolean> fullBoundingBoxLadders = () -> false;
    }
}
