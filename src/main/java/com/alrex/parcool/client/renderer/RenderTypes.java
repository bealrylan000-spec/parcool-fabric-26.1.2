package com.alrex.parcool.client.renderer;

import net.minecraft.client.renderer.rendertype.RenderType;

/**
 * Render types used by ParCool's custom client geometry.
 *
 * Minecraft 26.1 removed the old RenderStateShard/CompositeState builder API.
 * The vanilla leash pipeline is a close match for zipline rope geometry: it
 * consumes position + color + packed light and does not require a texture.
 */
public final class RenderTypes {
    public static final RenderType ZIPLINE_2D = net.minecraft.client.renderer.rendertype.RenderTypes.leash();
    public static final RenderType ZIPLINE_3D = net.minecraft.client.renderer.rendertype.RenderTypes.leash();

    private RenderTypes() {
    }
}
