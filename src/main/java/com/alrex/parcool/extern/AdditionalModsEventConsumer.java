package com.alrex.parcool.extern;

import com.alrex.parcool.api.client.gui.ParCoolHUDEvent;
import com.alrex.parcool.extern.AdditionalMods;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import com.alrex.parcool.platform.dist.Dist;
import com.alrex.parcool.platform.dist.OnlyIn;
import com.alrex.parcool.platform.eventbus.SubscribeEvent;

public class AdditionalModsEventConsumer {

    public static class Client {
        @OnlyIn(value=Dist.CLIENT)
        @SubscribeEvent
        public static void onHUDRender(ParCoolHUDEvent.RenderEvent event) {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player == null) {
                return;
            }
            if (AdditionalMods.isUsingExternalStamina()) {
                event.setCanceled(true);
            }
        }
    }
}

