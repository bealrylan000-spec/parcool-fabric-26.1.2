package com.alrex.parcool.common.info;

import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.Actions;
import com.alrex.parcool.common.stamina.StaminaType;
import com.alrex.parcool.config.ParCoolConfig;
import io.netty.buffer.ByteBuf;
import java.util.Arrays;
import java.util.EnumMap;
import net.minecraft.network.codec.StreamCodec;
import com.alrex.parcool.platform.dist.Dist;
import com.alrex.parcool.platform.dist.OnlyIn;

public abstract class ClientSetting {
    public static final StreamCodec<ByteBuf, ClientSetting> STREAM_CODEC = StreamCodec.of((buffer, value) -> value.writeTo((ByteBuf)buffer), ClientSetting::readFrom);
    public static final ClientSetting UNSYNCED_INSTANCE = new Default();

    public abstract boolean getPossibilityOf(Class<? extends Action> var1);

    public abstract int getStaminaConsumptionOf(Class<? extends Action> var1);

    public abstract StaminaType getRequestedStamina();

    public abstract Boolean get(ParCoolConfig.Client.Booleans var1);

    public abstract Integer get(ParCoolConfig.Client.Integers var1);

    public abstract Double get(ParCoolConfig.Client.Doubles var1);

    @OnlyIn(value=Dist.CLIENT)
    public static ClientSetting readFromLocalConfig() {
        ParCoolConfig.Client configInstance = ParCoolConfig.Client.getInstance();
        Remote instance = new Remote();
        for (int i = 0; i < instance.actionPossibilities.length; ++i) {
            instance.actionPossibilities[i] = (Boolean)configInstance.getPossibilityOf(Actions.LIST.get(i)).get();
            instance.staminaConsumptions[i] = (Integer)configInstance.getStaminaConsumptionOf(Actions.LIST.get(i)).get();
        }
        for (ParCoolConfig.Client.Booleans booleans : ParCoolConfig.Client.Booleans.values()) {
            instance.booleans.put(booleans, booleans.get());
        }
        for (Enum enum_ : ParCoolConfig.Client.Integers.values()) {
            instance.integers.put((ParCoolConfig.Client.Integers)enum_, ((ParCoolConfig.Client.Integers)enum_).get());
        }
        for (Enum enum_ : ParCoolConfig.Client.Doubles.values()) {
            instance.doubles.put((ParCoolConfig.Client.Doubles)enum_, ((ParCoolConfig.Client.Doubles)enum_).get());
        }
        instance.requestedStamina = (StaminaType)((Object)configInstance.StaminaType.get());
        return instance;
    }

    public void writeTo(ByteBuf buffer) {
        for (Class<? extends Action> action : Actions.LIST) {
            buffer.writeByte((int)((byte)(this.getPossibilityOf(action) ? 1 : 0)));
            buffer.writeInt(this.getStaminaConsumptionOf(action));
        }
        for (ParCoolConfig.Client.Booleans booleans : ParCoolConfig.Client.Booleans.values()) {
            buffer.writeByte((int)((byte)(this.get(booleans) != false ? 1 : 0)));
        }
        for (Enum enum_ : ParCoolConfig.Client.Integers.values()) {
            buffer.writeInt(this.get((ParCoolConfig.Client.Integers)enum_).intValue());
        }
        for (Enum enum_ : ParCoolConfig.Client.Doubles.values()) {
            buffer.writeDouble(this.get((ParCoolConfig.Client.Doubles)enum_).doubleValue());
        }
        buffer.writeByte(this.getRequestedStamina().ordinal());
    }

