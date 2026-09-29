package com.alrex.parcool.common.registries;

import com.alrex.parcool.client.hud.HUDManager;
import com.alrex.parcool.client.input.KeyRecorder;
import com.alrex.parcool.common.action.ActionProcessor;
import com.alrex.parcool.common.handlers.EnableOrDisableParCoolHandler;
import com.alrex.parcool.common.handlers.InputHandler;
import com.alrex.parcool.common.handlers.LoginLogoutHandler;
import com.alrex.parcool.common.handlers.OpenSettingsParCoolHandler;
import com.alrex.parcool.common.handlers.PlayerCloneHandler;
import com.alrex.parcool.common.handlers.PlayerDamageHandler;
import com.alrex.parcool.common.handlers.PlayerJoinHandler;
import com.alrex.parcool.common.handlers.PlayerJumpHandler;
import com.alrex.parcool.common.handlers.PlayerVisibilityHandler;
import com.alrex.parcool.common.network.ActionSynchronizationBroadcaster;
import com.alrex.parcool.common.network.StaminaSynchronizationBroadcaster;
import com.alrex.parcool.common.potion.ParCoolBrewingRecipe;
import com.alrex.parcool.platform.event.client.RenderFrameEvent;
import com.alrex.parcool.platform.event.client.ViewportEvent;
import com.alrex.parcool.platform.event.tick.PlayerTickEvent;
import com.alrex.parcool.platform.eventbus.IEventBus;

public class EventBusForgeRegistry {
    private static final ActionProcessor ACTION_PROCESSOR = new ActionProcessor();

    public static ActionProcessor actionProcessor() { return ACTION_PROCESSOR; }

    public static void register(IEventBus bus) {
        bus.register(ParCoolBrewingRecipe.class);
        bus.register(PlayerJumpHandler.class);
        bus.register(LoginLogoutHandler.class);
        bus.register(PlayerVisibilityHandler.class);
        bus.register(PlayerDamageHandler.class);
        bus.register(PlayerCloneHandler.class);
        bus.register(ActionSynchronizationBroadcaster.class);
        bus.register(StaminaSynchronizationBroadcaster.class);
        bus.addListener(PlayerTickEvent.Post.class, ACTION_PROCESSOR::onTick);
    }

    public static void registerClient(IEventBus bus) {
        bus.register(KeyRecorder.class);
        bus.register(OpenSettingsParCoolHandler.class);
        bus.register(EnableOrDisableParCoolHandler.class);
        bus.register(PlayerJoinHandler.class);
        bus.register(HUDManager.getInstance());
        bus.register(InputHandler.class);
        bus.addListener(RenderFrameEvent.Pre.class, ACTION_PROCESSOR::onRenderTick);
        bus.addListener(ViewportEvent.ComputeCameraAngles.class, ACTION_PROCESSOR::onViewRender);
    }
}
