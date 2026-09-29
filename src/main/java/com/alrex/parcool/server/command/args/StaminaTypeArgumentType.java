package com.alrex.parcool.server.command.args;

import com.alrex.parcool.common.stamina.StaminaType;
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

public class StaminaTypeArgumentType
implements ArgumentType<StaminaType> {
    private static final Collection<String> EXAMPLES = Arrays.stream(StaminaType.values()).map(Enum::name).toList();

    public StaminaType parse(StringReader reader) throws CommandSyntaxException {
        String name = reader.readUnquotedString();
        try {
            return StaminaType.valueOf(name);
        }
        catch (IllegalArgumentException e) {
            MutableComponent message = Component.translatable((String)"parcool.command.message.invalidStaminaType", (Object[])new Object[]{name});
            throw new CommandSyntaxException((CommandExceptionType)new SimpleCommandExceptionType((Message)message), (Message)message);
        }
    }

    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        String remain = builder.getRemaining();
        for (StaminaType type : Arrays.stream(StaminaType.values()).filter(it -> it.name().startsWith(remain)).toList()) {
            builder.suggest(type.name());
        }
        return builder.buildFuture();
    }

    public Collection<String> getExamples() {
        return EXAMPLES;
    }

    public static StaminaTypeArgumentType type() {
        return new StaminaTypeArgumentType();
    }

    public static StaminaType getStamina(CommandContext<?> context, String name) {
        return (StaminaType)((Object)context.getArgument(name, StaminaType.class));
    }
}

