package com.alrex.parcool.common.attachment.common;

import com.alrex.parcool.api.Attributes;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.common.network.payload.StaminaPayload;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import com.alrex.parcool.platform.dist.Dist;
import com.alrex.parcool.platform.dist.OnlyIn;
import com.alrex.parcool.platform.network.PacketDistributor;

public record ReadonlyStamina(boolean isExhausted, int value, int max) {
    public static final Codec<ReadonlyStamina> CODEC = RecordCodecBuilder.create(staminaInstance -> staminaInstance.group((App)Codec.BOOL.fieldOf("exhausted").forGetter(ReadonlyStamina::isExhausted), (App)Codec.INT.fieldOf("value").forGetter(ReadonlyStamina::value), (App)Codec.INT.fieldOf("max").forGetter(ReadonlyStamina::max)).apply((Applicative)staminaInstance, ReadonlyStamina::new));
    public static final StreamCodec<ByteBuf, ReadonlyStamina> STREAM_CODEC = StreamCodec.composite((StreamCodec)ByteBufCodecs.BOOL, ReadonlyStamina::isExhausted, (StreamCodec)ByteBufCodecs.VAR_INT, ReadonlyStamina::value, (StreamCodec)ByteBufCodecs.INT, ReadonlyStamina::max, ReadonlyStamina::new);

    public static ReadonlyStamina createDefault() {
        return new ReadonlyStamina(false, 0, 2000);
    }

    public ReadonlyStamina consumed(int value) {
        if (this.isExhausted()) {
            return this;
        }
        int newValue = this.value() - value;
        boolean exhausted = false;
        if (newValue < 0) {
            newValue = 0;
            exhausted = true;
        }
        return new ReadonlyStamina(exhausted, newValue, this.max());
    }

    public ReadonlyStamina recovered(int value) {
        int newValue = this.value() + value;
        boolean exhausted = this.isExhausted();
        if (newValue > this.max) {
            newValue = this.max;
            exhausted = false;
        }
        return new ReadonlyStamina(exhausted, newValue, this.max());
    }

    @OnlyIn(value=Dist.CLIENT)
    public ReadonlyStamina updateMax(LocalPlayer player) {
        AttributeInstance attr = player.getAttribute(Attributes.MAX_STAMINA);
        if (attr == null) {
            return this;
        }
        Parkourability parkourability = Parkourability.get((Player)player);
        if (parkourability == null) {
            return this;
        }
        int newMax = (int)Math.min(Math.floor(attr.getValue()), (double)parkourability.getActionInfo().getMaxStaminaLimit());
        if (this.max() == newMax) {
            return this;
        }
        return new ReadonlyStamina(this.isExhausted(), this.value(), newMax);
    }

    @OnlyIn(value=Dist.CLIENT)
    public void sync(LocalPlayer player) {
        PacketDistributor.sendToServer((CustomPacketPayload)new StaminaPayload(player.getUUID(), this), (CustomPacketPayload[])new CustomPacketPayload[0]);
    }
}

