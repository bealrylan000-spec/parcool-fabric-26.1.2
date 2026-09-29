package com.alrex.parcool.common.item.component;

import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record ZiplineColorComponent(int color) {
    public static final Codec<ZiplineColorComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.INT.fieldOf("color").forGetter(ZiplineColorComponent::color)).apply((Applicative)instance, ZiplineColorComponent::new));
    public static final StreamCodec<ByteBuf, ZiplineColorComponent> STREAM_CODEC = StreamCodec.composite((StreamCodec)ByteBufCodecs.INT, ZiplineColorComponent::color, ZiplineColorComponent::new);
}

