package com.alrex.parcool.common.tags;

import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class BlockTags {
    public static final TagKey<Block> HIDE_ABLE = net.minecraft.tags.BlockTags.create((Identifier)Identifier.fromNamespaceAndPath((String)"parcool", (String)"hide_able"));
    public static final TagKey<Block> POLE_CLIMBABLE = net.minecraft.tags.BlockTags.create((Identifier)Identifier.fromNamespaceAndPath((String)"parcool", (String)"pole_climbable"));
}

