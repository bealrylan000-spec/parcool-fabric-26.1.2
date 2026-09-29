package com.alrex.parcool.server.limitation;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.common.info.ServerLimitation;
import com.alrex.parcool.common.network.payload.LimitationPayload;
import com.alrex.parcool.server.limitation.Limitation;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.util.Collection;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.LevelResource;
import com.alrex.parcool.platform.event.server.ServerAboutToStartEvent;
import com.alrex.parcool.platform.event.server.ServerStoppingEvent;
import com.alrex.parcool.platform.network.PacketDistributor;
import org.apache.commons.io.FileUtils;

public class Limitations {
    private static final Limitation.ID GLOBAL_ID = new Limitation.ID("parcool", "global");
    public static final Limitation.ID INDIVIDUAL_ID = new Limitation.ID("parcool", "individual");
    private static final SortedMap<UUID, SortedMap<Limitation.ID, Limitation>> Loaded = new TreeMap<UUID, SortedMap<Limitation.ID, Limitation>>();
    private static final SortedSet<Limitation.ID> RegisteredID = new TreeSet<Limitation.ID>();
    private static final Limitation GlobalLimitation = new Limitation(GLOBAL_ID);
    @Nullable
    private static Path LimitationFolderRootPath = null;
    private static final LevelResource SERVERCONFIG = new LevelResource("serverconfig");

    private static SortedMap<Limitation.ID, Limitation> getLimitationMapOf(UUID playerID) {
        SortedMap<Limitation.ID, Limitation> map = (SortedMap<Limitation.ID, Limitation>)Loaded.get(playerID);
        if (map == null) {
            map = Limitations.load(playerID);
        }
        return map;
    }

    public static Collection<Limitation> getLimitationsOf(UUID playerID) {
        return Limitations.getLimitationMapOf(playerID).values();
    }

    public static Limitation createLimitationOf(UUID playerID, Limitation.ID id) {
        Limitation limitation = Limitations.getLimitationOf(playerID, id);
        if (limitation != null) {
            return limitation;
        }
        limitation = new Limitation(id);
        RegisteredID.add(id);
        Limitations.getLimitationMapOf(playerID).put(id, limitation);
        return limitation;
    }

    public static boolean delete(Limitation.ID id) {
        if (LimitationFolderRootPath == null) {
            return false;
        }
        for (SortedMap<Limitation.ID, Limitation> limitationMap : Loaded.values()) {
            limitationMap.remove(id);
        }
        try {
            FileUtils.deleteDirectory((File)Limitations.getFolderPath(LimitationFolderRootPath, id).toFile());
        }
        catch (IOException e) {
            return false;
        }
        return true;
    }

    public static Limitation getGlobalLimitation() {
        return GlobalLimitation;
    }

    public static Collection<Limitation.ID> getRegisteredIDs() {
        return RegisteredID;
    }

    @Nullable
    public static Limitation getLimitationOf(UUID playerID, Limitation.ID id) {
        if (id.equals(GLOBAL_ID)) {
            return GlobalLimitation;
        }
        return (Limitation)Limitations.getLimitationMapOf(playerID).get(id);
    }

    public static void update(ServerPlayer player) {
        Parkourability parkourability = Parkourability.get((Player)player);
        parkourability.getActionInfo().setServerLimitation(ServerLimitation.get(player));
        PacketDistributor.sendToPlayer((ServerPlayer)player, (CustomPacketPayload)new LimitationPayload(parkourability.getActionInfo().getServerLimitation()), (CustomPacketPayload[])new CustomPacketPayload[0]);
    }

    public static void updateOnlyLimitation(ServerPlayer player) {
        Parkourability parkourability = Parkourability.get((Player)player);
        parkourability.getActionInfo().setServerLimitation(ServerLimitation.get(player));
        PacketDistributor.sendToPlayer((ServerPlayer)player, (CustomPacketPayload)new LimitationPayload(parkourability.getActionInfo().getServerLimitation()), (CustomPacketPayload[])new CustomPacketPayload[0]);
    }

