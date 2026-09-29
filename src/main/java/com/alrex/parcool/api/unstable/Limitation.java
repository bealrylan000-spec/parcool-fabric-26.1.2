package com.alrex.parcool.api.unstable;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.action.Action;
import com.alrex.parcool.common.stamina.StaminaType;
import com.alrex.parcool.config.ParCoolConfig;
import com.alrex.parcool.server.limitation.Limitation;
import com.alrex.parcool.server.limitation.Limitations;
import com.google.gson.stream.JsonWriter;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public abstract class Limitation {
    protected final com.alrex.parcool.server.limitation.Limitation instance;

    public static Limitation get(ServerPlayer player, ID limitationID) {
        return new NormalLimitation(player, Limitations.createLimitationOf(player.getUUID(), limitationID.convert()));
    }

    public static Limitation getIndividual(ServerPlayer player) {
        return new NormalLimitation(player, Limitations.createLimitationOf(player.getUUID(), Limitations.INDIVIDUAL_ID));
    }

    public static Limitation getGlobal(MinecraftServer server) {
        return new GlobalLimitation(server);
    }

    public Limitation set(ParCoolConfig.Server.Booleans item, boolean value) {
        this.instance.set(item, value);
        return this;
    }

    public Limitation set(ParCoolConfig.Server.Integers item, int value) {
        this.instance.set(item, value);
        return this;
    }

    public Limitation set(ParCoolConfig.Server.Doubles item, double value) {
        this.instance.set(item, value);
        return this;
    }

    public Limitation setStaminaType(StaminaType type) {
        this.instance.setForcedStamina(type);
        return this;
    }

    public Limitation setDefault() {
        this.instance.setAllDefault();
        return this;
    }

    public Limitation permit(Class<? extends Action> action, boolean permission) {
        this.instance.setPossibilityOf(action, permission);
        return this;
    }

    public Limitation setLeastStaminaConsumption(Class<? extends Action> action, int value) {
        this.instance.setLeastStaminaConsumption(action, value);
        return this;
    }

    public Limitation enable() {
        this.instance.setEnabled(true);
        return this;
    }

    public Limitation disable() {
        this.instance.setEnabled(false);
        return this;
    }

    public boolean get(ParCoolConfig.Server.Booleans item) {
        return this.instance.get(item);
    }

    public int get(ParCoolConfig.Server.Integers item) {
        return this.instance.get(item);
    }

    public double get(ParCoolConfig.Server.Doubles item) {
        return this.instance.get(item);
    }

    public StaminaType getStaminaType() {
        return this.instance.getForcedStamina();
    }

    public boolean isPermitted(Class<? extends Action> action) {
        return this.instance.isPermitted(action);
    }

    public int getLeastStaminaConsumption(Class<? extends Action> action) {
        return this.instance.getLeastStaminaConsumption(action);
    }

    public boolean isEnabled() {
        return this.instance.isEnabled();
    }

    public abstract void apply();

    public abstract void save();

    public static boolean delete(ID limitationID) {
        return Limitations.delete(limitationID.convert());
    }

    private Limitation(com.alrex.parcool.server.limitation.Limitation instance) {
        this.instance = instance;
    }

    private static class NormalLimitation
    extends Limitation {
        private final ServerPlayer player;

        private NormalLimitation(ServerPlayer player, com.alrex.parcool.server.limitation.Limitation instance) {
            super(instance);
            this.player = player;
        }

        @Override
        public void apply() {
            Limitations.updateOnlyLimitation(this.player);
        }

        @Override
        public void save() {
            Path filepath = Limitations.getActualFilePath(this.player.getUUID(), this.instance.getID());
            if (filepath == null) {
                ParCool.LOGGER.error("On Saving Limitation : Could not resolve file(" + String.valueOf(this.player.getUUID()) + "," + this.instance.getID().getGroup() + ":" + this.instance.getID().getName() + ")");
                return;
            }
            File limitationFile = filepath.toFile();
            if (!limitationFile.getParentFile().exists()) {
                limitationFile.getParentFile().mkdirs();
            }
            try (JsonWriter writer = new JsonWriter((Writer)new OutputStreamWriter((OutputStream)new BufferedOutputStream(Files.newOutputStream(limitationFile.toPath(), StandardOpenOption.CREATE, StandardOpenOption.WRITE)), StandardCharsets.UTF_8));){
                this.instance.saveTo(writer);
            }
            catch (IOException e) {
                ParCool.LOGGER.error("IOException during saving limitation : " + e.getMessage());
            }
        }
    }

    public static class ID {
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

        Limitation.ID convert() {
            return new Limitation.ID(this.group, this.name);
        }

        public String toString() {
            return this.group + ":" + this.name;
        }
    }

    private static class GlobalLimitation
    extends Limitation {
        private final MinecraftServer server;

        private GlobalLimitation(MinecraftServer server) {
            super(Limitations.getGlobalLimitation());
            this.server = server;
        }

        @Override
        public void apply() {
            for (ServerPlayer player : this.server.getPlayerList().getPlayers()) {
                Limitations.updateOnlyLimitation(player);
            }
        }

        @Override
        public void save() {
        }
    }
}

