package com.alrex.parcool;

import com.alrex.parcool.api.Attributes;
import com.alrex.parcool.api.Effects;
import com.alrex.parcool.api.SoundEvents;
import com.alrex.parcool.common.attachment.Attachments;
import com.alrex.parcool.common.block.Blocks;
import com.alrex.parcool.common.block.TileEntities;
import com.alrex.parcool.common.entity.EntityTypes;
import com.alrex.parcool.common.handlers.AddAttributesHandler;
import com.alrex.parcool.common.item.CreativeTabs;
import com.alrex.parcool.common.item.DataComponents;
import com.alrex.parcool.common.item.Items;
import com.alrex.parcool.common.item.recipe.Recipes;
import com.alrex.parcool.common.network.NetworkRegistries;
import com.alrex.parcool.common.potion.Potions;
import com.alrex.parcool.common.registries.EventBusForgeRegistry;
import com.alrex.parcool.config.ParCoolConfig;
import com.alrex.parcool.extern.AdditionalMods;
import com.alrex.parcool.platform.PlatformBridge;
import com.alrex.parcool.platform.dist.Dist;
import com.alrex.parcool.platform.dist.FMLEnvironment;
import com.alrex.parcool.platform.event.lifecycle.FMLCommonSetupEvent;
import com.alrex.parcool.platform.event.lifecycle.FMLLoadCompleteEvent;
import com.alrex.parcool.platform.eventbus.IEventBus;
import com.alrex.parcool.platform.eventbus.NeoForge;
import com.alrex.parcool.server.command.args.ParCoolArgumentTypeInfos;
import com.alrex.parcool.server.limitation.Limitations;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;

/**
 * Common (server + client) entrypoint. Fabric registers this via the "main" entrypoint key in
 * fabric.mod.json. Mirrors the original ParCool.java constructor: everything that isn't gated to
 * a specific side goes here; client-only wiring lives in ParCoolFabricClient.
 */
public class ParCoolFabric implements ModInitializer {
    public static final String MOD_ID = "parcool";
    public static final Logger LOGGER = LogManager.getLogger();

    @Override
    public void onInitialize() {
        IEventBus bus = NeoForge.EVENT_BUS;

        EventBusForgeRegistry.register(bus);
        PlatformBridge.registerCommon();

        bus.register(AddAttributesHandler.class);
        NetworkRegistries.registerAll();

        Effects.registerAll(bus);
        Potions.registerAll(bus);
        Attributes.registerAll(bus);
        SoundEvents.registerAll(bus);
        Blocks.registerAll(bus);
        Items.registerAll(bus);
        CreativeTabs.registerAll(bus);
        Recipes.registerAll(bus);
        EntityTypes.registerAll(bus);
        TileEntities.registerAll(bus);
        DataComponents.registerAll(bus);
        Attachments.registerAll(bus);
        ParCoolArgumentTypeInfos.registerAll(bus);

        bus.addListener(com.alrex.parcool.platform.event.server.ServerAboutToStartEvent.class, Limitations::init);
        bus.addListener(com.alrex.parcool.platform.event.server.ServerStoppingEvent.class, Limitations::save);

        Path configDir = FabricLoader.getInstance().getConfigDir();
        ParCoolConfig.Server.getConfigSpec().load(configDir.resolve("parcool-server.properties"));
        ParCoolConfig.Client.getConfigSpec().load(configDir.resolve("parcool-client.properties"));

        bus.post(new FMLCommonSetupEvent());

        AdditionalMods.init();
        if (FMLEnvironment.dist == Dist.DEDICATED_SERVER) {
            AdditionalMods.initInDedicatedServer();
        }
        bus.post(new FMLLoadCompleteEvent());
    }
}
