package com.alrex.parcool.common.entity;

import com.alrex.parcool.common.entity.zipline.ZiplineRopeEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import com.alrex.parcool.platform.eventbus.IEventBus;
import com.alrex.parcool.platform.registries.DeferredHolder;
import com.alrex.parcool.platform.registries.DeferredRegister;

public class EntityTypes {
    private static final DeferredRegister<EntityType<?>> REGISTER = DeferredRegister.create((ResourceKey)Registries.ENTITY_TYPE, (String)"parcool");
    public static final DeferredHolder<EntityType<?>, EntityType<ZiplineRopeEntity>> ZIPLINE_ROPE = REGISTER.register("zipline_rope", () -> EntityType.Builder.of(ZiplineRopeEntity::new, (MobCategory)MobCategory.MISC).noSave().clientTrackingRange(37).updateInterval(Integer.MAX_VALUE).sized(0.1f, 0.1f).noSummon().build("zipline_rope"));

    public static void registerAll(IEventBus bus) {
        REGISTER.register(bus);
    }
}

