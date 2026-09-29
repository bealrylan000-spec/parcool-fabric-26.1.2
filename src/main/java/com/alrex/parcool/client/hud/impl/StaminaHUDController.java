package com.alrex.parcool.client.hud.impl;

import com.alrex.parcool.platform.attachment.AttachmentHolder;

import com.alrex.parcool.api.client.gui.ParCoolHUDEvent;
import com.alrex.parcool.client.hud.impl.HUDType;
import com.alrex.parcool.client.hud.impl.LightStaminaHUD;
import com.alrex.parcool.client.hud.impl.StaminaHUD;
import com.alrex.parcool.common.attachment.Attachments;
import com.alrex.parcool.common.attachment.client.LocalStamina;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.common.attachment.common.ReadonlyStamina;
import com.alrex.parcool.config.ParCoolConfig;
import javax.annotation.Nonnull;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElement;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import com.alrex.parcool.platform.dist.Dist;
import com.alrex.parcool.platform.dist.OnlyIn;
import com.alrex.parcool.platform.eventbus.Event;
import com.alrex.parcool.platform.event.tick.ClientTickEvent;
import com.alrex.parcool.platform.eventbus.NeoForge;

@OnlyIn(value=Dist.CLIENT)
public class StaminaHUDController
implements HudElement {
    public static Identifier ID = Identifier.fromNamespaceAndPath((String)"parcool", (String)"hud.stamina");
    LightStaminaHUD lightStaminaHUD = new LightStaminaHUD();
    StaminaHUD staminaHUD = new StaminaHUD();

    public void onTick(ClientTickEvent.Post event) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || player.isCreative()) {
            return;
        }
        this.lightStaminaHUD.onTick(event, player);
        this.staminaHUD.onTick(event, player);
    }

    public void extractRenderState(@Nonnull GuiGraphicsExtractor graphics, @Nonnull DeltaTracker partialTick) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return;
        }
        if (!ParCoolConfig.Client.Booleans.ParCoolIsActive.get().booleanValue()) {
            return;
        }
        Parkourability parkourability = Parkourability.get((Player)player);
        LocalStamina localStamina = LocalStamina.get(player);
        ReadonlyStamina stamina = (ReadonlyStamina)AttachmentHolder.getData(player, Attachments.STAMINA);
        if (ParCoolConfig.Client.Booleans.HideStaminaHUDWhenStaminaIsInfinite.get().booleanValue() && parkourability.getActionInfo().isStaminaInfinite(localStamina, player)) {
            return;
        }
        if (!localStamina.shouldShowHUD(player)) {
            return;
        }
        if (((ParCoolHUDEvent.RenderEvent)NeoForge.EVENT_BUS.post((Event)new ParCoolHUDEvent.RenderEvent(graphics, partialTick))).isCanceled()) {
            return;
        }
        switch ((HUDType)((Object)ParCoolConfig.Client.getInstance().StaminaHUDType.get())) {
            case Light: {
                this.lightStaminaHUD.render(graphics, parkourability, stamina, partialTick.getGameTimeDeltaPartialTick(true));
                break;
            }
            case Normal: {
                this.staminaHUD.render(graphics, parkourability, stamina, partialTick.getGameTimeDeltaPartialTick(true));
            }
        }
    }
}

