package com.alrex.parcool.extern.shouldersurfing;

import com.alrex.parcool.common.action.impl.ClingToCliff;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.github.exopandora.shouldersurfing.api.callback.ICameraCouplingCallback;
import com.github.exopandora.shouldersurfing.api.plugin.IShoulderSurfingPlugin;
import com.github.exopandora.shouldersurfing.api.plugin.IShoulderSurfingRegistrar;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

public class ShoulderSurfingDecoupledCamera
implements ICameraCouplingCallback,
IShoulderSurfingPlugin {
    public boolean isForcingCameraCoupling(Minecraft mc) {
        if (mc.player == null) {
            return false;
        }
        Parkourability parkourability = Parkourability.get((Player)mc.player);
        if (parkourability == null) {
            return false;
        }
        return parkourability.isDoingAny(ClingToCliff.class);
    }

    public void register(IShoulderSurfingRegistrar registrar) {
        registrar.registerCameraCouplingCallback((ICameraCouplingCallback)this);
    }
}

