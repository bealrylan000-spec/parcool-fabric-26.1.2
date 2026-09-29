package com.alrex.parcool.client.hud;

import com.alrex.parcool.client.hud.HUDManager;
import com.alrex.parcool.platform.eventbus.SubscribeEvent;
import com.alrex.parcool.platform.event.client.RegisterGuiLayersEvent;

public class HUDRegistry {
    @SubscribeEvent
    public static void onRegisterGui(RegisterGuiLayersEvent event) {
        HUDManager.getInstance().registerHUD(event);
    }
}

