package com.alrex.parcool.client.gui;

import com.alrex.parcool.client.gui.ColorTheme;
import com.alrex.parcool.client.gui.ParCoolSettingScreen;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.common.info.ActionInfo;
import com.alrex.parcool.common.info.ClientSetting;
import com.alrex.parcool.common.network.payload.ClientInformationPayload;
import com.alrex.parcool.config.ParCoolConfig;
import java.util.Collections;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import com.alrex.parcool.platform.config.ModConfigSpec;
import com.alrex.parcool.platform.network.PacketDistributor;

public class SettingBooleanConfigScreen
extends ParCoolSettingScreen {
    private final ParCoolConfig.Client.Booleans[] booleans = ParCoolConfig.Client.Booleans.values();
    private final Checkbox[] configButtons = new Checkbox[this.booleans.length];

    public SettingBooleanConfigScreen(Component titleIn, ActionInfo info, ColorTheme theme) {
        super(titleIn, info, theme);
        this.currentScreen = 1;
        for (int i = 0; i < this.booleans.length; ++i) {
            this.configButtons[i] = Checkbox.builder((Component)Component.translatable((String)this.booleans[i].Path), (Font)Minecraft.getInstance().font).pos(0, 0).selected(this.booleans[i].get().booleanValue()).build();
            this.configButtons[i].setHeight(18);
        }
    }

    @Override
    protected boolean isDownScrollable() {
        return this.topIndex + this.viewableItemCount < this.configButtons.length;
    }

    @Override
    protected void renderContents(GuiGraphics graphics, int mouseX, int mouseY, float partialTick, int topOffset, int bottomOffset) {
        int offsetX = 40;
        int contentWidth = this.width - 80;
        int contentHeight = this.height - topOffset - bottomOffset;
        this.viewableItemCount = contentHeight / 18;
        for (Checkbox configButton : this.configButtons) {
            configButton.setWidth(0);
        }
        for (int i = 0; i < this.viewableItemCount && i + this.topIndex < this.booleans.length; ++i) {
            Checkbox button = this.configButtons[i + this.topIndex];
            button.setX(41);
            button.setY(topOffset + 18 * i);
            button.setWidth(contentWidth);
            button.setHeight(17);
            button.render(graphics, mouseX, mouseY, partialTick);
            graphics.fill(40, button.getY() + button.getHeight(), this.width - 40, button.getY() + button.getHeight() + 1, this.color.getSubSeparator());
            String comment = this.booleans[i + this.topIndex].Comment;
            if (comment == null || button.getX() >= mouseX || mouseX >= button.getX() + contentWidth || button.getY() >= mouseY || mouseY >= button.getY() + 20) continue;
            graphics.renderComponentTooltip(this.font, Collections.singletonList(Component.literal((String)comment)), mouseX, mouseY);
        }
        graphics.fill(this.width - 40, topOffset, this.width - 40 - 1, topOffset + contentHeight, this.color.getSeparator());
        graphics.fill(40, topOffset, 41, topOffset + contentHeight, this.color.getSeparator());
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int type) {
        for (Checkbox button : this.configButtons) {
            if (!button.mouseClicked(mouseX, mouseY, type)) continue;
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, type);
    }

    @Override
    public void onClose() {
        super.onClose();
    }

    @Override
    protected void save() {
        for (int i = 0; i < this.booleans.length; ++i) {
            this.booleans[i].set(this.configButtons[i].selected());
            ModConfigSpec.BooleanValue configInstance = this.booleans[i].getInternalInstance();
            if (configInstance == null) continue;
            configInstance.save();
        }
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }
        Parkourability parkourability = Parkourability.get((Player)player);
        parkourability.getActionInfo().setClientSetting(ClientSetting.readFromLocalConfig());
        PacketDistributor.sendToServer((CustomPacketPayload)new ClientInformationPayload(player.getUUID(), true, parkourability.getClientInfo()), (CustomPacketPayload[])new CustomPacketPayload[0]);
    }
}

