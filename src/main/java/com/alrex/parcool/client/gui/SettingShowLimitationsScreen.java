package com.alrex.parcool.client.gui;

import com.alrex.parcool.client.gui.ColorTheme;
import com.alrex.parcool.client.gui.ParCoolSettingScreen;
import com.alrex.parcool.common.info.ActionInfo;
import com.alrex.parcool.config.ParCoolConfig;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Objects;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class SettingShowLimitationsScreen
extends ParCoolSettingScreen {
    private final InfoSet[] infoList;

    public SettingShowLimitationsScreen(Component titleIn, ActionInfo info, ColorTheme theme) {
        super(titleIn, info, theme);
        this.currentScreen = 3;
        LinkedList<InfoSet> infoSets = new LinkedList<InfoSet>();
        for (ParCoolConfig.Server.Booleans booleans : ParCoolConfig.Server.Booleans.values()) {
            infoSets.add(new InfoSet(booleans.Path, Boolean.toString(info.getServerLimitation().get(booleans))));
        }
        for (Enum enum_ : ParCoolConfig.Server.Integers.values()) {
            infoSets.add(new InfoSet(((ParCoolConfig.Server.Integers)enum_).Path, Integer.toString(info.getServerLimitation().get((ParCoolConfig.Server.Integers)enum_))));
        }
        for (Enum enum_ : ParCoolConfig.Server.Doubles.values()) {
            infoSets.add(new InfoSet(((ParCoolConfig.Server.Doubles)enum_).Path, Double.toString(info.getServerLimitation().get((ParCoolConfig.Server.Doubles)enum_))));
        }
        this.infoList = infoSets.toArray(new InfoSet[0]);
    }

    @Override
    protected boolean isDownScrollable() {
        return this.topIndex + this.viewableItemCount < this.infoList.length;
    }

    @Override
    protected void renderContents(GuiGraphics graphics, int mouseX, int mouseY, float partialTick, int topOffset, int bottomOffset) {
        int offsetX = 40;
        int contentHeight = this.height - topOffset - bottomOffset;
        Objects.requireNonNull(this.font);
        int itemHeight = 9 * 2;
        int valueWidth = Arrays.stream(this.infoList).map(it -> this.font.width(it.value)).max(Integer::compareTo).orElse(0);
        this.viewableItemCount = contentHeight / itemHeight;
        for (int i = 0; i < this.viewableItemCount && i + this.topIndex < this.infoList.length; ++i) {
            InfoSet item = this.infoList[this.topIndex + i];
            String string = item.name;
            int n = topOffset + itemHeight * i + itemHeight / 2;
            Objects.requireNonNull(this.font);
            graphics.drawString(this.font, string, 45, n - 9 / 2, this.color.getText());
            String string2 = item.value;
            int n2 = topOffset + itemHeight * i + itemHeight / 2;
            Objects.requireNonNull(this.font);
            graphics.drawString(this.font, string2, this.width - 40 - 5 - valueWidth, n2 - 9 / 2, this.color.getText());
            graphics.fill(40, topOffset + itemHeight * (i + 1), this.width - 40, topOffset + itemHeight * (i + 1) + 1, this.color.getSubSeparator());
        }
        graphics.fill(this.width - 40, topOffset, this.width - 40 - 1, topOffset + contentHeight, this.color.getSeparator());
        graphics.fill(40, topOffset, 41, topOffset + contentHeight, this.color.getSeparator());
    }

    private static class InfoSet {
        final String name;
        final String value;

        InfoSet(String name, String value) {
            this.name = name;
            this.value = value;
        }
    }
}

