package com.alrex.parcool.platform.event.client;

import com.alrex.parcool.platform.eventbus.Event;
import net.minecraft.client.Camera;

/** Stand-in for {@code net.neoforged.neoforge.client.event.ViewportEvent}. */
public abstract class ViewportEvent extends Event {
    private final Camera camera;
    private final float partialTick;

    protected ViewportEvent(Camera camera, float partialTick) {
        this.camera = camera;
        this.partialTick = partialTick;
    }

    public Camera getCamera() {
        return camera;
    }

    public float getPartialTick() {
        return partialTick;
    }

    public static final class ComputeCameraAngles extends ViewportEvent {
        private float yaw;
        private float pitch;
        private float roll;

        public ComputeCameraAngles(Camera camera, float partialTick, float yaw, float pitch, float roll) {
            super(camera, partialTick);
            this.yaw = yaw;
            this.pitch = pitch;
            this.roll = roll;
        }

        public float getYaw() { return yaw; }
        public void setYaw(float yaw) { this.yaw = yaw; }
        public float getPitch() { return pitch; }
        public void setPitch(float pitch) { this.pitch = pitch; }
        public float getRoll() { return roll; }
        public void setRoll(float roll) { this.roll = roll; }
    }
}
