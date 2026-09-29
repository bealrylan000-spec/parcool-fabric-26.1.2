package com.alrex.parcool.common.action;

import com.alrex.parcool.platform.attachment.AttachmentHolder;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.api.unstable.action.ParCoolActionEvent;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.StaminaConsumeTiming;
import com.alrex.parcool.common.attachment.Attachments;
import com.alrex.parcool.common.attachment.client.Animation;
import com.alrex.parcool.common.attachment.client.LocalStamina;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.common.attachment.common.ReadonlyStamina;
import com.alrex.parcool.common.network.payload.ActionStatePayload;
import com.alrex.parcool.config.ParCoolConfig;
import com.alrex.parcool.utilities.BufferUtil;
import java.nio.ByteBuffer;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import com.alrex.parcool.platform.dist.Dist;
import com.alrex.parcool.platform.dist.OnlyIn;
import com.alrex.parcool.platform.eventbus.Event;
import com.alrex.parcool.platform.event.client.RenderFrameEvent;
import com.alrex.parcool.platform.event.client.ViewportEvent;
import com.alrex.parcool.platform.eventbus.NeoForge;
import com.alrex.parcool.platform.event.tick.PlayerTickEvent;
import com.alrex.parcool.platform.network.PacketDistributor;
import org.apache.logging.log4j.Level;

public class ActionProcessor {
    private static final Identifier STAMINA_DEPLETED_SLOWNESS_MODIFIER_ID = Identifier.fromNamespaceAndPath((String)"parcool", (String)"exhausted.speed");
    private static final AttributeModifier STAMINA_DEPLETED_SLOWNESS_MODIFIER = new AttributeModifier(STAMINA_DEPLETED_SLOWNESS_MODIFIER_ID, -0.05, AttributeModifier.Operation.ADD_VALUE);
    private final ByteBuffer bufferOfPostState = ByteBuffer.allocate(128);
    private final ByteBuffer bufferOfPreState = ByteBuffer.allocate(128);
    private final ByteBuffer bufferOfStarting = ByteBuffer.allocate(128);
    private int staminaSyncCoolTimeTick = 5;

