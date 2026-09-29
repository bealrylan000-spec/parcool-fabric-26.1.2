package com.alrex.parcool.server.command.args;

import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.Actions;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandExceptionType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class ActionArgumentType
implements ArgumentType<Class<? extends Action>> {
    public Class<? extends Action> parse(StringReader reader) throws CommandSyntaxException {
        String name = reader.readUnquotedString();
        Class<? extends Action> result = Actions.getByName(name);
        if (result == null) {
            MutableComponent message = Component.translatable((String)"parcool.command.message.invalidActionName", (Object[])new Object[]{name});
            throw new CommandSyntaxException((CommandExceptionType)new SimpleCommandExceptionType((Message)message), (Message)message);
        }
        return result;
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        String remain = builder.getRemaining();
        for (String name : Actions.NAMES.stream().filter(it -> it.startsWith(remain)).toList()) {
            builder.suggest(name);
        }
        return builder.buildFuture();
    }

    public Collection<String> getExamples() {
        return Actions.NAMES;
    }

    public static ActionArgumentType action() {
        return new ActionArgumentType();
    }

    public static Class<? extends Action> getAction(CommandContext<?> context, String name) {
        return (Class)context.getArgument(name, Class.class);
    }
}

