package com.alrex.parcool.platform.event.entity;

import com.alrex.parcool.platform.eventbus.Event;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

/** Stand-in for {@code net.neoforged.neoforge.event.entity.EntityJoinLevelEvent}. */
public final class EntityJoinLevelEvent extends Event {
    private final Entity entity;
    private final Level level;

    public EntityJoinLevelEvent(Entity entity, Level level) {
        this.entity = entity;
        this.level = level;
    }

    public Entity getEntity() {
        return entity;
    }

    public Level getLevel() {
        return level;
    }
}
