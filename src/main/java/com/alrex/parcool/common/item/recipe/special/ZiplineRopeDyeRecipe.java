package com.alrex.parcool.common.item.recipe.special;

import com.alrex.parcool.common.item.Items;
import com.alrex.parcool.common.item.recipe.Recipes;
import com.alrex.parcool.common.item.zipline.ZiplineRopeItem;
import java.util.LinkedList;
import javax.annotation.Nonnull;
import net.minecraft.core.HolderLookup;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class ZiplineRopeDyeRecipe
extends CustomRecipe {
    public ZiplineRopeDyeRecipe(CraftingBookCategory p_252125_) {
        super(p_252125_);
    }

    public boolean matches(@Nonnull CraftingInput input, @Nonnull Level level) {
        boolean ziplineRopeFound = false;
        boolean dyeItemFound = false;
        for (int i = 0; i < input.size(); ++i) {
            ItemStack stack = input.getItem(i);
            if (stack.getItem().equals(Items.ZIPLINE_ROPE.get())) {
                if (ziplineRopeFound) {
                    return false;
                }
                ziplineRopeFound = true;
                continue;
            }
            if (stack.getItem() instanceof DyeItem) {
                dyeItemFound = true;
                continue;
            }
            if (stack.isEmpty()) continue;
            return false;
        }
        return ziplineRopeFound && dyeItemFound;
    }

    @Nonnull
    public ItemStack assemble(@Nonnull CraftingInput input, @Nonnull HolderLookup.Provider provider) {
        ItemStack ziplineRope = null;
        LinkedList<DyeItem> dyeItems = new LinkedList<DyeItem>();
        for (int i = 0; i < input.size(); ++i) {
            ItemStack stack = input.getItem(i);
            Item item = stack.getItem();
            if (item instanceof ZiplineRopeItem) {
                ziplineRope = stack;
                continue;
            }
            if (item instanceof DyeItem) {
                dyeItems.add((DyeItem)item);
                continue;
            }
            if (stack.isEmpty()) continue;
            return ItemStack.EMPTY;
        }
        if (ziplineRope == null || dyeItems.isEmpty()) {
            return ItemStack.EMPTY;
        }
        ItemStack resultZiplineRope = new ItemStack(() -> Items.ZIPLINE_ROPE.get());
        resultZiplineRope.applyComponents(resultZiplineRope.getComponents());
        int r = 0;
        int g = 0;
        int b = 0;
        int dyeSize = dyeItems.size();
        for (DyeItem dyeItem : dyeItems) {
            int color = dyeItem.getDyeColor().getTextureDiffuseColor();
            r += FastColor.ARGB32.red((int)color);
            g += FastColor.ARGB32.green((int)color);
            b += FastColor.ARGB32.blue((int)color);
        }
        if (ZiplineRopeItem.hasCustomColor(resultZiplineRope)) {
            ++dyeSize;
            int color = ZiplineRopeItem.getColor(resultZiplineRope);
            r += FastColor.ARGB32.red((int)color);
            g += FastColor.ARGB32.green((int)color);
            b += FastColor.ARGB32.blue((int)color);
        }
        r = Mth.clamp((int)(r / dyeSize), (int)0, (int)255);
        g = Mth.clamp((int)(g / dyeSize), (int)0, (int)255);
        b = Mth.clamp((int)(b / dyeSize), (int)0, (int)255);
        ZiplineRopeItem.setColor(resultZiplineRope, FastColor.ARGB32.color((int)r, (int)g, (int)b));
        return resultZiplineRope;
    }

    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Nonnull
    public RecipeSerializer<?> getSerializer() {
        return Recipes.ZIPLINE_ROPE_DYE.get();
    }
}

