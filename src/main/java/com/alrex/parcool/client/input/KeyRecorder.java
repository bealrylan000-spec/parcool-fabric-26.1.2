package com.alrex.parcool.client.input;

import com.alrex.parcool.client.input.KeyBindings;
import javax.annotation.Nullable;
import net.minecraft.client.KeyMapping;
import net.minecraft.world.phys.Vec3;
import com.alrex.parcool.platform.dist.Dist;
import com.alrex.parcool.platform.dist.OnlyIn;
import com.alrex.parcool.platform.eventbus.SubscribeEvent;
import com.alrex.parcool.platform.event.client.MovementInputUpdateEvent;

@OnlyIn(value=Dist.CLIENT)
public class KeyRecorder {
    public static final KeyState keyForward = new KeyState();
    public static final KeyState keyBack = new KeyState();
    public static final KeyState keyRight = new KeyState();
    public static final KeyState keyLeft = new KeyState();
    public static final KeyState keySneak = new KeyState();
    public static final KeyState keyJumpState = new KeyState();
    public static final KeyState keySprintState = new KeyState();
    public static final KeyState keyCrawlState = new KeyState();
    public static final KeyState keyOpenSettingsState = new KeyState();
    public static final KeyState keyFastRunning = new KeyState();
    public static final KeyState keyDodge = new KeyState();
    public static final KeyState keyRideZipline = new KeyState();
    public static final KeyState keyBreakfall = new KeyState();
    public static final KeyState keyWallJump = new KeyState();
    public static final KeyState keyQuickTurn = new KeyState();
    public static final KeyState keyFlipping = new KeyState();
    public static final KeyState keyGrabWall = new KeyState();
    public static Vec3 lastDirection = null;

    @SubscribeEvent
    public static void onClientTick(MovementInputUpdateEvent event) {
        KeyRecorder.record(KeyBindings.isKeyForwardDown(), keyForward);
        KeyRecorder.record(KeyBindings.isKeyBackDown(), keyBack);
        KeyRecorder.record(KeyBindings.isKeyRightDown(), keyRight);
        KeyRecorder.record(KeyBindings.isKeyLeftDown(), keyLeft);
        KeyRecorder.record(KeyBindings.getKeySneak(), keySneak);
        KeyRecorder.record(KeyBindings.isKeyJumpDown(), keyJumpState);
        KeyRecorder.record(KeyBindings.getKeySprint(), keySprintState);
        KeyRecorder.record(KeyBindings.getKeyCrawl(), keyCrawlState);
        KeyRecorder.record(KeyBindings.getKeyActivateParCool(), keyOpenSettingsState);
        KeyRecorder.record(KeyBindings.getKeyFastRunning(), keyFastRunning);
        KeyRecorder.record(KeyBindings.getKeyDodge(), keyDodge);
        KeyRecorder.record(KeyBindings.getKeyRideZipline(), keyRideZipline);
        KeyRecorder.record(KeyBindings.getKeyBreakfall(), keyBreakfall);
        KeyRecorder.record(KeyBindings.getKeyWallJump(), keyWallJump);
        KeyRecorder.record(KeyBindings.getKeyQuickTurn(), keyQuickTurn);
        KeyRecorder.record(KeyBindings.getKeyFlipping(), keyFlipping);
        KeyRecorder.record(KeyBindings.getKeyGrabWall(), keyGrabWall);
        KeyRecorder.recordMovingVector(KeyBindings.isAnyMovingKeyDown());
    }

    @Nullable
    public static Vec3 getLastMoveVector() {
        return lastDirection;
    }

    private static void record(Boolean isDown, KeyState state) {
        state.pressed = isDown != false && state.tickKeyDown == 0;
        state.released = isDown == false && state.tickNotKeyDown == 0;
        boolean bl = state.doubleTapped = isDown != false && 0 < state.tickNotKeyDown && state.tickNotKeyDown <= 2;
        if (state.pressed && state.tickNotKeyDown > 0) {
            state.previousTickNotKeyDown = state.tickNotKeyDown;
        }
        if (isDown.booleanValue()) {
            ++state.tickKeyDown;
            state.tickNotKeyDown = 0;
        } else {
            state.tickKeyDown = 0;
            ++state.tickNotKeyDown;
        }
    }

    private static void record(KeyMapping keyBinding, KeyState state) {
        KeyRecorder.record(keyBinding.isDown(), state);
    }

    private static void recordMovingVector(boolean isMoving) {
        if (isMoving) {
            lastDirection = KeyBindings.getCurrentMoveVector();
        }
    }

    public static class KeyState {
        private boolean pressed = false;
        private boolean released = false;
        private boolean doubleTapped = false;
        private int tickKeyDown = 0;
        private int tickNotKeyDown = 0;
        private int previousTickNotKeyDown = Integer.MAX_VALUE;

        public boolean isPressed() {
            return this.pressed;
        }

        public boolean isReleased() {
            return this.released;
        }

        public boolean isDoubleTapped() {
            return this.doubleTapped;
        }

        public int getTickKeyDown() {
            return this.tickKeyDown;
        }

        public int getTickNotKeyDown() {
            return this.tickNotKeyDown;
        }

        public int getPreviousTickNotKeyDown() {
            return this.previousTickNotKeyDown;
        }
    }
}

