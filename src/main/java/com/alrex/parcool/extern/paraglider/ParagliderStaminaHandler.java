package com.alrex.parcool.extern.paraglider;

import com.alrex.parcool.common.attachment.common.ReadonlyStamina;
import com.alrex.parcool.common.stamina.IParCoolStaminaHandler;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import com.alrex.parcool.platform.dist.Dist;
import com.alrex.parcool.platform.dist.OnlyIn;
import tictim.paraglider.api.stamina.Stamina;

public class ParagliderStaminaHandler
implements IParCoolStaminaHandler {
    private Stamina getInternalInstance(Player player) {
        return Stamina.get((Player)player);
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public ReadonlyStamina initializeStamina(LocalPlayer player, ReadonlyStamina current) {
        Stamina stamina = this.getInternalInstance((Player)player);
        return new ReadonlyStamina(false, (int)stamina.stamina(), (int)Math.ceil(stamina.maxStamina()));
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public ReadonlyStamina consume(LocalPlayer player, ReadonlyStamina current, int value) {
        return current;
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public ReadonlyStamina recover(LocalPlayer player, ReadonlyStamina current, int value) {
        return current;
    }

    @Override
    public ReadonlyStamina onTick(LocalPlayer player, ReadonlyStamina current) {
        Stamina stamina = this.getInternalInstance((Player)player);
        return new ReadonlyStamina(stamina.isDepleted(), (int)stamina.stamina(), (int)Math.ceil(stamina.maxStamina()));
    }

    @Override
    public boolean isExternalStamina() {
        return true;
    }
}

