package com.alrex.parcool.common.action;

import java.util.TreeMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.world.phys.Vec3;

public class BehaviorEnforcer {
    private final TreeMap<ID, Marker> jumpCancelMarks = new TreeMap();
    private final TreeMap<ID, Marker> descendFromEdgeCancelMarks = new TreeMap();
    private final ConcurrentSkipListMap<ID, Marker> sneakCancelMarks = new ConcurrentSkipListMap();
    private final TreeMap<ID, Marker> sprintCancelMarks = new TreeMap();
    private final TreeMap<ID, Marker> fallFlyingCancelMarks = new TreeMap();
    private final TreeMap<ID, Marker> showNameCancelMarks = new TreeMap();
    @Nullable
    private Enforcer<Vec3> movementEnforcer = null;
    @Nullable
    private Enforcer<Vec3> positionEnforcer = null;

    public static ID newID() {
        return ID.newID();
    }

    public void addMarkerCancellingJump(ID id, Marker marker) {
        this.jumpCancelMarks.put(id, marker);
    }

    public void addMarkerCancellingSneak(ID id, Marker marker) {
        this.sneakCancelMarks.put(id, marker);
    }

    public void addMarkerCancellingDescendFromEdge(ID id, Marker marker) {
        this.descendFromEdgeCancelMarks.put(id, marker);
    }

    public void addMarkerCancellingSprint(ID id, Marker marker) {
        this.sprintCancelMarks.put(id, marker);
    }

    public void addMarkerCancellingFallFlying(ID id, Marker marker) {
        this.fallFlyingCancelMarks.put(id, marker);
    }

    public void addMarkerCancellingShowName(ID id, Marker marker) {
        this.showNameCancelMarks.put(id, marker);
    }

    public void setMarkerEnforceMovePoint(Marker marker, Supplier<Vec3> movementSupplier) {
        this.movementEnforcer = new Enforcer<Vec3>(marker, movementSupplier);
    }

    public void setMarkerEnforcePosition(Marker marker, Supplier<Vec3> movementSupplier) {
        this.positionEnforcer = new Enforcer<Vec3>(marker, movementSupplier);
    }

    public boolean cancelJump() {
        this.jumpCancelMarks.values().removeIf(it -> !it.remain());
        return !this.jumpCancelMarks.isEmpty();
    }

    public boolean cancelSneak() {
        this.sneakCancelMarks.values().removeIf(it -> !it.remain());
        return !this.sneakCancelMarks.isEmpty();
    }

    public boolean cancelDescendFromEdge() {
        this.descendFromEdgeCancelMarks.values().removeIf(it -> !it.remain());
        return !this.descendFromEdgeCancelMarks.isEmpty();
    }

    public boolean cancelSprint() {
        this.sprintCancelMarks.values().removeIf(it -> !it.remain());
        return !this.sprintCancelMarks.isEmpty();
    }

    public boolean cancelFallFlying() {
        this.fallFlyingCancelMarks.values().removeIf(it -> !it.remain());
        return !this.fallFlyingCancelMarks.isEmpty();
    }

    public boolean cancelShowingName() {
        this.showNameCancelMarks.values().removeIf(it -> !it.remain());
        return !this.showNameCancelMarks.isEmpty();
    }

    @Nullable
    public Vec3 getEnforcedMovePoint() {
        if (this.movementEnforcer != null && this.movementEnforcer.remain()) {
            return this.movementEnforcer.getBehavior();
        }
        this.movementEnforcer = null;
        return null;
    }

    @Nullable
    public Vec3 getEnforcedPosition() {
        if (this.positionEnforcer != null && this.positionEnforcer.remain()) {
            return this.positionEnforcer.getBehavior();
        }
        this.positionEnforcer = null;
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

    public static class ID
    implements Comparable<ID> {
        private static int idValue = 0;
        private final int value;

        private static ID newID() {
            return new ID(idValue++);
        }

        private ID(int value) {
            this.value = value;
        }

        @Override
        public int compareTo(ID o) {
            return Integer.compare(this.value, o.value);
        }
    }

    public static interface Marker {
        public boolean remain();
    }
}

