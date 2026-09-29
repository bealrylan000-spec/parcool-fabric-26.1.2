package com.alrex.parcool.server.limitation;

import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.action.Actions;
import com.alrex.parcool.common.stamina.StaminaType;
import com.alrex.parcool.config.ParCoolConfig;
import com.alrex.parcool.server.limitation.ActionLimitation;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;

public class Limitation {
    private boolean enabled = false;
    private final ID id;
    private final EnumMap<ParCoolConfig.Server.Booleans, Boolean> booleans = new EnumMap(ParCoolConfig.Server.Booleans.class);
    private final EnumMap<ParCoolConfig.Server.Integers, Integer> integers = new EnumMap(ParCoolConfig.Server.Integers.class);
    private final EnumMap<ParCoolConfig.Server.Doubles, Double> doubles = new EnumMap(ParCoolConfig.Server.Doubles.class);
    private final ActionLimitation[] actionLimitations = new ActionLimitation[Actions.LIST.size()];
    private StaminaType forcedStamina = StaminaType.NONE;

    public Limitation(ID id) {
        this.id = id;
        for (int i = 0; i < this.actionLimitations.length; ++i) {
            this.actionLimitations[i] = new ActionLimitation(true, 0);
        }
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public ID getID() {
        return this.id;
    }

    public boolean isPermitted(Class<? extends Action> action) {
        return this.actionLimitations[Actions.getIndexOf(action)].isPossible();
    }

    public int getLeastStaminaConsumption(Class<? extends Action> action) {
        return this.actionLimitations[Actions.getIndexOf(action)].getLeastStaminaConsumption();
    }

    public boolean get(ParCoolConfig.Server.Booleans item) {
        Boolean value = this.booleans.get(item);
        return value != null ? value : item.DefaultValue;
    }

    public int get(ParCoolConfig.Server.Integers item) {
        Integer value = this.integers.get(item);
        return value != null ? value : item.DefaultValue;
    }

    public double get(ParCoolConfig.Server.Doubles item) {
        Double value = this.doubles.get(item);
        return value != null ? value : item.DefaultValue;
    }

    public StaminaType getForcedStamina() {
        return this.forcedStamina;
    }

    public void set(ParCoolConfig.Server.Booleans item, boolean value) {
        this.booleans.put(item, value);
    }

    public void set(ParCoolConfig.Server.Integers item, int value) {
        this.integers.put(item, value);
    }

    public void set(ParCoolConfig.Server.Doubles item, double value) {
        this.doubles.put(item, value);
    }

    public void setEnabled(boolean value) {
        this.enabled = value;
    }

    public void setPossibilityOf(Class<? extends Action> action, boolean value) {
        short index = Actions.getIndexOf(action);
        this.actionLimitations[index] = new ActionLimitation(value, this.actionLimitations[index].getLeastStaminaConsumption());
    }

    public void setForcedStamina(StaminaType forcedStamina) {
        this.forcedStamina = forcedStamina;
    }

    public void setLeastStaminaConsumption(Class<? extends Action> action, int value) {
        short index = Actions.getIndexOf(action);
        this.actionLimitations[index] = new ActionLimitation(this.actionLimitations[index].isPossible(), value);
    }

    public void setAllDefault() {
        for (ParCoolConfig.Server.Booleans booleans : ParCoolConfig.Server.Booleans.values()) {
            this.booleans.put(booleans, booleans.DefaultValue);
        }
        for (Enum enum_ : ParCoolConfig.Server.Integers.values()) {
            this.integers.put((ParCoolConfig.Server.Integers)enum_, ((ParCoolConfig.Server.Integers)enum_).DefaultValue);
        }
        for (int i = 0; i < this.actionLimitations.length; ++i) {
            this.actionLimitations[i] = new ActionLimitation(true, 0);
        }
        this.forcedStamina = StaminaType.NONE;
    }

    public void readFromServerConfig() {
        this.enabled = (Boolean)ParCoolConfig.Server.getInstance().LimitationEnabled.get();
        for (ParCoolConfig.Server.Booleans booleans : ParCoolConfig.Server.Booleans.values()) {
            this.booleans.put(booleans, booleans.get());
        }
        for (Enum enum_ : ParCoolConfig.Server.Integers.values()) {
            this.integers.put((ParCoolConfig.Server.Integers)enum_, ((ParCoolConfig.Server.Integers)enum_).get());
        }
        for (Enum enum_ : ParCoolConfig.Server.Doubles.values()) {
            this.doubles.put((ParCoolConfig.Server.Doubles)enum_, ((ParCoolConfig.Server.Doubles)enum_).get());
        }
        for (int i = 0; i < this.actionLimitations.length; ++i) {
            this.actionLimitations[i] = new ActionLimitation(ParCoolConfig.Server.getInstance().getPermissionOf(Actions.getByIndex(i)), ParCoolConfig.Server.getInstance().getLeastStaminaConsumptionOf(Actions.getByIndex(i)));
        }
        this.forcedStamina = (StaminaType)((Object)ParCoolConfig.Server.getInstance().StaminaType.get());
    }

    public void saveTo(JsonWriter writer) {
        LimitationJson json = new LimitationJson();
        json.imposed = this.enabled;
        json.booleans = new LinkedList<LimitationJson.BooleanItem>();
        for (ParCoolConfig.Server.Booleans booleans : ParCoolConfig.Server.Booleans.values()) {
            LimitationJson.BooleanItem booleanItem = new LimitationJson.BooleanItem();
            booleanItem.name = booleans.Path;
            booleanItem.value = this.get(booleans);
            json.booleans.add(booleanItem);
        }
        json.integers = new LinkedList<LimitationJson.IntegerItem>();
        for (Enum enum_ : ParCoolConfig.Server.Integers.values()) {
            LimitationJson.IntegerItem integerItem = new LimitationJson.IntegerItem();
            integerItem.name = ((ParCoolConfig.Server.Integers)enum_).Path;
            integerItem.value = this.get((ParCoolConfig.Server.Integers)enum_);
            json.integers.add(integerItem);
        }
        json.doubles = new LinkedList<LimitationJson.DoubleItem>();
        for (Enum enum_ : ParCoolConfig.Server.Doubles.values()) {
            LimitationJson.DoubleItem doubleItem = new LimitationJson.DoubleItem();
            doubleItem.name = ((ParCoolConfig.Server.Doubles)enum_).Path;
            doubleItem.value = this.get((ParCoolConfig.Server.Doubles)enum_);
            json.doubles.add(doubleItem);
        }
        json.actions = new LinkedList<LimitationJson.ActionPermission>();
        for (int i = 0; i < this.actionLimitations.length; ++i) {
            ActionLimitation limitation = this.actionLimitations[i];
            if (limitation == null) continue;
            Class<? extends Action> action = Actions.getByIndex(i);
            LimitationJson.ActionPermission actionPermission = new LimitationJson.ActionPermission();
            actionPermission.name = action.getSimpleName();
            actionPermission.permitted = limitation.isPossible();
            actionPermission.stamina_consumption = limitation.getLeastStaminaConsumption();
            json.actions.add(actionPermission);
        }
        json.stamina = this.getForcedStamina();
        Gson gson = new Gson();
        gson.toJson((Object)json, LimitationJson.class, writer);
    }

    public void loadFrom(JsonReader reader) {
        Gson gson = new Gson();
        LimitationJson parsed = (LimitationJson)gson.fromJson(reader, LimitationJson.class);
        this.enabled = parsed.imposed;
        block0: for (LimitationJson.BooleanItem booleanItem : parsed.booleans) {
            for (Enum enum_ : ParCoolConfig.Server.Booleans.values()) {
                if (!((ParCoolConfig.Server.Booleans)enum_).getPath().equals(booleanItem.name)) continue;
                this.booleans.put((ParCoolConfig.Server.Booleans)enum_, booleanItem.value);
                continue block0;
            }
        }
        block2: for (LimitationJson.IntegerItem integerItem : parsed.integers) {
            for (Enum enum_ : ParCoolConfig.Server.Integers.values()) {
                if (!((ParCoolConfig.Server.Integers)enum_).getPath().equals(integerItem.name)) continue;
                this.integers.put((ParCoolConfig.Server.Integers)enum_, integerItem.value);
                continue block2;
            }
        }
        block4: for (LimitationJson.DoubleItem doubleItem : parsed.doubles) {
            for (Enum enum_ : ParCoolConfig.Server.Doubles.values()) {
                if (!((ParCoolConfig.Server.Doubles)enum_).getPath().equals(doubleItem.name)) continue;
                this.doubles.put((ParCoolConfig.Server.Doubles)enum_, doubleItem.value);
                continue block4;
            }
        }
        for (LimitationJson.ActionPermission actionPermission : parsed.actions) {
            for (int i = 0; i < Actions.LIST.size(); ++i) {
                if (!Actions.LIST.get(i).getSimpleName().equals(actionPermission.name)) continue;
                this.actionLimitations[i] = new ActionLimitation(actionPermission.permitted, actionPermission.stamina_consumption);
            }
        }
        this.forcedStamina = parsed.stamina;
    }

    public static class ID
    implements Comparable<ID> {
        public final String group;
        public final String name;

        public ID(String group, String name) {
            this.group = group;
            this.name = name;
        }

        public String getGroup() {
            return this.group;
        }

        public String getName() {
            return this.name;
        }

        public boolean equals(Object obj) {
            if (obj instanceof ID) {
                ID other = (ID)obj;
                return this.group.equals(other.group) && this.name.equals(other.name);
            }
            return false;
        }

        @Override
        public int compareTo(ID o) {
            int groupCompare = this.group.compareTo(o.group);
            if (groupCompare != 0) {
                return groupCompare;
            }
            return this.name.compareTo(o.name);
        }
    }

    private static class LimitationJson {
        public Boolean imposed;
        public StaminaType stamina;
        public List<BooleanItem> booleans;
        public List<IntegerItem> integers;
        public List<DoubleItem> doubles;
        public List<ActionPermission> actions;

        private LimitationJson() {
        }

        public static class BooleanItem {
            public String name;
            public Boolean value;
        }

        public static class IntegerItem {
            public String name;
            public Integer value;
        }

        public static class DoubleItem {
            public String name;
            public Double value;
        }

        public static class ActionPermission {
            public String name;
            public Boolean permitted;
            public Integer stamina_consumption;
        }
    }
}

