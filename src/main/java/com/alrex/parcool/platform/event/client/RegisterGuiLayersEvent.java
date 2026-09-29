package com.alrex.parcool.platform.event.client;

import com.alrex.parcool.platform.eventbus.Event;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.resources.ResourceLocation;

/**
 * Stand-in for {@code net.neoforged.neoforge.client.event.RegisterGuiLayersEvent}. Backed by
 * Fabric API's {@code HudLayerRegistrationCallback}, which uses the same underlying vanilla
 * LayeredDraw registrar type (`LayeredDraw.Layer`), so ParCool's own HUD layer implementation
 * needs no changes at all - only this registration shim differs.
 */
public final class RegisterGuiLayersEvent extends Event {
    private final net.fabricmc.fabric.api.client.rendering.v1.LayeredDrawerWrapper registrar;

    public RegisterGuiLayersEvent(net.fabricmc.fabric.api.client.rendering.v1.LayeredDrawerWrapper registrar) {
        this.registrar = registrar;
    }

    public void registerAbove(ResourceLocation existingLayer, ResourceLocation id, LayeredDraw.Layer layer) {
        registrar.attachLayerAfter(existingLayer, id, layer);
    }

    public void registerBelow(ResourceLocation existingLayer, ResourceLocation id, LayeredDraw.Layer layer) {
        registrar.attachLayerBefore(existingLayer, id, layer);
    }
}
