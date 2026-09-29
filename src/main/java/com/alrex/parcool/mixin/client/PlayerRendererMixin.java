package com.alrex.parcool.mixin.client;

import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Compatibility placeholder for Minecraft 26.1.
 *
 * Player rendering now consumes render-state objects rather than the player
 * entity in setupRotations.  The old ParCool pose hook cannot be safely
 * carried forward by signature substitution alone, so it is intentionally
 * disabled while the gameplay port remains functional.
 */
@Mixin(PlayerRenderer.class)
public abstract class PlayerRendererMixin {
}
