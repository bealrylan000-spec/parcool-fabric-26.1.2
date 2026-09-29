package com.alrex.parcool.server.command.args;

import com.alrex.parcool.server.command.args.ActionArgumentType;
import com.alrex.parcool.server.command.args.LimitationIDArgumentType;
import com.alrex.parcool.server.command.args.LimitationItemArgumentType;
import com.alrex.parcool.server.command.args.StaminaTypeArgumentType;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import com.alrex.parcool.platform.eventbus.IEventBus;
import com.alrex.parcool.platform.registries.DeferredRegister;

public class ParCoolArgumentTypeInfos {
    private static final DeferredRegister<ArgumentTypeInfo<?, ?>> COMMAND_ARGUMENT_TYPES = DeferredRegister.create((ResourceKey)Registries.COMMAND_ARGUMENT_TYPE, (String)"parcool");
    private static final Holder<ArgumentTypeInfo<?, ?>> ACTION_ARGUMENT_TYPE = COMMAND_ARGUMENT_TYPES.register("action", () -> (SingletonArgumentInfo)ArgumentTypeInfos.registerByClass(ActionArgumentType.class, (ArgumentTypeInfo)SingletonArgumentInfo.contextFree(ActionArgumentType::action)));
    private static final Holder<ArgumentTypeInfo<?, ?>> LIMITATION_BOOL_ARGUMENT_TYPE = COMMAND_ARGUMENT_TYPES.register("limitation_bool", () -> (SingletonArgumentInfo)ArgumentTypeInfos.registerByClass(LimitationItemArgumentType.Booleans.class, (ArgumentTypeInfo)SingletonArgumentInfo.contextFree(LimitationItemArgumentType::booleans)));
    private static final Holder<ArgumentTypeInfo<?, ?>> LIMITATION_INT_ARGUMENT_TYPE = COMMAND_ARGUMENT_TYPES.register("limitation_int", () -> (SingletonArgumentInfo)ArgumentTypeInfos.registerByClass(LimitationItemArgumentType.Integers.class, (ArgumentTypeInfo)SingletonArgumentInfo.contextFree(LimitationItemArgumentType::integers)));
    private static final Holder<ArgumentTypeInfo<?, ?>> LIMITATION_REAL_ARGUMENT_TYPE = COMMAND_ARGUMENT_TYPES.register("limitation_reals", () -> (SingletonArgumentInfo)ArgumentTypeInfos.registerByClass(LimitationItemArgumentType.Doubles.class, (ArgumentTypeInfo)SingletonArgumentInfo.contextFree(LimitationItemArgumentType::doubles)));
    private static final Holder<ArgumentTypeInfo<?, ?>> LIMITATION_ID_ARGUMENT_TYPE = COMMAND_ARGUMENT_TYPES.register("limitation_id", () -> (SingletonArgumentInfo)ArgumentTypeInfos.registerByClass(LimitationIDArgumentType.class, (ArgumentTypeInfo)SingletonArgumentInfo.contextFree(LimitationIDArgumentType::new)));
    private static final Holder<ArgumentTypeInfo<?, ?>> STAMINA_TYPE_ARGUMENT_TYPE = COMMAND_ARGUMENT_TYPES.register("stamina_type", () -> (SingletonArgumentInfo)ArgumentTypeInfos.registerByClass(StaminaTypeArgumentType.class, (ArgumentTypeInfo)SingletonArgumentInfo.contextFree(StaminaTypeArgumentType::new)));

    public static void registerAll(IEventBus bus) {
        COMMAND_ARGUMENT_TYPES.register(bus);
    }
}

