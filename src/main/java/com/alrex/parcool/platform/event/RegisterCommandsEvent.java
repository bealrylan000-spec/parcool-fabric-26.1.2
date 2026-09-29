package com.alrex.parcool.platform.event;

import com.alrex.parcool.platform.eventbus.Event;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public final class RegisterCommandsEvent extends Event {
    private final CommandDispatcher<CommandSourceStack> dispatcher;
    private final CommandBuildContext buildContext;
    private final Commands.CommandSelection selection;

    public RegisterCommandsEvent(CommandDispatcher<CommandSourceStack> dispatcher,
                                  CommandBuildContext buildContext,
                                  Commands.CommandSelection selection) {
        this.dispatcher = dispatcher;
        this.buildContext = buildContext;
        this.selection = selection;
    }

    public CommandDispatcher<CommandSourceStack> getDispatcher() { return dispatcher; }
    public CommandBuildContext getBuildContext() { return buildContext; }
    public Commands.CommandSelection getCommandSelection() { return selection; }
}
