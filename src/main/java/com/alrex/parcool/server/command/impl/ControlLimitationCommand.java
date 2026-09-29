package com.alrex.parcool.server.command.impl;

import com.alrex.parcool.api.unstable.Limitation;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.Actions;
import com.alrex.parcool.common.stamina.StaminaType;
import com.alrex.parcool.config.ParCoolConfig;
import com.alrex.parcool.server.command.args.ActionArgumentType;
import com.alrex.parcool.server.command.args.LimitationIDArgumentType;
import com.alrex.parcool.server.command.args.LimitationItemArgumentType;
import com.alrex.parcool.server.command.args.StaminaTypeArgumentType;
import com.alrex.parcool.server.limitation.Limitations;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class ControlLimitationCommand {
    private static final String ARGS_NAME_PLAYERS = "targets";
    private static final String ARGS_NAME_PLAYER = "target";
    private static final String ARGS_NAME_ACTION = "action";
    private static final String ARGS_NAME_STAMINA_CONSUMPTION = "stamina_consumption";
    private static final String ARGS_NAME_POSSIBILITY = "possibility";
    private static final String ARGS_NAME_VALUE = "value";
    private static final String ARGS_NAME_CONFIG_ITEM = "limitation_name";
    private static final String ARGS_NAME_LIMITATION_ID = "limitation_id";
    private static final String ARGS_NAME_STAMINA_TYPE = "stamina_type";

    private static ArgumentBuilder<CommandSourceStack, ?> limitationGetCoreCommands(ArgumentBuilder<CommandSourceStack, ?> builder, boolean hasID, boolean hasPlayer) {
        return builder.then(Commands.literal((String)"boolean").then(Commands.argument((String)ARGS_NAME_CONFIG_ITEM, (ArgumentType)LimitationItemArgumentType.booleans()).executes(context -> ControlLimitationCommand.getBoolLimitation((CommandContext<CommandSourceStack>)context, hasID, hasPlayer)))).then(Commands.literal((String)"integer").then(Commands.argument((String)ARGS_NAME_CONFIG_ITEM, (ArgumentType)LimitationItemArgumentType.integers()).executes(context -> ControlLimitationCommand.getIntLimitation((CommandContext<CommandSourceStack>)context, hasID, hasPlayer)))).then(Commands.literal((String)"reals").then(Commands.argument((String)ARGS_NAME_CONFIG_ITEM, (ArgumentType)LimitationItemArgumentType.doubles()).executes(context -> ControlLimitationCommand.getDoubleLimitation((CommandContext<CommandSourceStack>)context, hasID, hasPlayer)))).then(Commands.literal((String)ARGS_NAME_POSSIBILITY).then(Commands.argument((String)ARGS_NAME_ACTION, (ArgumentType)ActionArgumentType.action()).executes(context -> ControlLimitationCommand.getActionPossibility((CommandContext<CommandSourceStack>)context, hasID, hasPlayer)))).then(Commands.literal((String)"least_stamina_consumption").then(Commands.argument((String)ARGS_NAME_ACTION, (ArgumentType)ActionArgumentType.action()).executes(context -> ControlLimitationCommand.getLeastStaminaConsumption((CommandContext<CommandSourceStack>)context, hasID, hasPlayer)))).then(Commands.literal((String)ARGS_NAME_STAMINA_TYPE).executes(context -> ControlLimitationCommand.getStaminaType((CommandContext<CommandSourceStack>)context, hasID, hasPlayer)));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> limitationSetCoreCommands(ArgumentBuilder<CommandSourceStack, ?> builder, boolean hasID, boolean hasPlayer) {
        return builder.then(Commands.literal((String)"to_default").executes(context -> ControlLimitationCommand.setLimitationDefault((CommandContext<CommandSourceStack>)context, hasID, hasPlayer))).then(Commands.literal((String)"boolean").then(Commands.argument((String)ARGS_NAME_CONFIG_ITEM, (ArgumentType)LimitationItemArgumentType.booleans()).then(Commands.argument((String)ARGS_NAME_VALUE, (ArgumentType)BoolArgumentType.bool()).executes(context -> ControlLimitationCommand.setBoolLimitation((CommandContext<CommandSourceStack>)context, hasID, hasPlayer))))).then(Commands.literal((String)"integer").then(Commands.argument((String)ARGS_NAME_CONFIG_ITEM, (ArgumentType)LimitationItemArgumentType.integers()).then(Commands.argument((String)ARGS_NAME_VALUE, (ArgumentType)IntegerArgumentType.integer()).executes(context -> ControlLimitationCommand.setIntLimitation((CommandContext<CommandSourceStack>)context, hasID, hasPlayer))))).then(Commands.literal((String)"reals").then(Commands.argument((String)ARGS_NAME_CONFIG_ITEM, (ArgumentType)LimitationItemArgumentType.doubles()).then(Commands.argument((String)ARGS_NAME_VALUE, (ArgumentType)DoubleArgumentType.doubleArg()).executes(context -> ControlLimitationCommand.setDoubleLimitation((CommandContext<CommandSourceStack>)context, hasID, hasPlayer))))).then(Commands.literal((String)ARGS_NAME_POSSIBILITY).then(Commands.argument((String)ARGS_NAME_ACTION, (ArgumentType)ActionArgumentType.action()).then(Commands.argument((String)ARGS_NAME_POSSIBILITY, (ArgumentType)BoolArgumentType.bool()).executes(context -> ControlLimitationCommand.changePossibilityOfAction((CommandContext<CommandSourceStack>)context, hasID, hasPlayer))))).then(Commands.literal((String)"least_stamina_consumption").then(Commands.argument((String)ARGS_NAME_ACTION, (ArgumentType)ActionArgumentType.action()).then(Commands.argument((String)ARGS_NAME_STAMINA_CONSUMPTION, (ArgumentType)IntegerArgumentType.integer((int)0, (int)Integer.MAX_VALUE)).executes(context -> ControlLimitationCommand.changeStaminaConsumption((CommandContext<CommandSourceStack>)context, hasID, hasPlayer))))).then(Commands.literal((String)ARGS_NAME_STAMINA_TYPE).then(Commands.argument((String)ARGS_NAME_STAMINA_TYPE, (ArgumentType)StaminaTypeArgumentType.type()).executes(context -> ControlLimitationCommand.setStaminaType((CommandContext<CommandSourceStack>)context, hasID, hasPlayer))));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> getLimitationByNameCommands(boolean multiPlayer, Function<RequiredArgumentBuilder<CommandSourceStack, ?>, ArgumentBuilder<CommandSourceStack, ?>> afterCommand) {
        if (multiPlayer) {
            return Commands.argument((String)ARGS_NAME_LIMITATION_ID, (ArgumentType)LimitationIDArgumentType.limitation()).then(Commands.literal((String)"of").then(afterCommand.apply(Commands.argument((String)ARGS_NAME_PLAYERS, (ArgumentType)EntityArgument.players()))));
        }
        return Commands.argument((String)ARGS_NAME_LIMITATION_ID, (ArgumentType)LimitationIDArgumentType.limitation()).then(Commands.literal((String)"of").then(afterCommand.apply(Commands.argument((String)ARGS_NAME_PLAYER, (ArgumentType)EntityArgument.player()))));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> getIndividualLimitationCommands(boolean multiPlayer, Function<RequiredArgumentBuilder<CommandSourceStack, ?>, ArgumentBuilder<CommandSourceStack, ?>> afterCommand) {
        if (multiPlayer) {
            return Commands.literal((String)"individual").then(Commands.literal((String)"of").then(afterCommand.apply(Commands.argument((String)ARGS_NAME_PLAYERS, (ArgumentType)EntityArgument.players()))));
        }
        return Commands.literal((String)"individual").then(Commands.literal((String)"of").then(afterCommand.apply(Commands.argument((String)ARGS_NAME_PLAYER, (ArgumentType)EntityArgument.player()))));
    }

    private static ArgumentBuilder<CommandSourceStack, ?> getGlobalLimitationCommands(Function<LiteralArgumentBuilder<CommandSourceStack>, ArgumentBuilder<CommandSourceStack, ?>> afterCommand) {
        return afterCommand.apply((LiteralArgumentBuilder<CommandSourceStack>)Commands.literal((String)"global"));
    }

    public static ArgumentBuilder<CommandSourceStack, ?> getBuilder() {
        return ((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal((String)"limitation").then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal((String)"get").then(ControlLimitationCommand.getLimitationByNameCommands(false, it -> {
            it.executes(context -> ControlLimitationCommand.getLimitationInfo((CommandContext<CommandSourceStack>)context, true, true));
            ControlLimitationCommand.limitationGetCoreCommands(it, true, true);
            return it;
        }))).then(ControlLimitationCommand.getIndividualLimitationCommands(false, it -> {
            it.executes(context -> ControlLimitationCommand.getLimitationInfo((CommandContext<CommandSourceStack>)context, false, true));
            ControlLimitationCommand.limitationGetCoreCommands(it, false, true);
            return it;
        }))).then(ControlLimitationCommand.getGlobalLimitationCommands(it -> {
            it.executes(context -> ControlLimitationCommand.getLimitationInfo((CommandContext<CommandSourceStack>)context, false, false));
            ControlLimitationCommand.limitationGetCoreCommands(it, false, false);
            return it;
        })))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal((String)"set").requires(commandSource -> commandSource.hasPermission(2))).then(ControlLimitationCommand.getLimitationByNameCommands(true, it -> {
            ControlLimitationCommand.limitationSetCoreCommands(it, true, true);
            return it;
        }))).then(ControlLimitationCommand.getIndividualLimitationCommands(true, it -> {
            ControlLimitationCommand.limitationSetCoreCommands(it, false, true);
            return it;
        })))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal((String)"enable").requires(commandSource -> commandSource.hasPermission(2))).then(ControlLimitationCommand.getLimitationByNameCommands(true, it -> {
            it.executes(context -> ControlLimitationCommand.enableLimitation((CommandContext<CommandSourceStack>)context, true, true));
            return it;
        }))).then(ControlLimitationCommand.getIndividualLimitationCommands(true, it -> {
            it.executes(context -> ControlLimitationCommand.enableLimitation((CommandContext<CommandSourceStack>)context, false, true));
            return it;
        })))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal((String)"disable").requires(commandSource -> commandSource.hasPermission(2))).then(ControlLimitationCommand.getLimitationByNameCommands(true, it -> {
            it.executes(context -> ControlLimitationCommand.disableLimitation((CommandContext<CommandSourceStack>)context, true, true));
            return it;
        }))).then(ControlLimitationCommand.getIndividualLimitationCommands(true, it -> {
            it.executes(context -> ControlLimitationCommand.disableLimitation((CommandContext<CommandSourceStack>)context, false, true));
            return it;
        })))).then(((LiteralArgumentBuilder)Commands.literal((String)"delete").requires(commandSource -> commandSource.hasPermission(2))).then(Commands.argument((String)ARGS_NAME_LIMITATION_ID, (ArgumentType)LimitationIDArgumentType.limitation()).executes(ControlLimitationCommand::deleteLimitation)));
    }

    private static List<Limitation> getLimitationInstance(Collection<ServerPlayer> players, @Nullable Limitation.ID id, @Nullable MinecraftServer server) {
        if (players.isEmpty()) {
            if (server != null) {
                return Collections.singletonList(Limitation.getGlobal(server));
            }
            if (id == null) {
                // empty if block
            }
        } else {
            if (id != null) {
                LinkedList<Limitation> list = new LinkedList<Limitation>();
                for (ServerPlayer player : players) {
                    list.add(Limitation.get(player, id));
                }
                return list;
            }
            LinkedList<Limitation> list = new LinkedList<Limitation>();
            for (ServerPlayer player : players) {
                list.add(Limitation.getIndividual(player));
            }
            return list;
        }
        return Collections.emptyList();
    }

    private static int getBoolLimitation(CommandContext<CommandSourceStack> context, boolean hasID, boolean hasPlayer) throws CommandSyntaxException {
        List<Limitation> limitations = ControlLimitationCommand.getLimitationInstance(hasPlayer ? Collections.singletonList(EntityArgument.getPlayer(context, (String)ARGS_NAME_PLAYER)) : Collections.emptyList(), hasID ? LimitationIDArgumentType.getLimitationID(context, ARGS_NAME_LIMITATION_ID) : null, ((CommandSourceStack)context.getSource()).getServer());
        ParCoolConfig.Server.Booleans item = LimitationItemArgumentType.getBool(context, ARGS_NAME_CONFIG_ITEM);
        ((CommandSourceStack)context.getSource()).sendSuccess(() -> Component.literal((String)Boolean.toString(((Limitation)limitations.get(0)).get(item))), false);
        return 0;
    }

    private static int getIntLimitation(CommandContext<CommandSourceStack> context, boolean hasID, boolean hasPlayer) throws CommandSyntaxException {
        List<Limitation> limitations = ControlLimitationCommand.getLimitationInstance(hasPlayer ? Collections.singletonList(EntityArgument.getPlayer(context, (String)ARGS_NAME_PLAYER)) : Collections.emptyList(), hasID ? LimitationIDArgumentType.getLimitationID(context, ARGS_NAME_LIMITATION_ID) : null, ((CommandSourceStack)context.getSource()).getServer());
        ParCoolConfig.Server.Integers item = LimitationItemArgumentType.getInt(context, ARGS_NAME_CONFIG_ITEM);
        ((CommandSourceStack)context.getSource()).sendSuccess(() -> Component.literal((String)Integer.toString(((Limitation)limitations.get(0)).get(item))), false);
        return 0;
    }

    private static int getDoubleLimitation(CommandContext<CommandSourceStack> context, boolean hasID, boolean hasPlayer) throws CommandSyntaxException {
        List<Limitation> limitations = ControlLimitationCommand.getLimitationInstance(hasPlayer ? Collections.singletonList(EntityArgument.getPlayer(context, (String)ARGS_NAME_PLAYER)) : Collections.emptyList(), hasID ? LimitationIDArgumentType.getLimitationID(context, ARGS_NAME_LIMITATION_ID) : null, ((CommandSourceStack)context.getSource()).getServer());
        ParCoolConfig.Server.Doubles item = LimitationItemArgumentType.getDouble(context, ARGS_NAME_CONFIG_ITEM);
        ((CommandSourceStack)context.getSource()).sendSuccess(() -> Component.literal((String)Double.toString(((Limitation)limitations.get(0)).get(item))), false);
        return 0;
    }

    private static int getActionPossibility(CommandContext<CommandSourceStack> context, boolean hasID, boolean hasPlayer) throws CommandSyntaxException {
        List<Limitation> limitations = ControlLimitationCommand.getLimitationInstance(hasPlayer ? Collections.singletonList(EntityArgument.getPlayer(context, (String)ARGS_NAME_PLAYER)) : Collections.emptyList(), hasID ? LimitationIDArgumentType.getLimitationID(context, ARGS_NAME_LIMITATION_ID) : null, ((CommandSourceStack)context.getSource()).getServer());
        Class<? extends Action> action = ActionArgumentType.getAction(context, ARGS_NAME_ACTION);
        ((CommandSourceStack)context.getSource()).sendSuccess(() -> Component.literal((String)Boolean.toString(((Limitation)limitations.get(0)).isPermitted(action))), false);
        return 0;
    }

    private static int getLimitationInfo(CommandContext<CommandSourceStack> context, boolean hasID, boolean hasPlayer) throws CommandSyntaxException {
        List<Limitation> limitations = ControlLimitationCommand.getLimitationInstance(hasPlayer ? Collections.singletonList(EntityArgument.getPlayer(context, (String)ARGS_NAME_PLAYER)) : Collections.emptyList(), hasID ? LimitationIDArgumentType.getLimitationID(context, ARGS_NAME_LIMITATION_ID) : null, ((CommandSourceStack)context.getSource()).getServer());
        Limitation limitation = limitations.get(0);
        StringBuilder builder = new StringBuilder();
        builder.append("- Limitation Info -\n");
        builder.append("Enabled : ").append(limitation.isEnabled()).append('\n');
        for (Class<? extends Action> action : Actions.LIST) {
            builder.append("  ").append(action.getSimpleName()).append(" : ").append('\n').append("    ").append("permitted : ").append(limitation.isPermitted(action)).append('\n').append("    ").append("stamina consumption : ").append(limitation.getLeastStaminaConsumption(action)).append('\n');
        }
        for (ParCoolConfig.Server.Booleans booleans : ParCoolConfig.Server.Booleans.values()) {
            builder.append("  ").append(booleans.getPath()).append(" : ").append(limitation.get(booleans)).append('\n');
        }
        for (Enum enum_ : ParCoolConfig.Server.Integers.values()) {
            builder.append("  ").append(((ParCoolConfig.Server.Integers)enum_).getPath()).append(" : ").append(limitation.get((ParCoolConfig.Server.Integers)enum_)).append('\n');
        }
        for (Enum enum_ : ParCoolConfig.Server.Doubles.values()) {
            builder.append("  ").append(((ParCoolConfig.Server.Doubles)enum_).getPath()).append(" : ").append(limitation.get((ParCoolConfig.Server.Doubles)enum_)).append('\n');
        }
        builder.append("----------");
        ((CommandSourceStack)context.getSource()).sendSuccess(() -> Component.literal((String)builder.toString()), false);
        return 0;
    }

    private static int getLeastStaminaConsumption(CommandContext<CommandSourceStack> context, boolean hasID, boolean hasPlayer) throws CommandSyntaxException {
        List<Limitation> limitations = ControlLimitationCommand.getLimitationInstance(hasPlayer ? Collections.singletonList(EntityArgument.getPlayer(context, (String)ARGS_NAME_PLAYER)) : Collections.emptyList(), hasID ? LimitationIDArgumentType.getLimitationID(context, ARGS_NAME_LIMITATION_ID) : null, ((CommandSourceStack)context.getSource()).getServer());
        Class<? extends Action> action = ActionArgumentType.getAction(context, ARGS_NAME_ACTION);
        ((CommandSourceStack)context.getSource()).sendSuccess(() -> Component.literal((String)Integer.toString(((Limitation)limitations.get(0)).getLeastStaminaConsumption(action))), false);
        return 0;
    }

    private static int getStaminaType(CommandContext<CommandSourceStack> context, boolean hasID, boolean hasPlayer) throws CommandSyntaxException {
        List<Limitation> limitations = ControlLimitationCommand.getLimitationInstance(hasPlayer ? Collections.singletonList(EntityArgument.getPlayer(context, (String)ARGS_NAME_PLAYER)) : Collections.emptyList(), hasID ? LimitationIDArgumentType.getLimitationID(context, ARGS_NAME_LIMITATION_ID) : null, ((CommandSourceStack)context.getSource()).getServer());
        Class<? extends Action> action = ActionArgumentType.getAction(context, ARGS_NAME_ACTION);
        ((CommandSourceStack)context.getSource()).sendSuccess(() -> Component.literal((String)((Limitation)limitations.get(0)).getStaminaType().toString()), false);
        return 0;
    }

    private static int setLimitationDefault(CommandContext<CommandSourceStack> context, boolean hasID, boolean hasPlayer) throws CommandSyntaxException {
        List<Limitation> limitations = ControlLimitationCommand.getLimitationInstance(hasPlayer ? EntityArgument.getPlayers(context, (String)ARGS_NAME_PLAYERS) : Collections.emptyList(), hasID ? LimitationIDArgumentType.getLimitationID(context, ARGS_NAME_LIMITATION_ID) : null, ((CommandSourceStack)context.getSource()).getServer());
        int num = 0;
        for (Limitation limitation : limitations) {
            limitation.setDefault().apply();
            limitation.save();
            ++num;
        }
        int finalNum = num;
        ((CommandSourceStack)context.getSource()).sendSuccess(() -> Component.translatable((String)"parcool.command.message.success.setLimitationToDefault", (Object[])new Object[]{finalNum}), true);
        return 0;
    }

    private static int setBoolLimitation(CommandContext<CommandSourceStack> context, boolean hasID, boolean hasPlayer) throws CommandSyntaxException {
        List<Limitation> limitations = ControlLimitationCommand.getLimitationInstance(hasPlayer ? EntityArgument.getPlayers(context, (String)ARGS_NAME_PLAYERS) : Collections.emptyList(), hasID ? LimitationIDArgumentType.getLimitationID(context, ARGS_NAME_LIMITATION_ID) : null, ((CommandSourceStack)context.getSource()).getServer());
        ParCoolConfig.Server.Booleans item = LimitationItemArgumentType.getBool(context, ARGS_NAME_CONFIG_ITEM);
        boolean value = BoolArgumentType.getBool(context, (String)ARGS_NAME_VALUE);
        int num = 0;
        for (Limitation limitation : limitations) {
            limitation.set(item, value).apply();
            limitation.save();
            ++num;
        }
        int finalNum = num;
        ((CommandSourceStack)context.getSource()).sendSuccess(() -> Component.translatable((String)"parcool.command.message.success.set", (Object[])new Object[]{finalNum, item.getPath(), Boolean.toString(value)}), true);
        return 0;
    }

    private static int setIntLimitation(CommandContext<CommandSourceStack> context, boolean hasID, boolean hasPlayer) throws CommandSyntaxException {
        List<Limitation> limitations = ControlLimitationCommand.getLimitationInstance(hasPlayer ? EntityArgument.getPlayers(context, (String)ARGS_NAME_PLAYERS) : Collections.emptyList(), hasID ? LimitationIDArgumentType.getLimitationID(context, ARGS_NAME_LIMITATION_ID) : null, ((CommandSourceStack)context.getSource()).getServer());
        ParCoolConfig.Server.Integers item = LimitationItemArgumentType.getInt(context, ARGS_NAME_CONFIG_ITEM);
        int value = IntegerArgumentType.getInteger(context, (String)ARGS_NAME_VALUE);
        if (value < item.Min) {
            value = item.Min;
        }
        if (value > item.Max) {
            value = item.Max;
        }
        int num = 0;
        for (Limitation limitation : limitations) {
            limitation.set(item, value).apply();
            limitation.save();
            ++num;
        }
        int finalValue = value;
        int finalNum = num;
        ((CommandSourceStack)context.getSource()).sendSuccess(() -> Component.translatable((String)"parcool.command.message.success.set", (Object[])new Object[]{finalNum, item.getPath(), Integer.toString(finalValue)}), true);
        return 0;
    }

    private static int setDoubleLimitation(CommandContext<CommandSourceStack> context, boolean hasID, boolean hasPlayer) throws CommandSyntaxException {
        List<Limitation> limitations = ControlLimitationCommand.getLimitationInstance(hasPlayer ? EntityArgument.getPlayers(context, (String)ARGS_NAME_PLAYERS) : Collections.emptyList(), hasID ? LimitationIDArgumentType.getLimitationID(context, ARGS_NAME_LIMITATION_ID) : null, ((CommandSourceStack)context.getSource()).getServer());
        ParCoolConfig.Server.Doubles item = LimitationItemArgumentType.getDouble(context, ARGS_NAME_CONFIG_ITEM);
        double value = DoubleArgumentType.getDouble(context, (String)ARGS_NAME_VALUE);
        if (value < item.Min) {
            value = item.Min;
        }
        if (value > item.Max) {
            value = item.Max;
        }
        int num = 0;
        for (Limitation limitation : limitations) {
            limitation.set(item, value).apply();
            limitation.save();
            ++num;
        }
        double finalValue = value;
        int finalNum = num;
        ((CommandSourceStack)context.getSource()).sendSuccess(() -> Component.translatable((String)"parcool.command.message.success.set", (Object[])new Object[]{finalNum, item.getPath(), Double.toString(finalValue)}), true);
        return 0;
    }

    private static int enableLimitation(CommandContext<CommandSourceStack> context, boolean hasID, boolean hasPlayer) throws CommandSyntaxException {
        List<Limitation> limitations = ControlLimitationCommand.getLimitationInstance(hasPlayer ? EntityArgument.getPlayers(context, (String)ARGS_NAME_PLAYERS) : Collections.emptyList(), hasID ? LimitationIDArgumentType.getLimitationID(context, ARGS_NAME_LIMITATION_ID) : null, ((CommandSourceStack)context.getSource()).getServer());
        int num = 0;
        for (Limitation limitation : limitations) {
            limitation.enable().apply();
            limitation.save();
            ++num;
        }
        int finalNum = num;
        ((CommandSourceStack)context.getSource()).sendSuccess(() -> Component.translatable((String)"parcool.command.message.success.enableLimitation", (Object[])new Object[]{finalNum}), true);
        return 0;
    }

    private static int disableLimitation(CommandContext<CommandSourceStack> context, boolean hasID, boolean hasPlayer) throws CommandSyntaxException {
        List<Limitation> limitations = ControlLimitationCommand.getLimitationInstance(hasPlayer ? EntityArgument.getPlayers(context, (String)ARGS_NAME_PLAYERS) : Collections.emptyList(), hasID ? LimitationIDArgumentType.getLimitationID(context, ARGS_NAME_LIMITATION_ID) : null, ((CommandSourceStack)context.getSource()).getServer());
        int num = 0;
        for (Limitation limitation : limitations) {
            limitation.disable().apply();
            limitation.save();
            ++num;
        }
        int finalNum = num;
        ((CommandSourceStack)context.getSource()).sendSuccess(() -> Component.translatable((String)"parcool.command.message.success.disableLimitation", (Object[])new Object[]{finalNum}), true);
        return 0;
    }

    private static int deleteLimitation(CommandContext<CommandSourceStack> context) {
        Limitation.ID limitation = LimitationIDArgumentType.getLimitationID(context, ARGS_NAME_LIMITATION_ID);
        if (Limitation.delete(limitation)) {
            for (ServerPlayer player : ((CommandSourceStack)context.getSource()).getServer().getPlayerList().getPlayers()) {
                Limitations.updateOnlyLimitation(player);
            }
            ((CommandSourceStack)context.getSource()).sendSuccess(() -> Component.translatable((String)"parcool.command.message.success.deleteLimitation", (Object[])new Object[]{limitation.toString()}), true);
        } else {
            ((CommandSourceStack)context.getSource()).sendFailure((Component)Component.literal((String)"Error:deleting folder failed"));
        }
        return 0;
    }

    private static int changeStaminaConsumption(CommandContext<CommandSourceStack> context, boolean hasID, boolean hasPlayer) throws CommandSyntaxException {
        List<Limitation> limitations = ControlLimitationCommand.getLimitationInstance(hasPlayer ? EntityArgument.getPlayers(context, (String)ARGS_NAME_PLAYERS) : Collections.emptyList(), hasID ? LimitationIDArgumentType.getLimitationID(context, ARGS_NAME_LIMITATION_ID) : null, ((CommandSourceStack)context.getSource()).getServer());
        Class<? extends Action> action = ActionArgumentType.getAction(context, ARGS_NAME_ACTION);
        int newValue = IntegerArgumentType.getInteger(context, (String)ARGS_NAME_STAMINA_CONSUMPTION);
        int num = 0;
        for (Limitation limitation : limitations) {
            limitation.setLeastStaminaConsumption(action, newValue).apply();
            limitation.save();
            ++num;
        }
        int finalNum = num;
        ((CommandSourceStack)context.getSource()).sendSuccess(() -> Component.translatable((String)"parcool.command.message.success.setStaminaConsumption", (Object[])new Object[]{finalNum, action.getSimpleName(), newValue}), true);
        return 0;
    }

    private static int setStaminaType(CommandContext<CommandSourceStack> context, boolean hasID, boolean hasPlayer) throws CommandSyntaxException {
        List<Limitation> limitations = ControlLimitationCommand.getLimitationInstance(hasPlayer ? EntityArgument.getPlayers(context, (String)ARGS_NAME_PLAYERS) : Collections.emptyList(), hasID ? LimitationIDArgumentType.getLimitationID(context, ARGS_NAME_LIMITATION_ID) : null, ((CommandSourceStack)context.getSource()).getServer());
        StaminaType type = StaminaTypeArgumentType.getStamina(context, ARGS_NAME_STAMINA_TYPE);
        int num = 0;
        for (Limitation limitation : limitations) {
            limitation.setStaminaType(type);
            ++num;
        }
        int finalNum = num;
        ((CommandSourceStack)context.getSource()).sendSuccess(() -> Component.translatable((String)"parcool.command.message.success.setStaminaType", (Object[])new Object[]{finalNum, type.name()}), true);
        return 0;
    }

    private static int changePossibilityOfAction(CommandContext<CommandSourceStack> context, boolean hasID, boolean hasPlayer) throws CommandSyntaxException {
        List<Limitation> limitations = ControlLimitationCommand.getLimitationInstance(hasPlayer ? EntityArgument.getPlayers(context, (String)ARGS_NAME_PLAYERS) : Collections.emptyList(), hasID ? LimitationIDArgumentType.getLimitationID(context, ARGS_NAME_LIMITATION_ID) : null, ((CommandSourceStack)context.getSource()).getServer());
        Class<? extends Action> action = ActionArgumentType.getAction(context, ARGS_NAME_ACTION);
        boolean newValue = BoolArgumentType.getBool(context, (String)ARGS_NAME_POSSIBILITY);
        int num = 0;
        for (Limitation limitation : limitations) {
            limitation.permit(action, newValue).apply();
            limitation.save();
            ++num;
        }
        int finalNum = num;
        ((CommandSourceStack)context.getSource()).sendSuccess(() -> Component.translatable((String)"parcool.command.message.success.setPermissionOfAction", (Object[])new Object[]{finalNum, action.getSimpleName(), newValue}), true);
        return 0;
    }
}

