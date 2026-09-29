package com.alrex.parcool.platform.dist;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Stand-in for {@code net.neoforged.api.distmarker.OnlyIn}.
 *
 * On NeoForge this is enforced by the mod loader at classloading time. On Fabric we don't
 * split a separate client source set for this port, so this annotation is purely documentation:
 * it is up to the runtime dist-check in {@link FMLEnvironment#dist} (mirrored from the original
 * ParCool.java entrypoint) to make sure client-only code paths never actually execute on a
 * dedicated server. As long as that guard stays intact, the JVM never loads the client classes
 * referenced from inside those guarded methods, because Java only resolves a class the first
 * time a method that references it actually runs.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.CONSTRUCTOR})
public @interface OnlyIn {
    Dist value();
}
