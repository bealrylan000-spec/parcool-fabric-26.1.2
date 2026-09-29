package com.alrex.parcool.common.item.component;

import com.alrex.parcool.common.zipline.ZiplineType;
import com.mojang.datafixers.kinds.App;
import com.mojang.datafixers.kinds.Applicative;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record ZiplineTensionComponent(ZiplineType type) {
    public static final Codec<ZiplineTensionComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group((App)Codec.intRange((int)0, (int)(ZiplineType.values().length - 1)).fieldOf("type").forGetter(ZiplineTensionComponent::getIndex)).apply((Applicative)instance, ZiplineTensionComponent::fromIndex));
    public static final StreamCodec<ByteBuf, ZiplineTensionComponent> STREAM_CODEC = StreamCodec.composite((StreamCodec)ByteBufCodecs.INT, ZiplineTensionComponent::getIndex, ZiplineTensionComponent::fromIndex);

    private int getIndex() {
        return this.type().ordinal();
    }

    private static ZiplineTensionComponent fromIndex(int index) {
        if (index < 0 || index >= ZiplineType.values().length) {
            return new ZiplineTensionComponent(ZiplineType.STANDARD);
        }
        return new ZiplineTensionComponent(ZiplineType.values()[index]);
    }
}

