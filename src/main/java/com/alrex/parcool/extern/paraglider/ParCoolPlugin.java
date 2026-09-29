package com.alrex.parcool.extern.paraglider;

import com.alrex.parcool.extern.paraglider.ParCoolPlayerStates;
import net.minecraft.resources.ResourceLocation;
import tictim.paraglider.api.movement.MovementPlugin;
import tictim.paraglider.api.plugin.ParagliderPlugin;

@ParagliderPlugin
public class ParCoolPlugin
implements MovementPlugin {
    public void registerNewStates(MovementPlugin.PlayerStateRegister register) {
        ParCoolPlayerStates.ENTRIES.forEach(it -> register.register(it.stateID(), (double)it.staminaDelta(), new ResourceLocation[0]));
    }

    public void registerStateConnections(MovementPlugin.PlayerStateConnectionRegister register) {
        ParCoolPlayerStates.ENTRIES.forEach(it -> {
            for (ResourceLocation parent : it.parentID()) {
                register.connect(parent, it.stateID(), it.condition(), it.priority());
            }
        });
    }
}

