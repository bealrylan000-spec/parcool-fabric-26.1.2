package com.alrex.parcool;

import com.alrex.parcool.client.hud.HUDRegistry;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.client.renderer.Renderers;
import com.alrex.parcool.common.attachment.ClientAttachments;
import com.alrex.parcool.common.registries.EventBusForgeRegistry;
import com.alrex.parcool.common.item.Items;
import com.alrex.parcool.extern.AdditionalMods;
import com.alrex.parcool.platform.dist.Dist;
import com.alrex.parcool.platform.dist.FMLEnvironment;
import com.alrex.parcool.platform.event.client.ClientTickEvent;
import com.alrex.parcool.platform.event.client.MovementInputUpdateEvent;
import com.alrex.parcool.platform.event.client.RegisterGuiLayersEvent;
import com.alrex.parcool.platform.event.client.RegisterKeyMappingsEvent;
import com.alrex.parcool.platform.event.lifecycle.FMLClientSetupEvent;
import com.alrex.parcool.platform.eventbus.IEventBus;
import com.alrex.parcool.platform.eventbus.NeoForge;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudLayerRegistrationCallback;
import net.fabricmc.fabric.api.client.rendering.v1.HudElementRegistry;

/** Client-only entrypoint (fabric.mod.json "client" entrypoint key). */
public class ParCoolFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        IEventBus bus = NeoForge.EVENT_BUS;

        EventBusForgeRegistry.registerClient(bus);
        ClientAttachments.registerAll(bus);

        bus.register(HUDRegistry.class);
        bus.addListener(RegisterKeyMappingsEvent.class, KeyBindings::register);
        bus.post(new RegisterKeyMappingsEvent());

        HudLayerRegistrationCallback.EVENT.register(drawer ->
                bus.post(new RegisterGuiLayersEvent(HudElementRegistry.class.cast(drawer))));

        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            bus.post(new ClientTickEvent.Post());
            bus.post(new MovementInputUpdateEvent());
        });

        // Deferred to CLIENT_STARTED so Minecraft.getInstance() and its subsystems (ItemColors,
        // etc.) are fully constructed before mod code (e.g. Items.registerColors, Renderers)
        // touches them - unlike NeoForge, Fabric's onInitializeClient() runs too early for that.
        net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents.CLIENT_STARTED.register(mc -> {
            bus.addListener(FMLClientSetupEvent.class, Renderers::register);
            bus.addListener(FMLClientSetupEvent.class, Items::registerColors);
            bus.post(new FMLClientSetupEvent());
        });

        AdditionalMods.initInClient();
        if (FMLEnvironment.dist == Dist.CLIENT) {
            // no-op: parity with original code's switch branch
        }
    }
}
