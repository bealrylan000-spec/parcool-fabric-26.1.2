package com.alrex.parcool.common.attachment.client;

import com.alrex.parcool.platform.attachment.AttachmentHolder;

import com.alrex.parcool.api.Effects;
import com.alrex.parcool.common.attachment.Attachments;
import com.alrex.parcool.common.attachment.ClientAttachments;
import com.alrex.parcool.common.attachment.common.ReadonlyStamina;
import com.alrex.parcool.common.stamina.IParCoolStaminaHandler;
import com.alrex.parcool.common.stamina.StaminaType;
import com.alrex.parcool.common.stamina.handlers.InfiniteStaminaHandler;
import javax.annotation.Nullable;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import com.alrex.parcool.platform.dist.Dist;
import com.alrex.parcool.platform.dist.OnlyIn;

@OnlyIn(value=Dist.CLIENT)
public class LocalStamina {
    @Nullable
    private StaminaType currentType = null;
    @Nullable
    private IParCoolStaminaHandler handler = null;
    private ReadonlyStamina oldStamina = ReadonlyStamina.createDefault();

    public static LocalStamina get(LocalPlayer player) {
        return (LocalStamina)AttachmentHolder.getData(player, ClientAttachments.LOCAL_STAMINA);
    }

    public boolean isAvailable() {
        return this.handler != null && this.currentType != null;
    }

    public boolean isInfinite(LocalPlayer player) {
        return player.isCreative() || player.isSpectator() || this.handler instanceof InfiniteStaminaHandler;
    }

    public void changeType(LocalPlayer player, StaminaType type) {
        this.currentType = type;
        this.handler = type.newHandler((Player)player);
        AttachmentHolder.setData(player, Attachments.STAMINA, (Object)this.handler.initializeStamina(player, (ReadonlyStamina)AttachmentHolder.getData(player, Attachments.STAMINA)));
    }

    @Nullable
    public IParCoolStaminaHandler getHandler() {
        return this.handler;
    }

    public boolean isExhausted(LocalPlayer player) {
        return ((ReadonlyStamina)AttachmentHolder.getData(player, Attachments.STAMINA)).isExhausted();
    }

    public int getValue(LocalPlayer player) {
        return ((ReadonlyStamina)AttachmentHolder.getData(player, Attachments.STAMINA)).value();
    }

    public int getMax(LocalPlayer player) {
        return ((ReadonlyStamina)AttachmentHolder.getData(player, Attachments.STAMINA)).max();
    }

    public void consume(LocalPlayer player, int value) {
        if (player.isCreative() || player.isSpectator()) {
            return;
        }
        if (this.handler == null) {
            return;
        }
        if (this.isInfinite(player)) {
            return;
        }
        if (player.hasEffect(Effects.INEXHAUSTIBLE)) {
            return;
        }
        AttachmentHolder.setData(player, Attachments.STAMINA, (Object)this.handler.consume(player, (ReadonlyStamina)AttachmentHolder.getData(player, Attachments.STAMINA), value));
    }

    public void recover(LocalPlayer player, int value) {
        if (player.isCreative() || player.isSpectator()) {
            return;
        }
        if (this.handler == null) {
            return;
        }
        AttachmentHolder.setData(player, Attachments.STAMINA, (Object)this.handler.recover(player, (ReadonlyStamina)AttachmentHolder.getData(player, Attachments.STAMINA), value));
    }

    public void onTick(LocalPlayer player) {
        if (this.handler == null) {
            return;
        }
        AttachmentHolder.setData(player, Attachments.STAMINA, (Object)this.handler.onTick(player, (ReadonlyStamina)AttachmentHolder.getData(player, Attachments.STAMINA)));
    }

    public boolean shouldShowHUD(LocalPlayer player) {
        if (this.handler == null) {
            return false;
        }
        return this.handler.shouldShowHUD(player);
    }

    public boolean imposeExhaustionPenalty(LocalPlayer player) {
        if (this.handler == null) {
            return false;
        }
        ReadonlyStamina current = (ReadonlyStamina)AttachmentHolder.getData(player, Attachments.STAMINA);
        return current.isExhausted() && this.handler.shouldImposeExhaustionPenalty(player, current);
    }

    public void sync(LocalPlayer player) {
        ReadonlyStamina stamina = (ReadonlyStamina)AttachmentHolder.getData(player, Attachments.STAMINA);
        if (!stamina.equals(this.oldStamina)) {
            stamina.sync(player);
        }
        this.oldStamina = stamina;
    }

    public boolean isUsingExternalStamina() {
        return this.handler != null && this.handler.isExternalStamina();
    }
}

