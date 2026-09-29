package com.alrex.parcool.client.input;

import com.alrex.parcool.utilities.VectorUtil;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import com.alrex.parcool.platform.dist.Dist;
import com.alrex.parcool.platform.dist.OnlyIn;
import com.alrex.parcool.platform.eventbus.SubscribeEvent;
import com.alrex.parcool.platform.event.client.RegisterKeyMappingsEvent;
import com.alrex.parcool.platform.input.IKeyConflictContext;
import com.alrex.parcool.platform.input.KeyConflictContext;
import com.alrex.parcool.platform.input.KeyModifier;

@OnlyIn(value=Dist.CLIENT)
public class KeyBindings {
    private static final Minecraft mc = Minecraft.getInstance();
    private static final Options settings = KeyBindings.mc.options;
    private static final KeyMapping keyBindEnable = new KeyMapping("key.parcool.Enable", (IKeyConflictContext)KeyConflictContext.UNIVERSAL, KeyModifier.CONTROL, InputConstants.Type.KEYSYM, 80, "key.categories.parcool");
    private static final KeyMapping keyBindCrawl = new KeyMapping("key.parcool.Crawl", 67, "key.categories.parcool");
    private static final KeyMapping keyBindGrabWall = new KeyMapping("key.parcool.ClingToCliff", InputConstants.Type.MOUSE, 1, "key.categories.parcool");
    private static final KeyMapping keyBindBreakfall = new KeyMapping("key.parcool.Breakfall", 82, "key.categories.parcool");
    private static final KeyMapping keyBindFastRunning = new KeyMapping("key.parcool.FastRun", 341, "key.categories.parcool");
    private static final KeyMapping keyBindFlipping = new KeyMapping("key.parcool.Flipping", -1, "key.categories.parcool");
    private static final KeyMapping keyBindVault = new KeyMapping("key.parcool.Vault", InputConstants.Type.MOUSE, 1, "key.categories.parcool");
    private static final KeyMapping keyBindDodge = new KeyMapping("key.parcool.Dodge", 82, "key.categories.parcool");
    private static final KeyMapping keyBindRideZipline = new KeyMapping("key.parcool.RideZipline", InputConstants.Type.MOUSE, 1, "key.categories.parcool");
    private static final KeyMapping keyBindWallJump = new KeyMapping("key.parcool.WallJump", 32, "key.categories.parcool");
    private static final KeyMapping keyBindHangDown = new KeyMapping("key.parcool.HangDown", InputConstants.Type.MOUSE, 1, "key.categories.parcool");
    private static final KeyMapping keyBindWallSlide = new KeyMapping("key.parcool.WallSlide", InputConstants.Type.MOUSE, 1, "key.categories.parcool");
    private static final KeyMapping keyBindHideInBlock = new KeyMapping("key.parcool.HideInBlock", 67, "key.categories.parcool");
    private static final KeyMapping keyBindHorizontalWallRun = new KeyMapping("key.parcool.HorizontalWallRun", 82, "key.categories.parcool");
    private static final KeyMapping keyBindQuickTurn = new KeyMapping("key.parcool.QuickTurn", -1, "key.categories.parcool");
    private static final KeyMapping keyBindOpenSettings = new KeyMapping("key.parcool.openSetting", (IKeyConflictContext)KeyConflictContext.UNIVERSAL, KeyModifier.ALT, InputConstants.Type.KEYSYM, 80, "key.categories.parcool");
    private static final Vec3 forwardVector = new Vec3(0.0, 0.0, 1.0);

    public static KeyMapping getKeySprint() {
        return KeyBindings.settings.keySprint;
    }

    public static Boolean isKeyJumpDown() {
        return KeyBindings.mc.player != null && KeyBindings.mc.player.input != null && KeyBindings.mc.player.input.jumping;
    }

    public static KeyMapping getKeySneak() {
        return KeyBindings.settings.keyShift;
    }

