package com.alrex.parcool.common.item.recipe;

import com.alrex.parcool.common.item.recipe.special.ZiplineRopeDyeRecipe;
import java.util.function.Supplier;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import com.alrex.parcool.platform.eventbus.IEventBus;
import com.alrex.parcool.platform.registries.DeferredRegister;

public class Recipes {
    private static final DeferredRegister<RecipeSerializer<?>> RECIPES = DeferredRegister.create((ResourceKey)Registries.RECIPE_SERIALIZER, (String)"parcool");
    public static final Supplier<RecipeSerializer<ZiplineRopeDyeRecipe>> ZIPLINE_ROPE_DYE = RECIPES.register("zipline_rope_dye", () -> new SimpleCraftingRecipeSerializer(ZiplineRopeDyeRecipe::new));

    public static void registerAll(IEventBus bus) {
        RECIPES.register(bus);
    }
}

