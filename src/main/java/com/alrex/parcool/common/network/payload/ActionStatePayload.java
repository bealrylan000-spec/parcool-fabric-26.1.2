package com.alrex.parcool.common.network.payload;

import com.alrex.parcool.api.unstable.action.ParCoolActionEvent;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.Actions;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.common.network.ActionSynchronizationBroadcaster;
import io.netty.buffer.ByteBuf;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nonnull;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import com.alrex.parcool.platform.eventbus.Event;
import com.alrex.parcool.platform.eventbus.NeoForge;
import com.alrex.parcool.platform.network.IPayloadContext;

public record ActionStatePayload(UUID playerID, List<Entry> states) implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<ActionStatePayload> TYPE = new CustomPacketPayload.Type(ResourceLocation.fromNamespaceAndPath((String)"parcool", (String)"payload.action_state"));
    public static final StreamCodec<ByteBuf, ActionStatePayload> CODEC = StreamCodec.of(ActionStatePayload::encode, ActionStatePayload::decode);

    @Nonnull
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    private static void encode(ByteBuf buf, ActionStatePayload payload) {
        buf.writeLong(payload.playerID().getMostSignificantBits());
        buf.writeLong(payload.playerID().getLeastSignificantBits());
        buf.writeInt(payload.states().size());
        for (Entry s : payload.states()) {
            s.encode(buf);
        }
    }

    private static ActionStatePayload decode(ByteBuf buf) {
        UUID id = new UUID(buf.readLong(), buf.readLong());
        int size = buf.readInt();
        ArrayList<Entry> entries = new ArrayList<Entry>(size);
        for (int i = 0; i < size; ++i) {
            entries.addLast(Entry.decode(buf));
        }
        return new ActionStatePayload(id, entries);
    }

    public void processPlayer(Player player) {
        Parkourability parkourability = Parkourability.get(player);
        for (Entry state : this.states()) {
            Action action = parkourability.get(state.action());
            switch (state.type().ordinal()) {
                case 1: {
                    ByteBuffer buf = state.getDataAsBuffer();
                    NeoForge.EVENT_BUS.post((Event)new ParCoolActionEvent.Start.Pre(player, action));
                    action.start(player, parkourability, buf);
                    NeoForge.EVENT_BUS.post((Event)new ParCoolActionEvent.StartEvent(player, action));
                    NeoForge.EVENT_BUS.post((Event)new ParCoolActionEvent.Start.Post(player, action));
                    break;
                }
                case 2: {
                    NeoForge.EVENT_BUS.post((Event)new ParCoolActionEvent.Finish.Pre(player, action));
                    action.finish(player);
                    NeoForge.EVENT_BUS.post((Event)new ParCoolActionEvent.StopEvent(player, action));
                    NeoForge.EVENT_BUS.post((Event)new ParCoolActionEvent.Finish.Post(player, action));
                    break;
                }
                case 0: {
                    action.restoreSynchronizedState(state.getDataAsBuffer());
                }
            }
        }
    }

    public static void handleClient(ActionStatePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Level world = context.player().level();
            Player player = world.getPlayerByUUID(payload.playerID());
            if (player == null || player.isLocalPlayer()) {
                return;
            }
            payload.processPlayer(player);
        });
    }

    public static void handleServer(ActionStatePayload payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player();
            ActionSynchronizationBroadcaster.add(payload);
            payload.processPlayer(player);
        });
    }

    public record Entry(Class<? extends Action> action, Type type, byte[] data) {
        private void encode(ByteBuf buf) {
            buf.writeShort((int)Actions.getIndexOf(this.action)).writeByte(this.type().ordinal()).writeInt(this.data().length).writeBytes(this.data);
        }

        public ByteBuffer getDataAsBuffer() {
            return ByteBuffer.wrap(this.data);
        }

        private static Entry decode(ByteBuf buf) {
            Class<? extends Action> action = Actions.getByIndex(buf.readShort());
            Type type = Type.values()[buf.readByte()];
            int remaining = buf.readInt();
            byte[] data = new byte[remaining];
            buf.readBytes(data);
            return new Entry(action, type, data);
        }

        public static enum Type {
            Normal,
            Start,
            Finish;

        }
    }
}

