package com.alrex.parcool.client;

import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.CameraType;
import com.alrex.parcool.platform.dist.Dist;
import com.alrex.parcool.platform.dist.OnlyIn;

@OnlyIn(value=Dist.CLIENT)
public final class RenderBehaviorEnforcer {
    @Nullable
    private static Enforcer<CameraType> cameraTypeEnforcer = null;

    @OnlyIn(value=Dist.CLIENT)
    public static void serMarkerEnforceCameraType(Marker marker, Supplier<CameraType> cameraTypeSupplier) {
        cameraTypeEnforcer = new Enforcer<CameraType>(marker, cameraTypeSupplier);
    }

    @OnlyIn(value=Dist.CLIENT)
    @Nullable
    public static CameraType getEnforcedCameraType() {
        if (cameraTypeEnforcer != null && cameraTypeEnforcer.remain()) {
            return cameraTypeEnforcer.getBehavior();
        }
        cameraTypeEnforcer = null;
        return null;
    }

    public static class Enforcer<T> {
        final Marker marker;
        final Supplier<T> behaviorSupplier;

        Enforcer(Marker marker, Supplier<T> supplier) {
            this.marker = marker;
            this.behaviorSupplier = supplier;
        }

        boolean remain() {
            return this.marker.remain();
        }

        T getBehavior() {
            return this.behaviorSupplier.get();
        }
    }

    public static interface Marker {
        public boolean remain();
    }
}

