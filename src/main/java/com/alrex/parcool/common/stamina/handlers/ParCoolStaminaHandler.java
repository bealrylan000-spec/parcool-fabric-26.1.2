package com.alrex.parcool.common.stamina.handlers;

import com.alrex.parcool.api.Attributes;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.common.attachment.common.ReadonlyStamina;
import com.alrex.parcool.common.stamina.IParCoolStaminaHandler;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import com.alrex.parcool.platform.dist.Dist;
import com.alrex.parcool.platform.dist.OnlyIn;

public class ParCoolStaminaHandler
implements IParCoolStaminaHandler {
    private int recoveryCoolDown = 0;

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public ReadonlyStamina initializeStamina(LocalPlayer player, ReadonlyStamina current) {
        return current;
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public ReadonlyStamina consume(LocalPlayer player, ReadonlyStamina current, int value) {
        this.recoveryCoolDown = 30;
        return current.consumed(value);
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public ReadonlyStamina recover(LocalPlayer player, ReadonlyStamina current, int value) {
        return current.recovered(value);
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public ReadonlyStamina onTick(LocalPlayer player, ReadonlyStamina current) {
        if (this.recoveryCoolDown > 0) {
            --this.recoveryCoolDown;
        }
        current = current.updateMax(player);
        if (this.recoveryCoolDown <= 0) {
            Parkourability parkourability = Parkourability.get((Player)player);
            if (parkourability == null) {
                return current;
            }
            AttributeInstance attr = player.getAttribute(Attributes.STAMINA_RECOVERY);
            if (attr == null) {
                return current;
            }
            int recoverValue = (int)Math.min((double)parkourability.getActionInfo().getStaminaRecoveryLimit(), attr.getValue());
            current = player.onGround() ? current.recovered(recoverValue) : current.recovered(recoverValue / 5);
        }
        return current;
    }

    @Override
    @OnlyIn(value=Dist.CLIENT)
    public boolean shouldShowHUD(LocalPlayer player) {
        return true;
    }
}

