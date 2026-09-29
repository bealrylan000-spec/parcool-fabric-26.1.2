package com.alrex.parcool.common.stamina;

import com.alrex.parcool.common.stamina.IParCoolStaminaHandler;
import com.alrex.parcool.common.stamina.handlers.HungerStaminaHandler;
import com.alrex.parcool.common.stamina.handlers.InfiniteStaminaHandler;
import com.alrex.parcool.common.stamina.handlers.ParCoolStaminaHandler;
import com.alrex.parcool.extern.AdditionalMods;
import io.netty.buffer.ByteBuf;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Player;

public enum StaminaType {
    NONE(InfiniteStaminaHandler::new),
    PARCOOL(ParCoolStaminaHandler::new),
    HUNGER(HungerStaminaHandler::new),
    PARAGLIDER(AdditionalMods.paraglider()::newParagliderStaminaHandlerFor),
    EPIC_FIGHT(AdditionalMods.epicFight()::newStaminaHandlerFor);

    private final Function<Player, IParCoolStaminaHandler> constructor;
    public static final StreamCodec<ByteBuf, StaminaType> STREAM_CODEC;

    private StaminaType(Function<Player, IParCoolStaminaHandler> constructor) {
        this.constructor = constructor;
    }

    private StaminaType(Supplier<IParCoolStaminaHandler> constructor) {
        this.constructor = player -> (IParCoolStaminaHandler)constructor.get();
    }

    public IParCoolStaminaHandler newHandler(Player player) {
        return this.constructor.apply(player);
    }

    static {
        STREAM_CODEC = StreamCodec.composite((StreamCodec)ByteBufCodecs.VAR_INT, Enum::ordinal, v -> StaminaType.values()[v]);
    }
}

