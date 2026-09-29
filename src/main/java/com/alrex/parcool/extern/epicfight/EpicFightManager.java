package com.alrex.parcool.extern.epicfight;

import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.common.stamina.IParCoolStaminaHandler;
import com.alrex.parcool.common.stamina.handlers.ParCoolStaminaHandler;
import com.alrex.parcool.extern.ModManager;
import net.minecraft.world.entity.player.Player;

/** Optional Epic Fight integration is disabled in the standalone 26.1.2 build. */
public class EpicFightManager extends ModManager {
    public EpicFightManager() { super("epicfight"); }
    public IParCoolStaminaHandler newStaminaHandlerFor(Player player) { return new ParCoolStaminaHandler(); }
    public boolean isUsingEpicFightStamina(Parkourability parkourability) { return false; }
}
