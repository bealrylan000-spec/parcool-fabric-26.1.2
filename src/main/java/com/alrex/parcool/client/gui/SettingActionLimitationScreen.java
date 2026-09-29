package com.alrex.parcool.client.gui;

import com.alrex.parcool.common.info.ActionInfo;
import net.minecraft.network.chat.Component;

/** 26.1.2 compatibility screen; see ParCoolSettingScreen. */
public class SettingActionLimitationScreen extends ParCoolSettingScreen {
    public SettingActionLimitationScreen(Component title, ActionInfo info, ColorTheme theme) {
        super(title, info, theme);
    }
}
