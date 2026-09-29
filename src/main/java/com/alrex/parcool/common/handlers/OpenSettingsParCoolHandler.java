package com.alrex.parcool.common.handlers;

import com.alrex.parcool.client.gui.ColorTheme;
import com.alrex.parcool.client.gui.SettingActionLimitationScreen;
import com.alrex.parcool.client.input.KeyRecorder;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.config.ParCoolConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import com.alrex.parcool.platform.dist.Dist;
import com.alrex.parcool.platform.dist.OnlyIn;
import com.alrex.parcool.platform.eventbus.SubscribeEvent;
import com.alrex.parcool.platform.event.client.ClientTickEvent;

@OnlyIn(value=Dist.CLIENT)
public class OpenSettingsParCoolHandler {
    @SubscribeEvent
    public static void onTick(ClientTickEvent.Pre event) {
        if (KeyRecorder.keyOpenSettingsState.isPressed()) {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player == null) {
                return;
            }
            Parkourability parkourability = Parkourability.get((Player)player);
            if (parkourability == null) {
                return;
            }
            Minecraft.getInstance().setScreen((Screen)new SettingActionLimitationScreen((Component)Component.literal((String)"ParCool Setting"), parkourability.getActionInfo(), (ColorTheme)((Object)ParCoolConfig.Client.getInstance().GUIColorTheme.get())));
        }
    }
}

