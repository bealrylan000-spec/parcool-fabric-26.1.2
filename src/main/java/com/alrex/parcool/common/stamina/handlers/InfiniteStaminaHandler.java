package com.alrex.parcool.common.stamina.handlers;

import com.alrex.parcool.common.attachment.common.ReadonlyStamina;
import com.alrex.parcool.common.stamina.IParCoolStaminaHandler;
import net.minecraft.client.player.LocalPlayer;
import com.alrex.parcool.platform.dist.Dist;
import com.alrex.parcool.platform.dist.OnlyIn;

public class InfiniteStaminaHandler
implements IParCoolStaminaHandler {
    private static final ReadonlyStamina INSTANCE = new ReadonlyStamina(false, 1, 1);

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public ReadonlyStamina initializeStamina(LocalPlayer player, ReadonlyStamina current) {
        return INSTANCE;
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public ReadonlyStamina consume(LocalPlayer player, ReadonlyStamina current, int value) {
        return INSTANCE;
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public ReadonlyStamina recover(LocalPlayer player, ReadonlyStamina current, int value) {
        return INSTANCE;
    }
}

