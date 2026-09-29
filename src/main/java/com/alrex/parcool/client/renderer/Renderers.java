package com.alrex.parcool.client.renderer;

import com.alrex.parcool.client.renderer.entity.ZiplineRopeRenderer;
import com.alrex.parcool.common.entity.EntityTypes;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.world.entity.EntityType;
import com.alrex.parcool.platform.dist.Dist;
import com.alrex.parcool.platform.dist.OnlyIn;
import com.alrex.parcool.platform.event.lifecycle.FMLClientSetupEvent;

@OnlyIn(value=Dist.CLIENT)
public class Renderers {
    public static void register(FMLClientSetupEvent event) {
        EntityRenderers.register((EntityType)((EntityType)EntityTypes.ZIPLINE_ROPE.get()), ZiplineRopeRenderer::new);
    }
}

