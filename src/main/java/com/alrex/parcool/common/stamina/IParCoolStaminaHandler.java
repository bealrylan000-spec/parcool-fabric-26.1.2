package com.alrex.parcool.common.stamina;

import com.alrex.parcool.common.attachment.common.ReadonlyStamina;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import com.alrex.parcool.platform.dist.Dist;
import com.alrex.parcool.platform.dist.OnlyIn;

public interface IParCoolStaminaHandler {
    @OnlyIn(value=Dist.CLIENT)
    public ReadonlyStamina initializeStamina(LocalPlayer var1, ReadonlyStamina var2);

    @OnlyIn(value=Dist.CLIENT)
    public ReadonlyStamina consume(LocalPlayer var1, ReadonlyStamina var2, int var3);

    @OnlyIn(value=Dist.CLIENT)
    public ReadonlyStamina recover(LocalPlayer var1, ReadonlyStamina var2, int var3);

    @OnlyIn(value=Dist.CLIENT)
    default public ReadonlyStamina onTick(LocalPlayer player, ReadonlyStamina current) {
        return current;
    }

    @OnlyIn(value=Dist.CLIENT)
    default public boolean shouldShowHUD(LocalPlayer player) {
        return false;
    }

    @OnlyIn(value=Dist.CLIENT)
    default public boolean shouldImposeExhaustionPenalty(LocalPlayer player, ReadonlyStamina current) {
        return true;
    }

    default public void processOnServer(Player player, int value) {
    }

    default public boolean isExternalStamina() {
        return false;
    }
}

