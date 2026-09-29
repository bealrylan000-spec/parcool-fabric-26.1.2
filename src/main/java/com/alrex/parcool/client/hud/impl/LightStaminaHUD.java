package com.alrex.parcool.client.hud.impl;

import com.alrex.parcool.platform.attachment.AttachmentHolder;

import com.alrex.parcool.api.Effects;
import com.alrex.parcool.client.hud.impl.StaminaHUD;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.attachment.Attachments;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.common.attachment.common.ReadonlyStamina;
import com.alrex.parcool.config.ParCoolConfig;
import com.alrex.parcool.utilities.MathUtil;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import com.alrex.parcool.platform.event.client.ClientTickEvent;

public class LightStaminaHUD {
    private long lastStaminaChangedTick = 0L;
    private int lastChangingSign = 0;
    private int changingSign = 0;
    private long changingTimeTick = 0L;
    private int randomOffset = 0;
    private boolean justBecameMax = false;
    private float statusValue = 0.0f;
    private float oldStatusValue = 0.0f;
    private boolean showStatus = false;
    private int oldValue = 0;

    public void onTick(ClientTickEvent.Post event, LocalPlayer player) {
        Parkourability parkourability = Parkourability.get((Player)player);
        if (parkourability == null) {
            return;
        }
        ReadonlyStamina stamina = (ReadonlyStamina)AttachmentHolder.getData(player, Attachments.STAMINA);
        int newValue = stamina.value();
        this.changingSign = (int)Math.signum(newValue - this.oldValue);
        long gameTime = player.getCommandSenderWorld().getGameTime();
        if (this.changingSign != this.lastChangingSign) {
            this.lastChangingSign = this.changingSign;
            this.changingTimeTick = 0L;
        } else {
            ++this.changingTimeTick;
        }
        this.randomOffset = player.getRandom().nextInt(5) == 0 ? (this.randomOffset += player.getRandom().nextBoolean() ? 1 : -1) : 0;
        if (newValue != this.oldValue || stamina.isExhausted()) {
            this.lastStaminaChangedTick = gameTime;
        }
        this.justBecameMax = this.oldValue < newValue && newValue == stamina.max();
        this.oldStatusValue = this.statusValue;
        boolean oldShowStatus = this.showStatus;
        this.showStatus = false;
        if (ParCoolConfig.Client.Booleans.ShowActionStatusBar.get().booleanValue()) {
            for (Action a : parkourability.getList()) {
                if (!a.wantsToShowStatusBar(player, parkourability)) continue;
                this.showStatus = true;
                this.statusValue = a.getStatusValue(player, parkourability);
                if (this.statusValue > 1.0f) {
                    this.statusValue = 1.0f;
                    break;
                }
                if (!(this.statusValue < 0.0f)) break;
                this.statusValue = 0.0f;
                break;
            }
        }
        if (!oldShowStatus && this.showStatus) {
            this.oldStatusValue = this.statusValue;
        }
        this.oldValue = newValue;
    }

    public void render(GuiGraphics graphics, Parkourability parkourability, ReadonlyStamina stamina, float partialTick) {
        long gameTime;
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }
        boolean inexhaustible = player.hasEffect(Effects.INEXHAUSTIBLE);
        boolean exhausted = stamina.isExhausted();
        if (!this.showStatus && (gameTime = player.level().getGameTime()) - this.lastStaminaChangedTick > 40L && !ParCoolConfig.Client.Booleans.ShowLightStaminaHUDAlways.get().booleanValue()) {
            return;
        }
        float staminaScale = (float)stamina.value() / (float)stamina.max();
        if (staminaScale < 0.0f) {
            staminaScale = 0.0f;
        }
        if (staminaScale > 1.0f) {
            staminaScale = 1.0f;
        }
        staminaScale *= 10.0f;
        float statusScale = this.showStatus ? MathUtil.lerp(this.oldStatusValue, this.statusValue, partialTick) * 10.0f : 0.0f;
        RenderSystem.setShaderColor((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        RenderSystem.setShaderTexture((int)0, (ResourceLocation)StaminaHUD.STAMINA);
        int width = graphics.guiWidth();
        int height = graphics.guiHeight();
        int baseX = width / 2 + 91 + ParCoolConfig.Client.Integers.HorizontalOffsetOfLightStaminaHUD.get();
        int baseY = height - Minecraft.getInstance().gui.rightHeight + ParCoolConfig.Client.Integers.VerticalOffsetOfLightStaminaHUD.get();
        for (int i = 0; i < 10; ++i) {
            int x = baseX - i * 8 - 9;
            int offsetY = 0;
            int textureX = inexhaustible ? (this.showStatus ? (statusScale > (float)i + 0.9f ? 90 : 0) : 54) : (exhausted ? 27 : (statusScale > (float)i + 0.9f ? 90 : 0));
            if (this.justBecameMax) {
                textureX = 81;
            } else if (staminaScale < (float)i) {
                textureX += 18;
            } else if (staminaScale < (float)i + 0.5f) {
                textureX += 9;
            }
            if (this.justBecameMax) {
                offsetY = -1;
            } else if (this.changingSign == 1) {
                if ((this.changingTimeTick & 0x1FL) == (long)i) {
                    offsetY = -1;
                }
            } else if ((float)(i + 1) > staminaScale && staminaScale > (float)i && this.changingSign == -1) {
                offsetY = this.randomOffset;
            }
            graphics.blit(StaminaHUD.STAMINA, x, baseY + offsetY, (float)textureX, 119.0f, 9, 9, 128, 128);
        }
        Minecraft.getInstance().gui.rightHeight += 10;
    }
}

