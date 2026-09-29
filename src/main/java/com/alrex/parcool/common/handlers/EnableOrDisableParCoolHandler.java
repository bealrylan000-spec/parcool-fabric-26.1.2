package com.alrex.parcool.common.handlers;

import com.alrex.parcool.api.SoundEvents;
import com.alrex.parcool.client.input.KeyBindings;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.common.info.ClientSetting;
import com.alrex.parcool.common.network.payload.ClientInformationPayload;
import com.alrex.parcool.config.ParCoolConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import com.alrex.parcool.platform.dist.Dist;
import com.alrex.parcool.platform.dist.OnlyIn;
import com.alrex.parcool.platform.eventbus.SubscribeEvent;
import com.alrex.parcool.platform.event.client.ClientTickEvent;
import com.alrex.parcool.platform.network.PacketDistributor;

@OnlyIn(value=Dist.CLIENT)
public class EnableOrDisableParCoolHandler {
    @SubscribeEvent
    public static void onTick(ClientTickEvent.Post event) {
        if (KeyBindings.getKeyBindEnable().consumeClick()) {
            boolean currentStatus = ParCoolConfig.Client.Booleans.ParCoolIsActive.get() == false;
            ParCoolConfig.Client.Booleans.ParCoolIsActive.set(currentStatus);
            LocalPlayer player = Minecraft.getInstance().player;
            if (player == null) {
                return;
            }
            Parkourability parkourability = Parkourability.get((Player)player);
            if (parkourability == null) {
                return;
            }
            parkourability.getActionInfo().setClientSetting(ClientSetting.readFromLocalConfig());
            PacketDistributor.sendToServer((CustomPacketPayload)new ClientInformationPayload(player.getUUID(), false, parkourability.getClientInfo()), (CustomPacketPayload[])new CustomPacketPayload[0]);
            player.displayClientMessage((Component)Component.translatable((String)(currentStatus ? "parcool.message.enabled" : "parcool.message.disabled")), true);
            if (currentStatus) {
                player.playSound((SoundEvent)SoundEvents.PARCOOL_ENABLE.get(), 1.0f, 1.0f);
            } else {
                player.playSound((SoundEvent)SoundEvents.PARCOOL_DISABLE.get(), 1.0f, 1.0f);
            }
        }
    }
}

