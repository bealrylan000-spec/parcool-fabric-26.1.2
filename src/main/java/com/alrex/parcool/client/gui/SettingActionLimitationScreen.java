package com.alrex.parcool.client.gui;

import com.alrex.parcool.client.gui.ColorTheme;
import com.alrex.parcool.client.gui.ParCoolSettingScreen;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.Actions;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.common.info.ActionInfo;
import com.alrex.parcool.common.info.ClientSetting;
import com.alrex.parcool.common.network.payload.ClientInformationPayload;
import com.alrex.parcool.config.ParCoolConfig;
import java.util.Collections;
import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import com.alrex.parcool.platform.config.ModConfigSpec;
import com.alrex.parcool.platform.network.PacketDistributor;

public class SettingActionLimitationScreen
extends ParCoolSettingScreen {
    private final ActionConfigSet[] actionList = new ActionConfigSet[Actions.LIST.size()];
    private final Checkbox[] actionButtons = new Checkbox[this.actionList.length];

    public SettingActionLimitationScreen(Component titleIn, ActionInfo info, ColorTheme theme) {
        super(titleIn, info, theme);
        this.currentScreen = 0;
        for (int i = 0; i < this.actionList.length; ++i) {
            this.actionList[i] = new ActionConfigSet(Actions.getByIndex(i), info);
            this.actionButtons[i] = Checkbox.builder((Component)Component.literal((String)this.actionList[i].name), (Font)Minecraft.getInstance().font).selected(this.actionList[i].get()).pos(0, 0).build();
            this.actionButtons[i].setHeight(18);
        }
    }

    @Override
    public boolean isDownScrollable() {
        return this.topIndex + this.viewableItemCount < this.actionButtons.length;
    }

    @Override
    public void renderContents(GuiGraphics graphics, int mouseX, int mouseY, float partialTick, int topOffset, int bottomOffset) {
        int offsetX = 40;
        Objects.requireNonNull(this.font);
        int headerHeight = (int)(9.0f * 1.5f);
        int contentOffsetY = topOffset + headerHeight + 2;
        int permissionColumnWidth = this.font.width((FormattedText)Permission_Permitted) * 5;
        int nameColumnWidth = this.width - 80 - permissionColumnWidth;
        int contentHeight = this.height - contentOffsetY - bottomOffset;
        this.viewableItemCount = contentHeight / 18;
        int n = topOffset + headerHeight / 2;
        Objects.requireNonNull(this.font);
        int headerTextY = n - 9 / 2 + 1;
        graphics.drawString(this.font, Header_ActionName, 45, headerTextY, this.color.getText());
        graphics.drawCenteredString(this.font, Header_Limitation, 40 + nameColumnWidth + permissionColumnWidth / 2, headerTextY, this.color.getText());
        for (Checkbox actionButton : this.actionButtons) {
            actionButton.setWidth(0);
        }
        graphics.fill(40, contentOffsetY, this.width - 40, contentOffsetY - 1, this.color.getSeparator());
        graphics.fill(40, topOffset, 41, this.height - bottomOffset, this.color.getSeparator());
        graphics.fill(40 + nameColumnWidth, topOffset, 40 + nameColumnWidth + 1, this.height - bottomOffset, this.color.getSeparator());
        graphics.fill(40 + nameColumnWidth + permissionColumnWidth, topOffset, 40 + nameColumnWidth + permissionColumnWidth + 1, this.height - bottomOffset, this.color.getSeparator());
        for (int i = 0; i < this.viewableItemCount && i + this.topIndex < this.actionButtons.length; ++i) {
            Checkbox button = this.actionButtons[i + this.topIndex];
            button.setX(41);
            button.setY(contentOffsetY + 18 * i);
            button.setWidth(nameColumnWidth - 5);
            button.setHeight(17);
            button.render(graphics, mouseX, mouseY, partialTick);
            graphics.fill(40, button.getY() + button.getHeight(), this.width - 40, button.getY() + button.getHeight() + 1, this.color.getSubSeparator());
            int rowY = contentOffsetY + 18 * i + 9;
            boolean permitted = this.actionList[this.topIndex + i].serverLimitation.getAsBoolean();
            Component component = permitted ? Permission_Permitted : Permission_Denied;
            int n2 = 40 + nameColumnWidth + permissionColumnWidth / 2;
            Objects.requireNonNull(this.font);
            graphics.drawCenteredString(this.font, component, n2, rowY - 9 / 2, permitted ? 43520 : 0xAA0000);
        }
        int columnCenter = 40 + nameColumnWidth + permissionColumnWidth / 2;
        if (topOffset < mouseY && mouseY < topOffset + headerHeight && columnCenter - permissionColumnWidth / 2 < mouseX && mouseX < columnCenter + permissionColumnWidth / 2) {
            graphics.renderComponentTooltip(this.font, Collections.singletonList(Header_Limitation_Text), mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int type) {
        for (Checkbox button : this.actionButtons) {
            if (!button.mouseClicked(mouseX, mouseY, type)) continue;
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, type);
    }

    @Override
    protected void save() {
        for (int i = 0; i < this.actionList.length; ++i) {
            this.actionList[i].set(this.actionButtons[i].selected());
            this.actionList[i].save();
        }
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }
        Parkourability parkourability = Parkourability.get((Player)player);
        parkourability.getActionInfo().setClientSetting(ClientSetting.readFromLocalConfig());
        PacketDistributor.sendToServer((CustomPacketPayload)new ClientInformationPayload(player.getUUID(), true, parkourability.getClientInfo()), (CustomPacketPayload[])new CustomPacketPayload[0]);
    }

    private static class ActionConfigSet {
        private final String name;
        private final Consumer<Boolean> setter;
        private final BooleanSupplier getter;
        private final BooleanSupplier serverLimitation;
        private final ModConfigSpec.BooleanValue configInstance;
        private boolean dirty = false;

        ActionConfigSet(Class<? extends Action> action, ActionInfo info) {
            this.name = Component.translatable((String)("parcool.action." + action.getSimpleName())).getString();
            this.configInstance = ParCoolConfig.Client.getInstance().getPossibilityOf(action);
            this.setter = arg_0 -> ((ModConfigSpec.BooleanValue)this.configInstance).set(arg_0);
            this.getter = () -> ((ModConfigSpec.BooleanValue)this.configInstance).get();
            this.serverLimitation = () -> info.getServerLimitation().isPermitted(action);
        }

        private boolean get() {
            return this.getter.getAsBoolean();
        }

        private boolean getServerLimitation() {
            return this.serverLimitation.getAsBoolean();
        }

        private void set(boolean value) {
            this.setter.accept(value);
            this.dirty = true;
        }

        private void save() {
            if (this.dirty) {
                this.configInstance.save();
            }
        }
    }
}

