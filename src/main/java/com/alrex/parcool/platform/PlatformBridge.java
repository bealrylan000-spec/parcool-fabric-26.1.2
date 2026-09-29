package com.alrex.parcool.platform;

import com.alrex.parcool.platform.event.RegisterCommandsEvent;
import com.alrex.parcool.platform.event.entity.EntityAttributeModificationEvent;
import com.alrex.parcool.platform.event.entity.EntityJoinLevelEvent;
import com.alrex.parcool.platform.event.entity.player.PlayerEvent;
import com.alrex.parcool.platform.event.server.ServerAboutToStartEvent;
import com.alrex.parcool.platform.event.server.ServerStoppingEvent;
import com.alrex.parcool.platform.event.tick.PlayerTickEvent;
import com.alrex.parcool.platform.event.tick.ServerTickEvent;
import com.alrex.parcool.platform.eventbus.NeoForge;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.PlayerCopyCallback;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.world.entity.player.Player;

/**
 * Wires vanilla-server-side Fabric API callbacks into the NeoForge-shaped event bus so that
 * ParCool's own handler classes (which subscribe via {@code @SubscribeEvent}) don't need to know
 * they're running on Fabric. Registered once from {@link ParCoolFabric#onInitialize}.
 *
 * NOTE: PlayerTickEvent here fires once per player at the END of each server tick (via
 * ServerTickEvents.END_SERVER_TICK, iterating players), not from inside each player's own tick()
 * like NeoForge's real event. Functionally equivalent for ParCool's per-player action processing,
 * just very slightly later in the tick.
 */
public final class PlatformBridge {
    private PlatformBridge() {}

    public static void registerCommon() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (var player : server.getPlayerList().getPlayers()) {
                NeoForge.EVENT_BUS.post(new PlayerTickEvent.Post(player));
            }
            NeoForge.EVENT_BUS.post(new ServerTickEvent.Post(server));
        });

        ServerLifecycleEvents.SERVER_STARTING.register(server ->
                NeoForge.EVENT_BUS.post(new ServerAboutToStartEvent(server)));
        ServerLifecycleEvents.SERVER_STOPPING.register(server ->
                NeoForge.EVENT_BUS.post(new ServerStoppingEvent(server)));

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) ->
                NeoForge.EVENT_BUS.post(new PlayerEvent.PlayerLoggedInEvent(handler.player)));
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) ->
                NeoForge.EVENT_BUS.post(new PlayerEvent.PlayerLoggedOutEvent(handler.player)));

        // Fabric API's respawn copy-callback matches NeoForge's PlayerEvent.Clone shape closely.
        PlayerCopyCallback.EVENT.register((newPlayer, oldPlayer, wasDeath) ->
                NeoForge.EVENT_BUS.post(new PlayerEvent.Clone(newPlayer, oldPlayer, wasDeath)));

        ServerEntityEvents.ENTITY_LOAD.register((entity, level) -> {
            if (entity instanceof Player) {
                NeoForge.EVENT_BUS.post(new EntityJoinLevelEvent(entity, level));
            }
        });

        CommandRegistrationCallback.EVENT.register((dispatcher, ctx, selection) ->
                NeoForge.EVENT_BUS.post(new RegisterCommandsEvent(dispatcher, ctx, selection)));
    }

    /** Called once, right before the Player attribute supplier is built - see PlayerAttributeMixin. */
    public static java.util.List<EntityAttributeModificationEvent.Entry> collectPlayerAttributeAdditions() {
        java.util.Map<net.minecraft.world.entity.EntityType<?>, java.util.List<EntityAttributeModificationEvent.Entry>> pending = new java.util.HashMap<>();
        NeoForge.EVENT_BUS.post(new EntityAttributeModificationEvent(pending));
        return pending.getOrDefault(net.minecraft.world.entity.EntityType.PLAYER, java.util.List.of());
    }
}
