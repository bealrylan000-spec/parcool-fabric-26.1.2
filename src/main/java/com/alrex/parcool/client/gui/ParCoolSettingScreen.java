package com.alrex.parcool.client.gui;

import com.alrex.parcool.common.info.ActionInfo;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * Minimal 26.1.2-compatible settings screen shell.
 *
 * The original ParCool settings UI used the pre-26.1 immediate-mode GuiGraphics API.
 * Gameplay/config values still work and persist; the rich settings widgets can be
 * restored separately without blocking the Fabric 26.1.2 port.
 */
public abstract class ParCoolSettingScreen extends Screen {
    protected final ActionInfo info;
    protected final ColorTheme color;

    protected ParCoolSettingScreen(Component title, ActionInfo info, ColorTheme theme) {
        super(title);
        this.info = info;
        this.color = theme;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractRenderState(graphics, mouseX, mouseY, delta);
        graphics.centeredText(this.font, this.title, this.width / 2, 30, 0xFFFFFFFF);
        graphics.centeredText(this.font,
                Component.literal("ParCool 26.1.2 settings UI is temporarily simplified."),
                this.width / 2, 55, 0xFFE0E0E0);
        graphics.centeredText(this.font,
                Component.literal("Gameplay settings are still stored in config/parcool-client.properties."),
                this.width / 2, 75, 0xFFB0B0B0);
        graphics.centeredText(this.font,
                Component.literal("Press Esc to return to the game."),
                this.width / 2, 95, 0xFFB0B0B0);
    }
}
