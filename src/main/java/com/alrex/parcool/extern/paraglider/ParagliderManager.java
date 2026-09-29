package com.alrex.parcool.extern.paraglider;

import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.common.stamina.IParCoolStaminaHandler;
import com.alrex.parcool.common.stamina.StaminaType;
import com.alrex.parcool.common.stamina.handlers.ParCoolStaminaHandler;
import com.alrex.parcool.extern.ModManager;
import com.alrex.parcool.extern.paraglider.EventConsumerForParaglider;
import com.alrex.parcool.extern.paraglider.ParagliderStaminaHandler;
import javax.annotation.Nullable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import com.alrex.parcool.platform.eventbus.NeoForge;
import tictim.paraglider.api.ParagliderItemCapability;

public class ParagliderManager
extends ModManager {
    public ParagliderManager() {
        super("paraglider");
    }

    @Override
    public void initInClient() {
        if (this.isInstalled()) {
            NeoForge.EVENT_BUS.register(EventConsumerForParaglider.class);
        }
    }

    @Nullable
    public IParCoolStaminaHandler newParagliderStaminaHandlerFor(Player player) {
        if (this.isUsingParagliderStamina(Parkourability.get(player))) {
            return new ParagliderStaminaHandler();
        }
        return new ParCoolStaminaHandler();
    }

    public boolean isUsingParagliderStamina(Parkourability parkourability) {
        if (!this.isInstalled()) {
            return false;
        }
        StaminaType forcedStamina = parkourability.getServerLimitation().getForcedStamina();
        if (forcedStamina == StaminaType.PARAGLIDER) {
            return true;
        }
        return forcedStamina == StaminaType.NONE && parkourability.getClientInfo().getRequestedStamina() == StaminaType.PARAGLIDER;
    }

    public boolean isFallingWithParaglider(Player player) {
        if (this.isInstalled()) {
            for (ItemStack item : new ItemStack[]{player.getMainHandItem(), player.getOffhandItem()}) {
                ParagliderItemCapability cap = (ParagliderItemCapability)item.getCapability(ParagliderItemCapability.CAPABILITY);
                if (cap == null) {
                    cap = ParagliderItemCapability.defaultImpl();
                }
                if (!cap.isParagliding(item)) continue;
                return true;
            }
        }
        return false;
    }
}

