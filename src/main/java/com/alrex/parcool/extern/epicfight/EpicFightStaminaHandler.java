package com.alrex.parcool.extern.epicfight;

import com.alrex.parcool.common.attachment.common.ReadonlyStamina;
import com.alrex.parcool.common.network.payload.StaminaProcessOnServerPayload;
import com.alrex.parcool.common.stamina.IParCoolStaminaHandler;
import com.alrex.parcool.common.stamina.StaminaType;
import com.alrex.parcool.extern.AdditionalMods;
import javax.annotation.Nullable;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import com.alrex.parcool.platform.dist.Dist;
import com.alrex.parcool.platform.dist.OnlyIn;
import com.alrex.parcool.platform.network.PacketDistributor;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

public class EpicFightStaminaHandler
implements IParCoolStaminaHandler {
    private float consumed = 0.0f;

    private ReadonlyStamina readCurrentStamina(Player player, @Nullable ReadonlyStamina current) {
        PlayerPatch<?> patch = AdditionalMods.epicFight().getPlayerPatch(player);
        if (patch == null) {
            if (current == null) {
                return ReadonlyStamina.createDefault();
            }
            return current;
        }
        return new ReadonlyStamina(!patch.hasStamina(1.0f), (int)patch.getStamina(), (int)patch.getMaxStamina());
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public ReadonlyStamina initializeStamina(LocalPlayer player, ReadonlyStamina current) {
        return this.readCurrentStamina((Player)player, current);
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public ReadonlyStamina consume(LocalPlayer player, ReadonlyStamina current, int value) {
        this.consumed += (float)value / 60.0f;
        return current;
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public ReadonlyStamina recover(LocalPlayer player, ReadonlyStamina current, int value) {
        this.consumed -= (float)value / 60.0f;
        return current;
    }

    @Override
    public ReadonlyStamina onTick(LocalPlayer player, ReadonlyStamina current) {
        if (this.consumed != 0.0f) {
            PacketDistributor.sendToServer((CustomPacketPayload)new StaminaProcessOnServerPayload(StaminaType.EPIC_FIGHT, (int)(this.consumed * 2048.0f)), (CustomPacketPayload[])new CustomPacketPayload[0]);
            this.consumed = 0.0f;
        }
        return this.readCurrentStamina((Player)player, current);
    }

    @Override
    public boolean shouldImposeExhaustionPenalty(LocalPlayer player, ReadonlyStamina current) {
        return false;
    }

    @Override
    public void processOnServer(Player player, int value) {
        float consumedValue = (float)value / 2048.0f;
        PlayerPatch<?> patch = AdditionalMods.epicFight().getPlayerPatch(player);
        if (patch == null) {
            return;
        }
        patch.resetActionTick();
        patch.setStamina(patch.getStamina() - consumedValue);
    }

    @Override
    public boolean isExternalStamina() {
        return true;
    }
}

