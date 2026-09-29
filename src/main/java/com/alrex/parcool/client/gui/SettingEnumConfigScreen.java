package com.alrex.parcool.client.gui;

import com.alrex.parcool.client.gui.ColorTheme;
import com.alrex.parcool.client.gui.ParCoolSettingScreen;
import com.alrex.parcool.client.hud.Position;
import com.alrex.parcool.client.hud.impl.HUDType;
import com.alrex.parcool.common.action.impl.ClingToCliff;
import com.alrex.parcool.common.action.impl.Crawl;
import com.alrex.parcool.common.action.impl.FastRun;
import com.alrex.parcool.common.action.impl.Flipping;
import com.alrex.parcool.common.action.impl.HorizontalWallRun;
import com.alrex.parcool.common.action.impl.Vault;
import com.alrex.parcool.common.action.impl.WallJump;
import com.alrex.parcool.common.attachment.client.LocalStamina;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.common.info.ActionInfo;
import com.alrex.parcool.common.info.ClientSetting;
import com.alrex.parcool.common.network.payload.ClientInformationPayload;
import com.alrex.parcool.common.stamina.StaminaType;
import com.alrex.parcool.config.ParCoolConfig;
import java.util.List;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import com.alrex.parcool.platform.config.ModConfigSpec;
import com.alrex.parcool.platform.network.PacketDistributor;

public class SettingEnumConfigScreen
extends ParCoolSettingScreen {
    private final EnumConfigSet<?>[] enumConfigList;
    private final Button[] enumConfigButtons;

    public SettingEnumConfigScreen(Component titleIn, ActionInfo info, ColorTheme theme) {
        super(titleIn, info, theme);
        this.enumConfigList = new EnumConfigSet[]{new EnumConfigSet<StaminaType>(ParCoolConfig.Client.getInstance().StaminaType), new EnumConfigSet<Position.Horizontal>(ParCoolConfig.Client.getInstance().AlignHorizontalStaminaHUD), new EnumConfigSet<Position.Vertical>(ParCoolConfig.Client.getInstance().AlignVerticalStaminaHUD), new EnumConfigSet<FastRun.ControlType>(ParCoolConfig.Client.getInstance().FastRunControl), new EnumConfigSet<Crawl.ControlType>(ParCoolConfig.Client.getInstance().CrawlControl), new EnumConfigSet<Flipping.ControlType>(ParCoolConfig.Client.getInstance().FlipControl), new EnumConfigSet<HorizontalWallRun.ControlType>(ParCoolConfig.Client.getInstance().HWallRunControl), new EnumConfigSet<ClingToCliff.ControlType>(ParCoolConfig.Client.getInstance().ClingToCliffControl), new EnumConfigSet<WallJump.ControlType>(ParCoolConfig.Client.getInstance().WallJumpControl), new EnumConfigSet<Vault.TypeSelectionMode>(ParCoolConfig.Client.getInstance().VaultAnimationMode), new EnumConfigSet<ColorTheme>(ParCoolConfig.Client.getInstance().GUIColorTheme), new EnumConfigSet<HUDType>(ParCoolConfig.Client.getInstance().StaminaHUDType)};
        this.enumConfigButtons = new Button[this.enumConfigList.length];
        this.currentScreen = 2;
        int i = 0;
        while (i < this.enumConfigList.length) {
            int index = i++;
            this.enumConfigButtons[index] = Button.builder((Component)Component.literal((String)((Enum)this.enumConfigList[index].get()).toString()), it -> {
                this.enumConfigList[index].next();
                it.setMessage((Component)Component.literal((String)((Enum)this.enumConfigList[index].get()).toString()));
            }).build();
        }
    }

    @Override
    protected boolean isDownScrollable() {
        return this.topIndex + this.viewableItemCount < this.enumConfigButtons.length;
    }

    @Override
    protected void renderContents(GuiGraphics graphics, int mouseX, int mouseY, float partialTick, int topOffset, int bottomOffset) {
        int offsetX = 40;
        int boxHeight = 21;
        int contentWidth = this.width - 80;
        int contentHeight = this.height - topOffset - bottomOffset;
        this.viewableItemCount = contentHeight / 21;
        for (Button configButton : this.enumConfigButtons) {
            configButton.setWidth(0);
        }
        for (int i = 0; i < this.viewableItemCount && i + this.topIndex < this.enumConfigList.length; ++i) {
            Button button = this.enumConfigButtons[i + this.topIndex];
            int buttonWidth = contentWidth / 3;
            button.setX(this.width - 40 - buttonWidth);
            button.setY(topOffset + 21 * i);
            button.setWidth(buttonWidth);
            button.setHeight(20);
            button.render(graphics, mouseX, mouseY, partialTick);
            List path = this.enumConfigList[i + this.topIndex].configInstance.getPath();
            String string = (String)path.get(path.size() - 1);
            int n = button.getY() + 1;
            int n2 = button.getHeight();
            Objects.requireNonNull(this.font);
            graphics.drawString(this.font, string, 46, n + (n2 - 9) / 2, this.color.getText());
            graphics.fill(40, button.getY() + button.getHeight(), this.width - 40, button.getY() + button.getHeight() + 1, this.color.getSubSeparator());
        }
        graphics.fill(this.width - 40, topOffset, this.width - 40 - 1, topOffset + contentHeight, this.color.getSeparator());
        graphics.fill(40, topOffset, 41, topOffset + contentHeight, this.color.getSeparator());
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int type) {
        for (Button button : this.enumConfigButtons) {
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
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }
        for (EnumConfigSet<?> configItem : this.enumConfigList) {
            configItem.save();
        }
        Parkourability parkourability = Parkourability.get((Player)player);
        parkourability.getActionInfo().setClientSetting(ClientSetting.readFromLocalConfig());
        parkourability.getActionInfo().updateStaminaType(LocalStamina.get(player), player);
        PacketDistributor.sendToServer((CustomPacketPayload)new ClientInformationPayload(player.getUUID(), true, parkourability.getClientInfo()), (CustomPacketPayload[])new CustomPacketPayload[0]);
    }

    private static class EnumConfigSet<T extends Enum<T>> {
        final ModConfigSpec.EnumValue<T> configInstance;
        final T[] values;
        private boolean dirty = false;

        public EnumConfigSet(ModConfigSpec.EnumValue<T> configInstance) {
            this.configInstance = configInstance;
            this.values = (Enum[])((Enum)configInstance.get()).getDeclaringClass().getEnumConstants();
        }

        public void next() {
            int index = (((Enum)this.configInstance.get()).ordinal() + 1) % this.values.length;
            this.configInstance.set(this.values[index]);
            this.dirty = true;
        }

        public T get() {
            return (T)((Enum)this.configInstance.get());
        }

        public void save() {
            if (this.dirty) {
                this.configInstance.save();
            }
        }
    }
}

