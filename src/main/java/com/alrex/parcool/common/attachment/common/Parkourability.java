package com.alrex.parcool.common.attachment.common;

import com.alrex.parcool.platform.attachment.AttachmentHolder;

import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.Actions;
import com.alrex.parcool.common.action.AdditionalProperties;
import com.alrex.parcool.common.action.BehaviorEnforcer;
import com.alrex.parcool.common.attachment.Attachments;
import com.alrex.parcool.common.info.ActionInfo;
import com.alrex.parcool.common.info.ClientSetting;
import com.alrex.parcool.common.info.ServerLimitation;
import com.alrex.parcool.common.network.payload.ClientInformationPayload;
import com.alrex.parcool.config.ParCoolConfig;
import java.util.HashMap;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import com.alrex.parcool.platform.dist.Dist;
import com.alrex.parcool.platform.dist.OnlyIn;
import com.alrex.parcool.platform.network.PacketDistributor;

public class Parkourability {
    private final ActionInfo info;
    private final AdditionalProperties properties = new AdditionalProperties();
    private final BehaviorEnforcer enforcer = new BehaviorEnforcer();
    private final List<Action> actions = Actions.constructActionsList();
    private final HashMap<Class<? extends Action>, Action> actionsMap = new HashMap((int)((double)this.actions.size() * 1.5));
    private int synchronizeTrialCount = 0;

    public static Parkourability get(Player player) {
        return (Parkourability)AttachmentHolder.getData(player, Attachments.PARKOURABILITY);
    }

    public Parkourability() {
        for (Action action : this.actions) {
            this.actionsMap.put(action.getClass(), action);
        }
        this.info = new ActionInfo();
    }

    public <T extends Action> T get(Class<T> action) {
        Action value = this.actionsMap.getOrDefault(action, null);
        if (value == null) {
            throw new IllegalArgumentException("The Action instance is not registered:" + action.getSimpleName());
        }
        return (T)value;
    }

    public short getActionID(Action instance) {
        return Actions.getIndexOf(instance.getClass());
    }

    @Nullable
    public Action getActionFromID(short id) {
        if (0 <= id && id < this.actions.size()) {
            return this.actions.get(id);
        }
        return null;
    }

    public AdditionalProperties getAdditionalProperties() {
        return this.properties;
    }

    public BehaviorEnforcer getBehaviorEnforcer() {
        return this.enforcer;
    }

    public ActionInfo getActionInfo() {
        return this.info;
    }

    public ClientSetting getClientInfo() {
        return this.info.getClientSetting();
    }

    public ServerLimitation getServerLimitation() {
        return this.info.getServerLimitation();
    }

    public List<Action> getList() {
        return this.actions;
    }

    public void CopyFrom(Parkourability original) {
        this.getActionInfo().setClientSetting(original.getActionInfo().getClientSetting());
        this.getActionInfo().setServerLimitation(original.getActionInfo().getServerLimitation());
    }

    public boolean isDoingNothing() {
        return this.actions.stream().noneMatch(Action::isDoing);
    }

    public boolean getLimitedValue(ParCoolConfig.Client.Booleans client, ParCoolConfig.Server.Booleans server) {
        if (server.AdvantageousValue) {
            return this.getClientInfo().get(client) != false && this.getServerLimitation().get(server) != false;
        }
        return this.getClientInfo().get(client) == false && this.getServerLimitation().get(server) == false;
    }

    public int getLimitedValue(ParCoolConfig.Client.Integers client, ParCoolConfig.Server.Integers server) {
        if (server.Advantageous == ParCoolConfig.AdvantageousDirection.Higher) {
            return Math.min(this.getClientInfo().get(client), this.getServerLimitation().get(server));
        }
        return Math.max(this.getClientInfo().get(client), this.getServerLimitation().get(server));
    }

    public double getLimitedValue(ParCoolConfig.Client.Doubles client, ParCoolConfig.Server.Doubles server) {
        if (server.Advantageous == ParCoolConfig.AdvantageousDirection.Higher) {
            return Math.min(this.getClientInfo().get(client), this.getServerLimitation().get(server));
        }
        return Math.max(this.getClientInfo().get(client), this.getServerLimitation().get(server));
    }

    @OnlyIn(value=Dist.CLIENT)
    public void trySyncLimitation(LocalPlayer player, Parkourability parkourability) {
        ++this.synchronizeTrialCount;
        PacketDistributor.sendToServer((CustomPacketPayload)new ClientInformationPayload(player.getUUID(), true, parkourability.getClientInfo()), (CustomPacketPayload[])new CustomPacketPayload[0]);
    }

    @OnlyIn(value=Dist.CLIENT)
    public int getSynchronizeTrialCount() {
        return this.synchronizeTrialCount;
    }

    public void incrementSynchronizeTrialCount() {
        ++this.synchronizeTrialCount;
    }

    @OnlyIn(value=Dist.CLIENT)
    public boolean limitationIsNotSynced() {
        return !this.getServerLimitation().isSynced();
    }

    @SafeVarargs
    public final Boolean isDoingAny(Class<? extends Action> ... actions) {
        for (Class<? extends Action> action : actions) {
            if (!this.get(action).isDoing()) continue;
            return true;
        }
        return false;
    }
}

