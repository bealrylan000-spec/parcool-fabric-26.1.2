package com.alrex.parcool.platform.event.client;

import com.alrex.parcool.platform.eventbus.Event;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.resources.Identifier;

/** Fabric-backed stand-in for NeoForge's RegisterGuiLayersEvent. */
public final class RegisterGuiLayersEvent extends Event {
    public void registerAbove(Identifier existingLayer, Identifier id, HudElement layer) {
        HudElementRegistry.attachElementAfter(existingLayer, id, layer);
    }

    public void registerBelow(Identifier existingLayer, Identifier id, HudElement layer) {
        HudElementRegistry.attachElementBefore(existingLayer, id, layer);
    }
}
