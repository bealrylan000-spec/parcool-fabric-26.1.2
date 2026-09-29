package com.alrex.parcool.client.hud;

import com.alrex.parcool.client.hud.impl.StaminaHUDController;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import com.alrex.parcool.platform.dist.Dist;
import com.alrex.parcool.platform.dist.OnlyIn;
import com.alrex.parcool.platform.eventbus.SubscribeEvent;
import com.alrex.parcool.platform.event.tick.ClientTickEvent;
import com.alrex.parcool.platform.event.client.RegisterGuiLayersEvent;

@OnlyIn(value=Dist.CLIENT)
public class HUDManager {
    private static HUDManager instance = null;
    private final StaminaHUDController staminaHUD = new StaminaHUDController();

    public static HUDManager getInstance() {
        if (instance == null) {
            instance = new HUDManager();
        }
        return instance;
    }

    public void onSetup() {
    }

    public void registerHUD(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaHudElements.FOOD_BAR, StaminaHUDController.ID, this.staminaHUD);
    }

    @SubscribeEvent
    public void onTick(ClientTickEvent.Post event) {
        this.staminaHUD.onTick(event);
    }
}