    public static Vec3 getCurrentMoveVector() {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return Vec3.ZERO;
        }
        Vec2 vector = player.input.getMoveVector();
        if (VectorUtil.isZero(vector)) {
            return Vec3.ZERO;
        }
        double length = vector.length();
        return new Vec3((double)vector.x / length, 0.0, (double)vector.y / length);
    }

    public static Vec3 getForwardVector() {
        return forwardVector;
    }

    public static Boolean isAnyMovingKeyDown() {
        return KeyBindings.mc.player != null && KeyBindings.mc.player.input != null && (KeyBindings.mc.player.input.left || KeyBindings.mc.player.input.right || KeyBindings.mc.player.input.forwardImpulse != 0.0f || KeyBindings.mc.player.input.leftImpulse != 0.0f);
    }

    public static Boolean isLeftAndRightDown() {
        return KeyBindings.mc.player != null && KeyBindings.mc.player.input != null && KeyBindings.mc.player.input.left && KeyBindings.mc.player.input.right;
    }

    public static Boolean isKeyForwardDown() {
        return KeyBindings.mc.player != null && KeyBindings.mc.player.input != null && KeyBindings.mc.player.input.forwardImpulse > 0.0f;
    }

    public static Boolean isKeyLeftDown() {
        return KeyBindings.mc.player != null && KeyBindings.mc.player.input != null && KeyBindings.mc.player.input.left;
    }

    public static Boolean isKeyRightDown() {
        return KeyBindings.mc.player != null && KeyBindings.mc.player.input != null && KeyBindings.mc.player.input.right;
    }

    public static Boolean isKeyBackDown() {
        return KeyBindings.mc.player != null && KeyBindings.mc.player.input != null && KeyBindings.mc.player.input.forwardImpulse < 0.0f;
    }

    public static KeyMapping getKeyBindEnable() {
        return keyBindEnable;
    }

    public static KeyMapping getKeyCrawl() {
        return keyBindCrawl;
    }

    public static KeyMapping getKeyQuickTurn() {
        return keyBindQuickTurn;
    }

    public static KeyMapping getKeyGrabWall() {
        return keyBindGrabWall;
    }

    public static KeyMapping getKeyVault() {
        return keyBindVault;
    }

    public static KeyMapping getKeyActivateParCool() {
        return keyBindOpenSettings;
    }

    public static KeyMapping getKeyBreakfall() {
        return keyBindBreakfall;
    }

    public static KeyMapping getKeyFastRunning() {
        return keyBindFastRunning;
    }

    public static KeyMapping getKeyDodge() {
        return keyBindDodge;
    }

    public static KeyMapping getKeyRideZipline() {
        return keyBindRideZipline;
    }

    public static KeyMapping getKeyWallSlide() {
        return keyBindWallSlide;
    }

    public static KeyMapping getKeyHangDown() {
        return keyBindHangDown;
    }

    public static KeyMapping getKeyHideInBlock() {
        return keyBindHideInBlock;
    }

    public static KeyMapping getKeyHorizontalWallRun() {
        return keyBindHorizontalWallRun;
    }

    public static KeyMapping getKeyWallJump() {
        return keyBindWallJump;
    }

    public static KeyMapping getKeyFlipping() {
        return keyBindFlipping;
    }

    @SubscribeEvent
    public static void register(RegisterKeyMappingsEvent event) {
        event.register(keyBindEnable);
        event.register(keyBindCrawl);
        event.register(keyBindGrabWall);
        event.register(keyBindBreakfall);
        event.register(keyBindFastRunning);
        event.register(keyBindDodge);
        event.register(keyBindRideZipline);
        event.register(keyBindWallSlide);
        event.register(keyBindWallJump);
        event.register(keyBindVault);
        event.register(keyBindHorizontalWallRun);
        event.register(keyBindHideInBlock);
        event.register(keyBindOpenSettings);
        event.register(keyBindQuickTurn);
        event.register(keyBindFlipping);
        event.register(keyBindHangDown);
    }
}