    public void onTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        Parkourability parkourability = Parkourability.get(player);
        boolean inClient = player.level().isClientSide();
        boolean inServer = !inClient;
        this.onTick$doPreprocess((PlayerTickEvent)event);
        if (inClient) {
            this.onTick$doPreprocessInClient((PlayerTickEvent)event, parkourability);
        } else {
            this.onTick$doPreprocessInServer((PlayerTickEvent)event);
        }
        List<Action> actions = parkourability.getList();
        boolean needSync = player.isLocalPlayer();
        if (needSync) {
            this.onTick$checkLimitationSynchronization(player, parkourability);
        }
        parkourability.getAdditionalProperties().onTick(player, parkourability);
        LinkedList<ActionStatePayload.Entry> syncStates = new LinkedList<ActionStatePayload.Entry>();
        for (Action action : actions) {
            NeoForge.EVENT_BUS.post((Event)new ParCoolActionEvent.Tick.Pre(player, action));
            this.processAction(player, parkourability, syncStates, inClient, action);
            NeoForge.EVENT_BUS.post((Event)new ParCoolActionEvent.Tick.Post(player, action));
        }
        if (needSync && !syncStates.isEmpty()) {
            this.onTick$sendSynchronizationPacket(player, syncStates);
        }
        if (inClient) {
            this.onTick$doPostProcessInClient((PlayerTickEvent)event, parkourability);
        }
    }

    private void onTick$doPreprocess(PlayerTickEvent event) {
    }

    private void onTick$doPreprocessInServer(PlayerTickEvent event) {
    }

    @OnlyIn(value=Dist.CLIENT)
    private void onTick$doPreprocessInClient(PlayerTickEvent event, Parkourability parkourability) {
        Player player = event.getEntity();
        if (!(player instanceof AbstractClientPlayer)) {
            return;
        }
        AbstractClientPlayer clientPlayer = (AbstractClientPlayer)player;
        Animation animation = Animation.get((Player)clientPlayer);
        animation.tick(clientPlayer, parkourability);
    }

    @OnlyIn(value=Dist.CLIENT)
    private void onTick$doPostProcessInClient(PlayerTickEvent event, Parkourability parkourability) {
        AttributeInstance attr;
        Player player = event.getEntity();
        if (!(player instanceof LocalPlayer)) {
            return;
        }
        LocalPlayer player2 = (LocalPlayer)player;
        if (!parkourability.limitationIsNotSynced()) {
            LocalStamina stamina = LocalStamina.get(player2);
            stamina.onTick(player2);
            if (--this.staminaSyncCoolTimeTick <= 0) {
                stamina.sync(player2);
                this.staminaSyncCoolTimeTick = 5;
            }
        }
        if ((attr = player2.getAttribute(Attributes.MOVEMENT_SPEED)) != null) {
            if (LocalStamina.get(player2).imposeExhaustionPenalty(player2) && parkourability.getClientInfo().get(ParCoolConfig.Client.Booleans.EnableStaminaExhaustionPenalty).booleanValue()) {
                player2.setSprinting(false);
                if (!attr.hasModifier(STAMINA_DEPLETED_SLOWNESS_MODIFIER_ID)) {
                    attr.addTransientModifier(STAMINA_DEPLETED_SLOWNESS_MODIFIER);
                }
            } else {
                attr.removeModifier(STAMINA_DEPLETED_SLOWNESS_MODIFIER_ID);
            }
        }
    }

    @OnlyIn(value=Dist.CLIENT)
    private void onTick$checkLimitationSynchronization(Player player, Parkourability parkourability) {
        if (player.isLocalPlayer() && player.tickCount > 127 && player.tickCount % 256 == 0 && parkourability.limitationIsNotSynced() && player instanceof LocalPlayer) {
            LocalPlayer localPlayer = (LocalPlayer)player;
            int trialCount = parkourability.getSynchronizeTrialCount();
            if (trialCount < 5) {
                parkourability.trySyncLimitation(localPlayer, parkourability);
                if (ParCoolConfig.Client.Booleans.ShowAutoResynchronizationNotification.get().booleanValue()) {
                    player.sendSystemMessage((Component)Component.translatable((String)"parcool.message.error.limitation.not_synced"));
                }
                ParCool.LOGGER.log(Level.WARN, "Detected ParCool Limitation is not synced. Sending synchronization request...");
            } else if (trialCount == 5) {
                parkourability.incrementSynchronizeTrialCount();
                player.sendSystemMessage((Component)Component.translatable((String)"parcool.message.error.limitation.fail_sync").withStyle(ChatFormatting.DARK_RED));
                ParCool.LOGGER.log(Level.ERROR, "Failed to synchronize ParCool Limitation. There may be problems about server connection. Please report to the developer after checking connection");
            }
        }
    }

    private void onTick$sendSynchronizationPacket(Player player, List<ActionStatePayload.Entry> syncStates) {
        PacketDistributor.sendToServer((CustomPacketPayload)new ActionStatePayload(player.getUUID(), syncStates), (CustomPacketPayload[])new CustomPacketPayload[0]);
    }

    private void processAction(Player player, Parkourability parkourability, LinkedList<ActionStatePayload.Entry> syncStates, boolean inClientSide, Action action) {
        boolean needSync = player.isLocalPlayer();
        if (needSync) {
            this.saveSynchronizationState(action, this.bufferOfPreState);
        }
        action.tick();
        action.onTick(player, parkourability);
        if (inClientSide) {
            action.onClientTick(player, parkourability);
        } else {
            action.onServerTick(player, parkourability);
        }
        if (needSync) {
            this.checkAndChangeActionState(player, parkourability, action, syncStates);
        }
        if (action.isDoing()) {
            action.onWorkingTick(player, parkourability);
            if (inClientSide) {
                action.onWorkingTickInClient(player, parkourability);
                if (needSync) {
                    action.onWorkingTickInLocalClient(player, parkourability);
                    if (action.getStaminaConsumeTiming() == StaminaConsumeTiming.OnWorking) {
                        this.consumeStamina(player, parkourability.getActionInfo().getStaminaConsumptionOf(action.getClass()));
                    }
                } else {
                    action.onWorkingTickInOtherClient(player, parkourability);
                }
            } else {
                action.onWorkingTickInServer(player, parkourability);
            }
        }
        if (needSync) {
            this.saveSynchronizationState(action, this.bufferOfPostState);
            if (!BufferUtil.haveSameContents(this.bufferOfPreState, this.bufferOfPostState)) {
                this.bufferOfPostState.rewind();
                byte[] data = new byte[this.bufferOfPostState.remaining()];
                this.bufferOfPostState.get(data);
                syncStates.addLast(new ActionStatePayload.Entry(action.getClass(), ActionStatePayload.Entry.Type.Normal, data));
                this.bufferOfPreState.clear();
                this.bufferOfPostState.clear();
            }
        }
    }

    @OnlyIn(value=Dist.CLIENT)
    private void checkAndChangeActionState(Player player, Parkourability parkourability, Action action, LinkedList<ActionStatePayload.Entry> syncStates) {
        if (!(player instanceof LocalPlayer)) {
            return;
        }
        LocalPlayer localPlayer = (LocalPlayer)player;
        if (action.isDoing()) {
            boolean canContinue;
            boolean bl = canContinue = parkourability.getActionInfo().can(action.getClass()) && !((ReadonlyStamina)AttachmentHolder.getData(player, Attachments.STAMINA)).isExhausted() && !((ParCoolActionEvent.TryToContinueEvent)NeoForge.EVENT_BUS.post((Event)new ParCoolActionEvent.TryToContinueEvent(player, action))).isCanceled() && !((ParCoolActionEvent.TryToContinue)NeoForge.EVENT_BUS.post((Event)new ParCoolActionEvent.TryToContinue(player, action))).isCanceled() && action.canContinue(player, parkourability);
            if (!canContinue) {
                NeoForge.EVENT_BUS.post((Event)new ParCoolActionEvent.Finish.Pre(player, action));
                action.finish(player);
                NeoForge.EVENT_BUS.post((Event)new ParCoolActionEvent.StopEvent(player, action));
                NeoForge.EVENT_BUS.post((Event)new ParCoolActionEvent.Finish.Post(player, action));
                syncStates.addLast(new ActionStatePayload.Entry(action.getClass(), ActionStatePayload.Entry.Type.Finish, new byte[0]));
            }
        } else {
            this.bufferOfStarting.clear();
            boolean start = !player.isSpectator() && !((ReadonlyStamina)AttachmentHolder.getData(player, Attachments.STAMINA)).isExhausted() && parkourability.getActionInfo().can(action.getClass()) && !((ParCoolActionEvent.TryToStartEvent)NeoForge.EVENT_BUS.post((Event)new ParCoolActionEvent.TryToStartEvent(player, action))).isCanceled() && !((ParCoolActionEvent.TryToStart)NeoForge.EVENT_BUS.post((Event)new ParCoolActionEvent.TryToStart(player, action))).isCanceled() && action.canStart(player, parkourability, this.bufferOfStarting);
            this.bufferOfStarting.flip();
            if (start) {
                NeoForge.EVENT_BUS.post((Event)new ParCoolActionEvent.Start.Pre(player, action));
                action.start(player, parkourability, this.bufferOfStarting);
                NeoForge.EVENT_BUS.post((Event)new ParCoolActionEvent.StartEvent(player, action));
                NeoForge.EVENT_BUS.post((Event)new ParCoolActionEvent.Start.Post(player, action));
                if (action.getStaminaConsumeTiming() == StaminaConsumeTiming.OnStart) {
                    this.consumeStamina((Player)localPlayer, parkourability.getActionInfo().getStaminaConsumptionOf(action.getClass()));
                }
                byte[] data = new byte[this.bufferOfStarting.remaining()];
                this.bufferOfStarting.get(data);
                syncStates.addLast(new ActionStatePayload.Entry(action.getClass(), ActionStatePayload.Entry.Type.Start, data));
            }
        }
    }

    private void saveSynchronizationState(Action action, ByteBuffer buffer) {
        buffer.clear();
        action.saveSynchronizedState(buffer);
        buffer.flip();
    }

    @OnlyIn(value=Dist.CLIENT)
    private void consumeStamina(Player player, int value) {
        if (player instanceof LocalPlayer) {
            LocalPlayer localPlayer = (LocalPlayer)player;
            LocalStamina.get(localPlayer).consume(localPlayer, value);
        }
    }

    @OnlyIn(value=Dist.CLIENT)
    public void onRenderTick(RenderFrameEvent.Pre event) {
        LocalPlayer clientPlayer = Minecraft.getInstance().player;
        if (clientPlayer == null) {
            return;
        }
        for (Player player : clientPlayer.getCommandSenderWorld().players()) {
            Parkourability parkourability = Parkourability.get(player);
            if (parkourability == null) {
                return;
            }
            List<Action> actions = parkourability.getList();
            for (Action action : actions) {
                action.onRenderTick((RenderFrameEvent)event, player, parkourability);
            }
            Animation animation = Animation.get(player);
            if (animation == null) {
                return;
            }
            animation.onRenderTick((RenderFrameEvent)event, player, parkourability);
        }
    }

    @OnlyIn(value=Dist.CLIENT)
    public void onViewRender(ViewportEvent.ComputeCameraAngles event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }
        Parkourability parkourability = Parkourability.get((Player)player);
        if (parkourability == null) {
            return;
        }
        Animation animation = Animation.get((Player)player);
        if (animation == null) {
            return;
        }
        animation.cameraSetup(event, player, parkourability);
    }
}