    public static SortedMap<Limitation.ID, Limitation> load(UUID playerID) {
        if (LimitationFolderRootPath == null) {
            throw new IllegalStateException("When loading Limitation Player:" + playerID.toString() + ", Initialization is not completed yet");
        }
        File limitationFolder = LimitationFolderRootPath.toFile();
        File[] directories = limitationFolder.listFiles(File::isDirectory);
        if (directories == null) {
            ParCool.LOGGER.error("Cannot get Limitation folders");
            return null;
        }
        SortedMap playerData = Loaded.computeIfAbsent(playerID, k -> new TreeMap());
        for (File dir : directories) {
            File[] limitationGroups = dir.listFiles(File::isDirectory);
            if (limitationGroups == null) {
                ParCool.LOGGER.error("Cannot get Limitation folders of '" + dir.getName() + "'");
                continue;
            }
            for (File limitationGroup : limitationGroups) {
                File[] limitationFiles = limitationGroup.listFiles(file -> file.isFile() && file.canRead() && file.getName().endsWith(".json"));
                if (limitationFiles == null) {
                    ParCool.LOGGER.error("Cannot get Limitation files of '" + dir.getName() + "'");
                    continue;
                }
                Limitation.ID limitationID = new Limitation.ID(dir.getName(), limitationGroup.getName());
                RegisteredID.add(limitationID);
                for (File limitationFile : limitationFiles) {
                    String limitationFilename = limitationFile.getName();
                    String uuidString = limitationFilename.substring(0, limitationFilename.length() - ".json".length());
                    UUID playerUUID = UUID.fromString(uuidString);
                    if (!playerUUID.equals(playerID)) continue;
                    try (JsonReader reader = new JsonReader((Reader)new InputStreamReader((InputStream)new BufferedInputStream(new FileInputStream(limitationFile)), StandardCharsets.UTF_8));){
                        Limitation limitation = new Limitation(limitationID);
                        limitation.loadFrom(reader);
                        playerData.put(limitation.getID(), limitation);
                    }
                    catch (FileNotFoundException e) {
                        ParCool.LOGGER.error("Could not read '" + limitationFile.getAbsolutePath() + "', skipped.");
                    }
                    catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        ParCool.LOGGER.info("Limitation of " + String.valueOf(playerID) + " was loaded");
        return playerData;
    }

    public static void unload(UUID playerID) {
        if (LimitationFolderRootPath == null) {
            throw new IllegalStateException("When loading Limitation Player:" + playerID.toString() + ", Initialization is not completed yet");
        }
        SortedMap map = (SortedMap)Loaded.remove(playerID);
        if (map == null) {
            ParCool.LOGGER.warn("Limitation entry is not loaded for UUID:" + String.valueOf(playerID) + ". Skipped.");
            return;
        }
        for (Limitation limitation : map.values()) {
            File limitationFile = Limitations.getActualFilePath(playerID, limitation.getID()).toFile();
            if (!limitationFile.getParentFile().exists()) {
                limitationFile.getParentFile().mkdirs();
            }
            try (JsonWriter writer = new JsonWriter((Writer)new OutputStreamWriter((OutputStream)new BufferedOutputStream(Files.newOutputStream(limitationFile.toPath(), StandardOpenOption.CREATE, StandardOpenOption.WRITE)), StandardCharsets.UTF_8));){
                limitation.saveTo(writer);
            }
            catch (IOException e) {
                ParCool.LOGGER.error("IOException during saving limitation : " + e.getMessage());
            }
        }
        ParCool.LOGGER.info("Limitation of " + String.valueOf(playerID) + " was unloaded");
    }

    public static void init(ServerAboutToStartEvent event) {
        GlobalLimitation.readFromServerConfig();
        Path configPath = Limitations.getServerConfigPath(event.getServer());
        LimitationFolderRootPath = configPath.resolve("parcool").resolve("limitations");
        File limitationFolder = LimitationFolderRootPath.toFile();
        if (!limitationFolder.exists()) {
            limitationFolder.mkdirs();
        }
    }

    public static void save(ServerStoppingEvent event) {
        Path configPath = Limitations.getServerConfigPath(event.getServer());
        Path limitationRootPath = configPath.resolve("parcool").resolve("limitations");
        for (Map.Entry<UUID, SortedMap<Limitation.ID, Limitation>> limitationEntry : Loaded.entrySet()) {
            UUID playerID = limitationEntry.getKey();
            for (Limitation limitation : limitationEntry.getValue().values()) {
                File limitationFile = Limitations.getFolderPath(limitationRootPath, limitation.getID()).resolve(playerID.toString() + ".json").toFile();
                if (!limitationFile.getParentFile().exists()) {
                    limitationFile.getParentFile().mkdirs();
                }
                try (JsonWriter writer = new JsonWriter((Writer)new OutputStreamWriter((OutputStream)new BufferedOutputStream(Files.newOutputStream(limitationFile.toPath(), StandardOpenOption.CREATE, StandardOpenOption.WRITE)), StandardCharsets.UTF_8));){
                    limitation.saveTo(writer);
                }
                catch (IOException e) {
                    ParCool.LOGGER.error("IOException during saving limitation : " + e.getMessage());
                }
            }
        }
    }

    private static Path getServerConfigPath(MinecraftServer server) {
        Path serverConfig = server.getWorldPath(SERVERCONFIG);
        if (!Files.isDirectory(serverConfig, new LinkOption[0])) {
            try {
                Files.createDirectories(serverConfig, new FileAttribute[0]);
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return serverConfig;
    }

    public static Path getFolderPath(Path limitationRootPath, Limitation.ID id) {
        return limitationRootPath.resolve(id.getGroup()).resolve(id.getName());
    }

    @Nullable
    public static Path getActualFilePath(UUID playerID, Limitation.ID id) {
        if (LimitationFolderRootPath == null) {
            return null;
        }
        Path folderPath = Limitations.getFolderPath(LimitationFolderRootPath, id);
        return folderPath.resolve(String.valueOf(playerID) + ".json");
    }
}