    public static ClientSetting readFrom(ByteBuf buffer) {
        Remote instance = new Remote();
        for (int i = 0; i < instance.actionPossibilities.length; ++i) {
            instance.actionPossibilities[i] = buffer.readByte() != 0;
            instance.staminaConsumptions[i] = buffer.readInt();
        }
        for (ParCoolConfig.Client.Booleans booleans : ParCoolConfig.Client.Booleans.values()) {
            instance.booleans.put(booleans, booleans.readFromBuffer(buffer));
        }
        for (Enum enum_ : ParCoolConfig.Client.Integers.values()) {
            instance.integers.put((ParCoolConfig.Client.Integers)enum_, ((ParCoolConfig.Client.Integers)enum_).readFromBuffer(buffer));
        }
        for (Enum enum_ : ParCoolConfig.Client.Doubles.values()) {
            instance.doubles.put((ParCoolConfig.Client.Doubles)enum_, ((ParCoolConfig.Client.Doubles)enum_).readFromBuffer(buffer));
        }
        instance.requestedStamina = StaminaType.values()[buffer.readByte()];
        return instance;
    }

    private static class Remote
    extends ClientSetting {
        private final boolean[] actionPossibilities = new boolean[Actions.LIST.size()];
        private final int[] staminaConsumptions = new int[Actions.LIST.size()];
        private final EnumMap<ParCoolConfig.Client.Booleans, Boolean> booleans = new EnumMap(ParCoolConfig.Client.Booleans.class);
        private final EnumMap<ParCoolConfig.Client.Integers, Integer> integers = new EnumMap(ParCoolConfig.Client.Integers.class);
        private final EnumMap<ParCoolConfig.Client.Doubles, Double> doubles = new EnumMap(ParCoolConfig.Client.Doubles.class);
        private StaminaType requestedStamina = null;

        public Remote() {
            Arrays.fill(this.actionPossibilities, true);
            Arrays.fill(this.staminaConsumptions, 0);
            for (ParCoolConfig.Client.Booleans booleans : ParCoolConfig.Client.Booleans.values()) {
                this.booleans.put(booleans, booleans.DefaultValue);
            }
            for (Enum enum_ : ParCoolConfig.Client.Integers.values()) {
                this.integers.put((ParCoolConfig.Client.Integers)enum_, ((ParCoolConfig.Client.Integers)enum_).DefaultValue);
            }
            for (Enum enum_ : ParCoolConfig.Client.Doubles.values()) {
                this.doubles.put((ParCoolConfig.Client.Doubles)enum_, ((ParCoolConfig.Client.Doubles)enum_).DefaultValue);
            }
        }

        @Override
        public boolean getPossibilityOf(Class<? extends Action> action) {
            return this.actionPossibilities[Actions.getIndexOf(action)];
        }

        @Override
        public int getStaminaConsumptionOf(Class<? extends Action> action) {
            return this.staminaConsumptions[Actions.getIndexOf(action)];
        }

        @Override
        public StaminaType getRequestedStamina() {
            return this.requestedStamina;
        }

        @Override
        public Boolean get(ParCoolConfig.Client.Booleans item) {
            return this.booleans.get(item);
        }

        @Override
        public Integer get(ParCoolConfig.Client.Integers item) {
            return this.integers.get(item);
        }

        @Override
        public Double get(ParCoolConfig.Client.Doubles item) {
            return this.doubles.get(item);
        }
    }

    private static class Default
    extends ClientSetting {
        private Default() {
        }

        @Override
        public boolean getPossibilityOf(Class<? extends Action> action) {
            return false;
        }

        @Override
        public int getStaminaConsumptionOf(Class<? extends Action> action) {
            return Integer.MAX_VALUE;
        }

        @Override
        public StaminaType getRequestedStamina() {
            return StaminaType.NONE;
        }

        @Override
        public Boolean get(ParCoolConfig.Client.Booleans item) {
            return item.DefaultValue;
        }

        @Override
        public Integer get(ParCoolConfig.Client.Integers item) {
            return item.DefaultValue;
        }

        @Override
        public Double get(ParCoolConfig.Client.Doubles item) {
            return item.DefaultValue;
        }
    }
}

