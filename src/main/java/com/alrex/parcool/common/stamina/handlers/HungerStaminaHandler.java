package com.alrex.parcool.common.stamina.handlers;

import com.alrex.parcool.common.attachment.common.ReadonlyStamina;
import com.alrex.parcool.common.network.payload.StaminaProcessOnServerPayload;
import com.alrex.parcool.common.stamina.IParCoolStaminaHandler;
import com.alrex.parcool.common.stamina.StaminaType;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import com.alrex.parcool.platform.dist.Dist;
import com.alrex.parcool.platform.dist.OnlyIn;
import com.alrex.parcool.platform.network.PacketDistributor;

public class HungerStaminaHandler
implements IParCoolStaminaHandler {
    private int consumed = 0;

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public ReadonlyStamina initializeStamina(LocalPlayer player, ReadonlyStamina current) {
        return new ReadonlyStamina(false, player.getFoodData().getFoodLevel(), 20);
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public ReadonlyStamina consume(LocalPlayer player, ReadonlyStamina current, int value) {
        this.consumed += value;
        return current;
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public ReadonlyStamina recover(LocalPlayer player, ReadonlyStamina current, int value) {
        return current;
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public ReadonlyStamina onTick(LocalPlayer player, ReadonlyStamina current) {
        if (this.consumed > 0) {
            PacketDistributor.sendToServer((CustomPacketPayload)new StaminaProcessOnServerPayload(StaminaType.HUNGER, this.consumed), (CustomPacketPayload[])new CustomPacketPayload[0]);
            this.consumed = 0;
        }
        return new ReadonlyStamina(player.getFoodData().getFoodLevel() < 6, player.getFoodData().getFoodLevel(), 20);
    }

    @Override
    public void processOnServer(Player player, int value) {
        player.causeFoodExhaustion((float)value / 1000.0f);
    }
}

