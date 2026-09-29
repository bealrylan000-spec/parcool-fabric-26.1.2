package com.alrex.parcool.common.info;

import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.Actions;
import com.alrex.parcool.common.stamina.StaminaType;
import com.alrex.parcool.config.ParCoolConfig;
import com.alrex.parcool.server.limitation.Limitation;
import com.alrex.parcool.server.limitation.Limitations;
import io.netty.buffer.ByteBuf;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumMap;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;

public abstract class ServerLimitation {
    public static final StreamCodec<ByteBuf, ServerLimitation> STREAM_CODEC = StreamCodec.of((buf, limitation) -> limitation.writeTo((ByteBuf)buf), ServerLimitation::readFrom);
    public static final ServerLimitation UNSYNCED_INSTANCE = new Default();

    public abstract boolean isPermitted(Class<? extends Action> var1);

    public abstract int getStaminaConsumptionOf(Class<? extends Action> var1);

    public abstract Boolean get(ParCoolConfig.Server.Booleans var1);

    public abstract Integer get(ParCoolConfig.Server.Integers var1);

    public abstract Double get(ParCoolConfig.Server.Doubles var1);

    public abstract StaminaType getForcedStamina();

    public abstract boolean isSynced();

    public static ServerLimitation get(ServerPlayer player) {
        Collection<Limitation> limitations = Limitations.getLimitationsOf(player.getUUID());
        Remote instance = new Remote();
        if (Limitations.getGlobalLimitation().isEnabled()) {
            instance.apply(Limitations.getGlobalLimitation());
        }
        for (Limitation limitation : limitations) {
            if (!limitation.isEnabled()) continue;
            instance.apply(limitation);
        }
        return instance;
    }

    public void writeTo(ByteBuf buffer) {
        for (Class<? extends Action> action : Actions.LIST) {
            buffer.writeByte((int)((byte)(this.isPermitted(action) ? 1 : 0)));
            buffer.writeInt(this.getStaminaConsumptionOf(action));
        }
        for (ParCoolConfig.Server.Booleans booleans : ParCoolConfig.Server.Booleans.values()) {
            buffer.writeByte((int)((byte)(this.get(booleans) != false ? 1 : 0)));
        }
        for (Enum enum_ : ParCoolConfig.Server.Integers.values()) {
            buffer.writeInt(this.get((ParCoolConfig.Server.Integers)enum_).intValue());
        }
        for (Enum enum_ : ParCoolConfig.Server.Doubles.values()) {
            buffer.writeDouble(this.get((ParCoolConfig.Server.Doubles)enum_).doubleValue());
        }
        buffer.writeByte(this.getForcedStamina().ordinal());
    }

    public static ServerLimitation readFrom(ByteBuf buffer) {
        Remote instance = new Remote();
        for (int i = 0; i < instance.actionPossibilities.length; ++i) {
            instance.actionPossibilities[i] = buffer.readByte() != 0;
            instance.leastStaminaConsumptions[i] = buffer.readInt();
        }
        for (ParCoolConfig.Server.Booleans booleans : ParCoolConfig.Server.Booleans.values()) {
            instance.booleans.put(booleans, booleans.readFromBuffer(buffer));
        }
        for (Enum enum_ : ParCoolConfig.Server.Integers.values()) {
            instance.integers.put((ParCoolConfig.Server.Integers)enum_, ((ParCoolConfig.Server.Integers)enum_).readFromBuffer(buffer));
        }
        for (Enum enum_ : ParCoolConfig.Server.Doubles.values()) {
            instance.doubles.put((ParCoolConfig.Server.Doubles)enum_, ((ParCoolConfig.Server.Doubles)enum_).readFromBuffer(buffer));
        }
        instance.forcedStamina = StaminaType.values()[buffer.readByte()];
        return instance;
    }

