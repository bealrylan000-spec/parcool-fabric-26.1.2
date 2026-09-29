package com.alrex.parcool.mixin.client;

import net.minecraft.client.model.player.PlayerModel;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Compatibility placeholder for Minecraft 26.1.
 *
 * PlayerModel#setupAnim now receives a render state instead of a LivingEntity.
 * ParCool's legacy entity-driven pose injection is therefore disabled for this
 * compile-first port and can be restored later with a render-state extension.
 */
@Mixin(PlayerModel.class)
public abstract class PlayerModelMixin {
}
