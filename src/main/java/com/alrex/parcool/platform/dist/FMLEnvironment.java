package com.alrex.parcool.platform.dist;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

/** Stand-in for {@code net.neoforged.fml.loading.FMLEnvironment}. */
public final class FMLEnvironment {
    public static final Dist dist =
            FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT
                    ? Dist.CLIENT
                    : Dist.DEDICATED_SERVER;

    private FMLEnvironment() {}
}
