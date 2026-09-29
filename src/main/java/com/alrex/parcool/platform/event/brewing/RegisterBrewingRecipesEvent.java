package com.alrex.parcool.platform.event.brewing;

import com.alrex.parcool.platform.eventbus.Event;
import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistryBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;

/**
 * Stand-in for NeoForge's RegisterBrewingRecipesEvent, backed by Fabric API's
 * FabricBrewingRecipeRegistryBuilder (the standard, well-established way to add brewing recipes
 * on Fabric).
 */
public final class RegisterBrewingRecipesEvent extends Event {
    public static final class Builder {
        public void addMix(Holder<Potion> input, Item catalyst, Holder<Potion> output) {
            FabricBrewingRecipeRegistryBuilder.registerPotionRecipe(input, catalyst, output);
        }
    }

    private final Builder builder = new Builder();
    public Builder getBuilder() { return builder; }
}
