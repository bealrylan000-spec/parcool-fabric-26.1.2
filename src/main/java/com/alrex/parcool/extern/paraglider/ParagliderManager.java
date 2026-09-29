package com.alrex.parcool.extern.paraglider;

import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.common.stamina.IParCoolStaminaHandler;
import com.alrex.parcool.common.stamina.handlers.ParCoolStaminaHandler;
import com.alrex.parcool.extern.ModManager;
import net.minecraft.world.entity.player.Player;

/** Optional Paraglider integration is disabled in the standalone 26.1.2 build. */
public class ParagliderManager extends ModManager {
    public ParagliderManager() { super("paraglider"); }
    public IParCoolStaminaHandler newParagliderStaminaHandlerFor(Player player) { return new ParCoolStaminaHandler(); }
    public boolean isUsingParagliderStamina(Parkourability parkourability) { return false; }
    public boolean isFallingWithParaglider(Player player) { return false; }
}
