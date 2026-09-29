package com.alrex.parcool.server.command.args;

import com.alrex.parcool.config.ParCoolConfig;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandExceptionType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class LimitationItemArgumentType
implements ArgumentType<Object> {
    private final List<String> paths;
    private final Object[] enumConstants;

    public Object parse(StringReader reader) throws CommandSyntaxException {
        String name = reader.readUnquotedString();
        int index = this.paths.indexOf(name);
        if (index == -1) {
            MutableComponent message = Component.translatable((String)"parcool.command.message.invalidConfigName", (Object[])new Object[]{name});
            throw new CommandSyntaxException((CommandExceptionType)new SimpleCommandExceptionType((Message)message), (Message)message);
        }
        return this.enumConstants[index];
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        String remain = builder.getRemaining();
        for (String name : this.paths.stream().filter(it -> it.startsWith(remain)).toList()) {
            builder.suggest(name);
        }
        return builder.buildFuture();
    }

    public Collection<String> getExamples() {
        return this.paths;
    }

    LimitationItemArgumentType(Class<?> clazz) {
        this.enumConstants = clazz.getEnumConstants();
        this.paths = new ArrayList<String>(this.enumConstants.length);
        for (Object enumConstant : this.enumConstants) {
            ParCoolConfig.Item item = (ParCoolConfig.Item)enumConstant;
            this.paths.add(item.getPath());
        }
    }

    public static Booleans booleans() {
        return new Booleans();
    }

    public static Integers integers() {
        return new Integers();
    }

    public static Doubles doubles() {
        return new Doubles();
    }

    public static ParCoolConfig.Server.Booleans getBool(CommandContext<?> context, String name) {
        return (ParCoolConfig.Server.Booleans)context.getArgument(name, ParCoolConfig.Server.Booleans.class);
    }

    public static ParCoolConfig.Server.Integers getInt(CommandContext<?> context, String name) {
        return (ParCoolConfig.Server.Integers)context.getArgument(name, ParCoolConfig.Server.Integers.class);
    }

    public static ParCoolConfig.Server.Doubles getDouble(CommandContext<?> context, String name) {
        return (ParCoolConfig.Server.Doubles)context.getArgument(name, ParCoolConfig.Server.Doubles.class);
    }

    public static class Booleans
    extends LimitationItemArgumentType {
        Booleans() {
            super(ParCoolConfig.Server.Booleans.class);
        }
    }

    public static class Integers
    extends LimitationItemArgumentType {
        Integers() {
            super(ParCoolConfig.Server.Integers.class);
        }
    }

    public static class Doubles
    extends LimitationItemArgumentType {
        Doubles() {
            super(ParCoolConfig.Server.Doubles.class);
        }
    }
}