    private static class Remote
    extends ServerLimitation {
        private final boolean[] actionPossibilities = new boolean[Actions.LIST.size()];
        private final int[] leastStaminaConsumptions = new int[Actions.LIST.size()];
        private final EnumMap<ParCoolConfig.Server.Booleans, Boolean> booleans = new EnumMap(ParCoolConfig.Server.Booleans.class);
        private final EnumMap<ParCoolConfig.Server.Integers, Integer> integers = new EnumMap(ParCoolConfig.Server.Integers.class);
        private final EnumMap<ParCoolConfig.Server.Doubles, Double> doubles = new EnumMap(ParCoolConfig.Server.Doubles.class);
        private StaminaType forcedStamina = StaminaType.NONE;

        public Remote() {
            Arrays.fill(this.actionPossibilities, true);
            Arrays.fill(this.leastStaminaConsumptions, 0);
            for (ParCoolConfig.Server.Booleans booleans : ParCoolConfig.Server.Booleans.values()) {
                this.booleans.put(booleans, booleans.AdvantageousValue);
            }
            for (Enum enum_ : ParCoolConfig.Server.Integers.values()) {
                this.integers.put((ParCoolConfig.Server.Integers)enum_, ((ParCoolConfig.Server.Integers)enum_).Advantageous == ParCoolConfig.AdvantageousDirection.Higher ? ((ParCoolConfig.Server.Integers)enum_).Max : ((ParCoolConfig.Server.Integers)enum_).Min);
            }
            for (Enum enum_ : ParCoolConfig.Server.Doubles.values()) {
                this.doubles.put((ParCoolConfig.Server.Doubles)enum_, ((ParCoolConfig.Server.Doubles)enum_).Advantageous == ParCoolConfig.AdvantageousDirection.Higher ? ((ParCoolConfig.Server.Doubles)enum_).Max : ((ParCoolConfig.Server.Doubles)enum_).Min);
            }
        }

        @Override
        public boolean isPermitted(Class<? extends Action> action) {
            return this.actionPossibilities[Actions.getIndexOf(action)];
        }

        @Override
        public int getStaminaConsumptionOf(Class<? extends Action> action) {
            return this.leastStaminaConsumptions[Actions.getIndexOf(action)];
        }

        @Override
        public Boolean get(ParCoolConfig.Server.Booleans item) {
            return this.booleans.get(item);
        }

        @Override
        public Integer get(ParCoolConfig.Server.Integers item) {
            return this.integers.get(item);
        }

        @Override
        public Double get(ParCoolConfig.Server.Doubles item) {
            return this.doubles.get(item);
        }

        @Override
        public StaminaType getForcedStamina() {
            return this.forcedStamina;
        }

        @Override
        public boolean isSynced() {
            return true;
        }

        void apply(Limitation limitation) {
            for (int i = 0; i < Actions.LIST.size(); ++i) {
                if (this.actionPossibilities[i]) {
                    this.actionPossibilities[i] = limitation.isPermitted(Actions.LIST.get(i));
                }
                this.leastStaminaConsumptions[i] = Math.max(this.leastStaminaConsumptions[i], limitation.getLeastStaminaConsumption(Actions.LIST.get(i)));
            }
            for (ParCoolConfig.Server.Booleans booleans : ParCoolConfig.Server.Booleans.values()) {
                if (this.booleans.get(booleans) != booleans.AdvantageousValue) continue;
                this.booleans.put(booleans, limitation.get(booleans));
            }
            for (Enum enum_ : ParCoolConfig.Server.Integers.values()) {
                this.integers.put((ParCoolConfig.Server.Integers)enum_, ((ParCoolConfig.Server.Integers)enum_).Advantageous == ParCoolConfig.AdvantageousDirection.Higher ? Math.min(limitation.get((ParCoolConfig.Server.Integers)enum_), this.integers.get(enum_)) : Math.max(limitation.get((ParCoolConfig.Server.Integers)enum_), this.integers.get(enum_)));
            }
            for (Enum enum_ : ParCoolConfig.Server.Doubles.values()) {
                this.doubles.put((ParCoolConfig.Server.Doubles)enum_, ((ParCoolConfig.Server.Doubles)enum_).Advantageous == ParCoolConfig.AdvantageousDirection.Higher ? Math.min(limitation.get((ParCoolConfig.Server.Doubles)enum_), this.doubles.get(enum_)) : Math.max(limitation.get((ParCoolConfig.Server.Doubles)enum_), this.doubles.get(enum_)));
            }
            this.forcedStamina = limitation.getForcedStamina();
        }
    }

    private static class Default
    extends ServerLimitation {
        private Default() {
        }

        @Override
        public boolean isPermitted(Class<? extends Action> action) {
            return false;
        }

        @Override
        public int getStaminaConsumptionOf(Class<? extends Action> action) {
            return Integer.MAX_VALUE;
        }

        @Override
        public Boolean get(ParCoolConfig.Server.Booleans item) {
            return !item.AdvantageousValue;
        }

        @Override
        public Integer get(ParCoolConfig.Server.Integers item) {
            return item.Advantageous == ParCoolConfig.AdvantageousDirection.Lower ? item.Max : item.Min;
        }

        @Override
        public Double get(ParCoolConfig.Server.Doubles item) {
            return item.Advantageous == ParCoolConfig.AdvantageousDirection.Lower ? item.Max : item.Min;
        }

        @Override
        public StaminaType getForcedStamina() {
            return StaminaType.NONE;
        }

        @Override
        public boolean isSynced() {
            return false;
        }
    }
}

