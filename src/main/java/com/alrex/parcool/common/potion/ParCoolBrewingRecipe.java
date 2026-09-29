package com.alrex.parcool.common.potion;

import com.alrex.parcool.common.potion.Potions;
import net.minecraft.world.item.Items;
import com.alrex.parcool.platform.eventbus.SubscribeEvent;
import com.alrex.parcool.platform.event.brewing.RegisterBrewingRecipesEvent;

public class ParCoolBrewingRecipe {
    @SubscribeEvent
    public static void onRegister(RegisterBrewingRecipesEvent event) {
        event.getBuilder().addMix(net.minecraft.world.item.alchemy.Potions.AWKWARD, Items.POISONOUS_POTATO, Potions.POOR_ENERGY_DRINK);
        event.getBuilder().addMix(net.minecraft.world.item.alchemy.Potions.AWKWARD, Items.CHICKEN, Potions.POOR_ENERGY_DRINK);
        event.getBuilder().addMix(net.minecraft.world.item.alchemy.Potions.AWKWARD, Items.QUARTZ, Potions.ENERGY_DRINK);
        event.getBuilder().addMix(Potions.POOR_ENERGY_DRINK, Items.QUARTZ, Potions.ENERGY_DRINK);
    }
}

