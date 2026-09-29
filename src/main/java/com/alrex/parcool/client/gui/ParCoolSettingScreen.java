package com.alrex.parcool.client.gui;

import com.alrex.parcool.client.gui.ColorTheme;
import com.alrex.parcool.client.gui.SettingActionLimitationScreen;
import com.alrex.parcool.client.gui.SettingBooleanConfigScreen;
import com.alrex.parcool.client.gui.SettingEnumConfigScreen;
import com.alrex.parcool.client.gui.SettingShowLimitationsScreen;
import com.alrex.parcool.common.info.ActionInfo;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;

public abstract class ParCoolSettingScreen
extends Screen {
    private final ScreenSet<?>[] screenList;
    protected int currentScreen = 0;
    protected int topIndex = 0;
    protected int viewableItemCount = 0;
    protected static final int Checkbox_Item_Height = 18;
    protected final ColorTheme color;
    protected final BooleanSupplier serverPermissionReceived = info.getServerLimitation()::isSynced;
    private static final Component MenuTitle = Component.translatable((String)"parcool.gui.title.setting");
    protected static final Component Header_ActionName = Component.translatable((String)"parcool.gui.text.actionName");
    protected static final Component Header_Limitation = Component.literal((String)"L");
    protected static final Component Header_Limitation_Text = Component.translatable((String)"parcool.gui.text.limitation");
    protected static final Component Permission_Permitted = Component.literal((String)"\u2713");
    protected static final Component Permission_Denied = Component.literal((String)"\u00d7");
    protected static final Component Permission_Not_Received = Component.literal((String)"\u00a74[Error] Permissions are not sent from a server.\n\nBy closing this setting menu, permissions will be sent again.\nIf it were not done, please report to the mod developer after checking whether ParCool is installed and re-login to the server.\u00a7r");

    public ParCoolSettingScreen(Component titleIn, ActionInfo info, ColorTheme theme) {
        super(titleIn);
        this.color = theme;
        this.screenList = new ScreenSet[]{new ScreenSet<SettingActionLimitationScreen>((Component)Component.translatable((String)"parcool.gui.text.action"), () -> new SettingActionLimitationScreen(this.title, info, theme)), new ScreenSet<SettingBooleanConfigScreen>((Component)Component.literal((String)(I18n.get((String)"parcool.gui.text.config", (Object[])new Object[0]) + "1")), () -> new SettingBooleanConfigScreen(titleIn, info, theme)), new ScreenSet<SettingEnumConfigScreen>((Component)Component.literal((String)(I18n.get((String)"parcool.gui.text.config", (Object[])new Object[0]) + "2")), () -> new SettingEnumConfigScreen(titleIn, info, theme)), new ScreenSet<SettingShowLimitationsScreen>((Component)Component.translatable((String)"parcool.gui.text.limitation"), () -> new SettingShowLimitationsScreen(titleIn, info, theme))};
    }

    public void resize(@Nonnull Minecraft minecraft, int p_231152_2_, int p_231152_3_) {
        super.resize(minecraft, p_231152_2_, p_231152_3_);
        this.mouseScrolled(0.0, 0.0, 0.0);
    }

    public void render(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float p_230430_4_) {
        super.render(graphics, mouseX, mouseY, p_230430_4_);
        this.renderBackground(graphics, mouseX, mouseY, p_230430_4_);
        Objects.requireNonNull(this.font);
        int topBarHeight = 9 * 2;
        int topBarItemWidth = (int)(1.2 * (double)Arrays.stream(this.screenList).map(it -> this.font.width((FormattedText)it.title)).max(Integer::compareTo).orElse(0).intValue());
        int topBarOffsetX = this.width - topBarItemWidth * this.screenList.length;
        graphics.fillGradient(0, 0, this.width, topBarHeight, this.color.getTopBar1(), this.color.getTopBar2());
        this.renderSubHeaderAndFooter(graphics, this.screenList[this.currentScreen].title, this.isDownScrollable(), topBarHeight);
        Objects.requireNonNull(this.font);
        Objects.requireNonNull(this.font);
        this.renderContents(graphics, mouseX, mouseY, p_230430_4_, topBarHeight + 9 * 2, 9 * 2);
        for (int i = 0; i < this.screenList.length; ++i) {
            ScreenSet<?> item = this.screenList[i];
            item.y = 0;
            item.x = topBarOffsetX + i * topBarItemWidth;
            item.width = topBarItemWidth;
            item.height = topBarHeight;
            boolean selected = this.currentScreen == i || item.isMouseIn(mouseX, mouseY);
            graphics.drawCenteredString(this.font, item.title, topBarOffsetX + i * topBarItemWidth + topBarItemWidth / 2, topBarHeight / 4 + 1, selected ? this.color.getText() : this.color.getSubText());
            graphics.fill(item.x, 2, item.x + 1, topBarHeight - 3, this.color.getSeparator());
        }
        graphics.fill(0, topBarHeight - 1, this.width, topBarHeight, this.color.getSeparator());
        int titleOffset = 0;
        if (!this.serverPermissionReceived.getAsBoolean()) {
            graphics.fill(2, 2, topBarHeight - 3, topBarHeight - 3, -1118482);
            graphics.fill(3, 3, topBarHeight - 4, topBarHeight - 4, -1179648);
            int n = topBarHeight / 2;
            Objects.requireNonNull(this.font);
            graphics.drawCenteredString(this.font, "!", n, (topBarHeight - 9) / 2 + 1, 0xEEEEEE);
            if (2 <= mouseX && mouseX < topBarHeight - 3 && 1 <= mouseY && mouseY < topBarHeight - 3) {
                graphics.renderComponentTooltip(this.font, Collections.singletonList(Permission_Not_Received), mouseX, mouseY);
            }
            titleOffset = topBarHeight;
        }
        graphics.drawString(this.font, MenuTitle, titleOffset + 5, topBarHeight / 4 + 1, this.color.getText());
    }

    protected abstract void renderContents(GuiGraphics var1, int var2, int var3, float var4, int var5, int var6);

    protected void save() {
    }

    protected boolean isDownScrollable() {
        return false;
    }

    private void renderSubHeaderAndFooter(GuiGraphics graphics, Component title, boolean scrollable, int topOffset) {
        Objects.requireNonNull(this.font);
        int headerHeight = 9 * 2;
        graphics.fillGradient(0, topOffset, this.width, topOffset + headerHeight, this.color.getHeader1(), this.color.getHeader2());
        graphics.fillGradient(0, this.height - headerHeight, this.width, this.height, this.color.getHeader1(), this.color.getHeader2());
        int n = this.width / 2;
        Objects.requireNonNull(this.font);
        graphics.drawCenteredString(this.font, title, n, topOffset + 9 / 2 + 2, this.color.getStrongText());
        if (scrollable) {
            MutableComponent mutableComponent = Component.literal((String)"\u2193");
            int n2 = this.width / 2;
            Objects.requireNonNull(this.font);
            Objects.requireNonNull(this.font);
            graphics.drawCenteredString(this.font, (Component)mutableComponent, n2, this.height - 9 - 9 / 2, this.color.getStrongText());
        }
    }

    public void renderBackground(@Nonnull GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.fill(0, 0, this.width, this.height, this.color.getBackground());
    }

    public void onClose() {
        this.save();
        super.onClose();
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        switch (keyCode) {
            case 262: {
                this.save();
                Minecraft.getInstance().setScreen((Screen)this.screenList[(this.currentScreen + 1) % this.screenList.length].screenSupplier.get());
                break;
            }
            case 263: {
                this.save();
                Minecraft.getInstance().setScreen((Screen)this.screenList[(this.currentScreen - 1) % this.screenList.length].screenSupplier.get());
                break;
            }
            case 265: {
                this.mouseScrolled(0.0, 0.0, 1.0);
                break;
            }
            case 264: {
                this.mouseScrolled(0.0, 0.0, -1.0);
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        return this.mouseScrolled(mouseX, mouseY, scrollY);
    }

    public boolean mouseScrolled(double x, double y, double value) {
        int scroll = (int)(-Math.signum(value));
        if (scroll <= 0 || this.isDownScrollable()) {
            this.topIndex += scroll;
        }
        if (this.topIndex < 0) {
            this.topIndex = 0;
        }
        return true;
    }

    public boolean mouseClicked(double mouseX, double mouseY, int type) {
        for (ScreenSet<?> modeSet : this.screenList) {
            if (!modeSet.isMouseIn((int)mouseX, (int)mouseY) || type != 0) continue;
            this.save();
            Minecraft.getInstance().setScreen((Screen)modeSet.screenSupplier.get());
            return true;
        }
        return false;
    }

    private static class ScreenSet<T extends ParCoolSettingScreen> {
        final Supplier<T> screenSupplier;
        final Component title;
        int x;
        int y;
        int width;
        int height;

        boolean isMouseIn(double mouseX, double mouseY) {
            return (double)this.x < mouseX && mouseX < (double)(this.x + this.width) && (double)this.y < mouseY && mouseY < (double)(this.y + this.height);
        }

        public ScreenSet(Component title, Supplier<T> supplier) {
            this.title = title;
            this.screenSupplier = supplier;
        }
    }
}

