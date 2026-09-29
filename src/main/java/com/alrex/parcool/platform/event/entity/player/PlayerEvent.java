package com.alrex.parcool.platform.event.entity.player;

import com.alrex.parcool.platform.eventbus.Event;
import net.minecraft.world.entity.player.Player;

/** Stand-in for {@code net.neoforged.neoforge.event.entity.player.PlayerEvent}. */
public abstract class PlayerEvent extends Event {
    private final Player player;

    protected PlayerEvent(Player player) {
        this.player = player;
    }

    public Player getEntity() {
        return player;
    }

    public static final class PlayerLoggedInEvent extends PlayerEvent {
        public PlayerLoggedInEvent(Player player) { super(player); }
    }

    public static final class PlayerLoggedOutEvent extends PlayerEvent {
        public PlayerLoggedOutEvent(Player player) { super(player); }
    }

    /** Fired on respawn (death or dimension change). */
    public static final class Clone extends PlayerEvent {
        private final Player original;
        private final boolean wasDeath;

        public Clone(Player newPlayer, Player original, boolean wasDeath) {
            super(newPlayer);
            this.original = original;
            this.wasDeath = wasDeath;
        }

        public Player getOriginal() {
            return original;
        }

        public boolean isWasDeath() {
            return wasDeath;
        }
    }
}
