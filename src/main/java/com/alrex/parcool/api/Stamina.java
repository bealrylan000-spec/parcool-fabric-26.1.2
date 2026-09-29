package com.alrex.parcool.api;

import com.alrex.parcool.platform.attachment.AttachmentHolder;

import com.alrex.parcool.common.attachment.Attachments;
import com.alrex.parcool.common.attachment.client.LocalStamina;
import com.alrex.parcool.common.attachment.common.ReadonlyStamina;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import com.alrex.parcool.platform.dist.Dist;
import com.alrex.parcool.platform.dist.OnlyIn;

public class Stamina {
    private final Player player;

    public static Stamina get(Player player) {
        return new Stamina(player);
    }

    private Stamina(Player player) {
        this.player = player;
    }

    public int getMaxValue() {
        return ((ReadonlyStamina)AttachmentHolder.getData(this.player, Attachments.STAMINA)).max();
    }

    public int getValue() {
        return ((ReadonlyStamina)AttachmentHolder.getData(this.player, Attachments.STAMINA)).value();
    }

    public boolean isExhausted() {
        return ((ReadonlyStamina)AttachmentHolder.getData(this.player, Attachments.STAMINA)).isExhausted();
    }

    @OnlyIn(value=Dist.CLIENT)
    public void consume(int value) {
        Player player = this.player;
        if (!(player instanceof LocalPlayer)) {
            return;
        }
        LocalPlayer localPlayer = (LocalPlayer)player;
        LocalStamina stamina = LocalStamina.get(localPlayer);
        stamina.consume(localPlayer, value);
    }

    @OnlyIn(value=Dist.CLIENT)
    public void recover(int value) {
        Player player = this.player;
        if (!(player instanceof LocalPlayer)) {
            return;
        }
        LocalPlayer localPlayer = (LocalPlayer)player;
        LocalStamina stamina = LocalStamina.get(localPlayer);
        stamina.recover(localPlayer, value);
    }
}

