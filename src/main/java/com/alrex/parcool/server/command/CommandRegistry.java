package com.alrex.parcool.server.command;

import com.alrex.parcool.server.command.impl.ControlLimitationCommand;
import com.alrex.parcool.server.command.impl.ZiplineCommand;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class CommandRegistry {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal((String)"parcool").then(ControlLimitationCommand.getBuilder())).then(ZiplineCommand.getBuilder()));
    }
}

