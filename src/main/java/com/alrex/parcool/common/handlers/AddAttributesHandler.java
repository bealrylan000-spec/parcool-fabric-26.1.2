package com.alrex.parcool.common.handlers;

import com.alrex.parcool.api.Attributes;
import net.minecraft.world.entity.EntityType;
import com.alrex.parcool.platform.eventbus.SubscribeEvent;
import com.alrex.parcool.platform.event.entity.EntityAttributeModificationEvent;

public class AddAttributesHandler {
    @SubscribeEvent
    public static void onAddAttributes(EntityAttributeModificationEvent event) {
        event.add(EntityType.PLAYER, Attributes.MAX_STAMINA);
        event.add(EntityType.PLAYER, Attributes.STAMINA_RECOVERY);
    }
}

