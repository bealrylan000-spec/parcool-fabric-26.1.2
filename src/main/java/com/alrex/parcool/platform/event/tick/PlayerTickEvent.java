package com.alrex.parcool.platform.event.tick;

import com.alrex.parcool.platform.eventbus.Event;
import net.minecraft.world.entity.player.Player;

/** Stand-in for {@code net.neoforged.neoforge.event.tick.PlayerTickEvent}. */
public abstract class PlayerTickEvent extends Event {
    private final Player player;

    protected PlayerTickEvent(Player player) {
        this.player = player;
    }

    public Player getEntity() {
        return player;
    }

    public static final class Pre extends PlayerTickEvent {
        public Pre(Player player) { super(player); }
    }

    public static final class Post extends PlayerTickEvent {
        public Post(Player player) { super(player); }
    }
}
