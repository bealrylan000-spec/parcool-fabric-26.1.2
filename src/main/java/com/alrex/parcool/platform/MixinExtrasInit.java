package com.alrex.parcool.platform;

import com.llamalad7.mixinextras.MixinExtrasBootstrap;
import net.fabricmc.api.EntrypointException;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

/** Boots MixinExtras (needed by mixin/common/PlayerMixin.java's @WrapWithCondition). */
public final class MixinExtrasInit implements PreLaunchEntrypoint {
    @Override
    public void onPreLaunch() {
        try {
            MixinExtrasBootstrap.init();
        } catch (Throwable t) {
            throw new EntrypointException("Failed to bootstrap MixinExtras", t);
        }
    }
}
