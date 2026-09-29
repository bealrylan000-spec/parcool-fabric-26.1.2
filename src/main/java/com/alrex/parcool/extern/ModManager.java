package com.alrex.parcool.extern;

import net.fabricmc.loader.api.FabricLoader;

public abstract class ModManager {
    private boolean installed = false;
    private final String modId;

    public ModManager(String modId) {
        this.modId = modId;
    }

    public void init() {
        this.installed = FabricLoader.getInstance().isModLoaded(this.modId);
    }

    public void initInClient() {
    }

    public void initInDedicatedServer() {
    }

    public boolean isInstalled() {
        return this.installed;
    }

    public String getModID() {
        return this.modId;
    }
}
