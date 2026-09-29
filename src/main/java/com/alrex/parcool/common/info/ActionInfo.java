package com.alrex.parcool.common.info;

import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.attachment.client.LocalStamina;
import com.alrex.parcool.common.info.ClientSetting;
import com.alrex.parcool.common.info.ServerLimitation;
import com.alrex.parcool.common.stamina.StaminaType;
import com.alrex.parcool.config.ParCoolConfig;
import net.minecraft.client.player.LocalPlayer;
import com.alrex.parcool.platform.dist.Dist;
import com.alrex.parcool.platform.dist.OnlyIn;

public class ActionInfo {
    private ClientSetting clientSetting = ClientSetting.UNSYNCED_INSTANCE;
    private ServerLimitation serverLimitation = ServerLimitation.UNSYNCED_INSTANCE;

    public ClientSetting getClientSetting() {
        return this.clientSetting;
    }

    public void setClientSetting(ClientSetting clientSetting) {
        this.clientSetting = clientSetting;
    }

    public ServerLimitation getServerLimitation() {
        return this.serverLimitation;
    }

    public void setServerLimitation(ServerLimitation serverLimitation) {
        this.serverLimitation = serverLimitation;
    }

    public boolean can(Class<? extends Action> action) {
        return this.getClientSetting().get(ParCoolConfig.Client.Booleans.ParCoolIsActive) != false && this.getClientSetting().getPossibilityOf(action) && this.getServerLimitation().isPermitted(action);
    }

    public StaminaType getStaminaType() {
        StaminaType forcedStamina = this.getServerLimitation().getForcedStamina();
        if (forcedStamina == StaminaType.NONE) {
            StaminaType requestedStamina = this.getClientSetting().getRequestedStamina();
            if (requestedStamina == StaminaType.NONE) {
                return this.isInfiniteStaminaPermitted() ? StaminaType.NONE : StaminaType.PARCOOL;
            }
            return requestedStamina;
        }
        return forcedStamina;
    }

    public int getStaminaConsumptionOf(Class<? extends Action> action) {
        return Math.max(this.getClientSetting().getStaminaConsumptionOf(action), this.getServerLimitation().getStaminaConsumptionOf(action));
    }

    public int getStaminaRecoveryLimit() {
        return this.getServerLimitation().get(ParCoolConfig.Server.Integers.MaxStaminaRecovery);
    }

    public int getMaxStaminaLimit() {
        return this.getServerLimitation().get(ParCoolConfig.Server.Integers.MaxStaminaLimit);
    }

    @OnlyIn(value=Dist.CLIENT)
    public boolean isStaminaInfinite(LocalStamina stamina, LocalPlayer player) {
        return stamina.isInfinite(player);
    }

    public boolean isInfiniteStaminaPermitted() {
        return this.serverLimitation.get(ParCoolConfig.Server.Booleans.AllowInfiniteStamina);
    }

    @OnlyIn(value=Dist.CLIENT)
    public void updateStaminaType(LocalStamina stamina, LocalPlayer player) {
        stamina.changeType(player, this.getStaminaType());
    }
}

