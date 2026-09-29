package com.alrex.parcool.extern;

import com.alrex.parcool.common.attachment.client.LocalStamina;
import com.alrex.parcool.extern.AdditionalModsEventConsumer;
import com.alrex.parcool.extern.ModManager;
import com.alrex.parcool.extern.betterthirdperson.BetterThirdPersonManager;
import com.alrex.parcool.extern.epicfight.EpicFightManager;
import com.alrex.parcool.extern.paraglider.ParagliderManager;
import com.alrex.parcool.extern.shouldersurfing.ShoulderSurfingManager;
import java.util.Arrays;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import com.alrex.parcool.platform.dist.Dist;
import com.alrex.parcool.platform.dist.OnlyIn;
import com.alrex.parcool.platform.eventbus.NeoForge;

public enum AdditionalMods {
    BETTER_THIRD_PERSON(BetterThirdPersonManager::new),
    SHOULDER_SURFING(ShoulderSurfingManager::new),
    PARAGLIDER(ParagliderManager::new),
    EPIC_FIGHT(EpicFightManager::new);

    private final ModManager manager;

    private AdditionalMods(Supplier<ModManager> supplier) {
        this.manager = supplier.get();
    }

    public static BetterThirdPersonManager betterThirdPerson() {
        return (BetterThirdPersonManager)AdditionalMods.BETTER_THIRD_PERSON.manager;
    }

    public static ShoulderSurfingManager shoulderSurfing() {
        return (ShoulderSurfingManager)AdditionalMods.SHOULDER_SURFING.manager;
    }

    public static ParagliderManager paraglider() {
        return (ParagliderManager)AdditionalMods.PARAGLIDER.manager;
    }

    public static EpicFightManager epicFight() {
        return (EpicFightManager)AdditionalMods.EPIC_FIGHT.manager;
    }

    public ModManager get() {
        return this.manager;
    }

    public static void init() {
        Arrays.stream(AdditionalMods.values()).map(AdditionalMods::get).forEach(ModManager::init);
    }

    public static void initInClient() {
        NeoForge.EVENT_BUS.register(AdditionalModsEventConsumer.Client.class);
        Arrays.stream(AdditionalMods.values()).map(AdditionalMods::get).forEach(ModManager::initInClient);
    }

    public static void initInDedicatedServer() {
        Arrays.stream(AdditionalMods.values()).map(AdditionalMods::get).forEach(ModManager::initInDedicatedServer);
    }

    @OnlyIn(value=Dist.CLIENT)
    public static boolean isCameraDecoupled() {
        return AdditionalMods.shoulderSurfing().isCameraDecoupled() != false || AdditionalMods.betterThirdPerson().isCameraDecoupled();
    }

    @OnlyIn(value=Dist.CLIENT)
    public static boolean isUsingExternalStamina() {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return false;
        }
        return LocalStamina.get(player).isUsingExternalStamina();
    }
}

