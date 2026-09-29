package com.alrex.parcool.server.command.args;

import com.alrex.parcool.api.unstable.Limitation;
import com.alrex.parcool.server.limitation.Limitations;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandExceptionType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class LimitationIDArgumentType
implements ArgumentType<Limitation.ID> {
    private static final Collection<String> EXAMPLES = Arrays.asList("{Your ID}:{Limitation ID}", "{parcool:example_limitation}");

    private String read(StringReader reader) {
        int start = reader.getCursor();
        while (reader.canRead() && (reader.peek() == ':' || StringReader.isAllowedInUnquotedString((char)reader.peek()))) {
            reader.skip();
        }
        return reader.getString().substring(start, reader.getCursor());
    }

    public Limitation.ID parse(StringReader reader) throws CommandSyntaxException {
        String value = this.read(reader);
        String[] split = value.split(":");
        if (split.length != 2) {
            MutableComponent message = Component.literal((String)"Too many or less separators(':')");
            throw new CommandSyntaxException((CommandExceptionType)new SimpleCommandExceptionType((Message)message), (Message)message);
        }
        return new Limitation.ID(split[0], split[1]);
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        String remain = builder.getRemaining();
        for (com.alrex.parcool.server.limitation.Limitation.ID id : Limitations.getRegisteredIDs()) {
            String suggestion;
            if (id.getGroup().equals("parcool") || !(suggestion = id.getGroup() + ":" + id.getName()).startsWith(remain)) continue;
            builder.suggest(suggestion);
        }
        return builder.buildFuture();
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    public static LimitationIDArgumentType limitation() {
        return new LimitationIDArgumentType();
    }

    public static Limitation.ID getLimitationID(CommandContext<?> context, String name) {
        return (Limitation.ID)context.getArgument(name, Limitation.ID.class);
    }
}

