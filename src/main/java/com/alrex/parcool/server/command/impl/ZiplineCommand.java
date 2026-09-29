package com.alrex.parcool.server.command.impl;

import com.alrex.parcool.common.block.zipline.ZiplineHookTileEntity;
import com.alrex.parcool.common.block.zipline.ZiplineInfo;
import com.alrex.parcool.common.zipline.ZiplineType;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.CompoundTagArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ZiplineCommand {
    private static final String ARGS_NAME_HOOK_POS_1 = "hook1";
    private static final String ARGS_NAME_HOOK_POS_2 = "hook2";
    private static final String ARGS_NAME_ZIPLINE_INFO = "zipline_info";

    public static ArgumentBuilder<CommandSourceStack, ?> getBuilder() {
        return ((LiteralArgumentBuilder)Commands.literal((String)"zipline").requires(commandSource -> commandSource.hasPermission(2))).then(Commands.literal((String)"set").then(Commands.argument((String)ARGS_NAME_HOOK_POS_1, (ArgumentType)BlockPosArgument.blockPos()).then(((RequiredArgumentBuilder)Commands.argument((String)ARGS_NAME_HOOK_POS_2, (ArgumentType)BlockPosArgument.blockPos()).executes(c -> ZiplineCommand.setZipline((CommandContext<CommandSourceStack>)c, false))).then(Commands.argument((String)ARGS_NAME_ZIPLINE_INFO, (ArgumentType)CompoundTagArgument.compoundTag()).executes(c -> ZiplineCommand.setZipline((CommandContext<CommandSourceStack>)c, true))))));
    }

    private static int setZipline(CommandContext<CommandSourceStack> context, boolean hasInfo) throws CommandSyntaxException {
        ZiplineInfo info;
        BlockEntity entity;
        double verticalDist;
        BlockPos hook1 = BlockPosArgument.getLoadedBlockPos(context, (String)ARGS_NAME_HOOK_POS_1);
        BlockPos hook2 = BlockPosArgument.getLoadedBlockPos(context, (String)ARGS_NAME_HOOK_POS_2);
        ServerLevel level = ((CommandSourceStack)context.getSource()).getLevel();
        double horizontalDistSqr = Mth.square((int)(hook1.getX() - hook2.getX())) + Mth.square((int)(hook1.getZ() - hook2.getZ()));
        if (horizontalDistSqr > 5184.0) {
            ((CommandSourceStack)context.getSource()).sendFailure((Component)Component.translatable((String)"parcool.command.message.hookTooFar"));
        }
        if ((verticalDist = (double)Math.abs(hook2.getY() - hook1.getY())) * Mth.fastInvSqrt((double)horizontalDistSqr) > 1.0 || verticalDist > 36.72) {
            ((CommandSourceStack)context.getSource()).sendFailure((Component)Component.translatable((String)"parcool.command.message.ziplineTooSteep"));
        }
        if (!((entity = level.getBlockEntity(hook1)) instanceof ZiplineHookTileEntity)) {
            ((CommandSourceStack)context.getSource()).sendFailure((Component)Component.translatable((String)"parcool.command.message.hookNotFound", (Object[])new Object[]{hook1.toShortString()}));
            return 1;
        }
        BlockEntity entity2 = level.getBlockEntity(hook2);
        if (!(entity2 instanceof ZiplineHookTileEntity)) {
            ((CommandSourceStack)context.getSource()).sendFailure((Component)Component.translatable((String)"parcool.command.message.hookNotFound", (Object[])new Object[]{hook2.toShortString()}));
            return 1;
        }
        if (hasInfo) {
            CompoundTag infoTag = CompoundTagArgument.getCompoundTag(context, (String)ARGS_NAME_ZIPLINE_INFO);
            info = ZiplineInfo.load((Tag)infoTag);
        } else {
            info = new ZiplineInfo(ZiplineType.STANDARD, -11763738);
        }
        if (!((ZiplineHookTileEntity)entity).connectTo((ZiplineHookTileEntity)entity2, info)) {
            return 1;
        }
        ((CommandSourceStack)context.getSource()).sendSuccess(() -> Component.translatable((String)"parcool.command.message.success.setZipline", (Object[])new Object[]{hook1.toShortString(), hook2.toShortString()}), true);
        return 0;
    }
}

