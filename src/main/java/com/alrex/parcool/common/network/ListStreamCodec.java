package com.alrex.parcool.common.network;

import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.network.codec.StreamCodec;

public class ListStreamCodec<B extends ByteBuf, R>
implements StreamCodec<B, List<R>> {
    private final StreamCodec<B, R> CODEC;

    public ListStreamCodec(StreamCodec<B, R> codec) {
        this.CODEC = codec;
    }

    @Nonnull
    public List<R> decode(B b) {
        int count = b.readInt();
        ArrayList<Object> list = new ArrayList<Object>();
        for (int i = 0; i < count; ++i) {
            list.addLast(this.CODEC.decode(b));
        }
        return list;
    }

    public void encode(B b, List<R> rs) {
        b.writeInt(rs.size());
        for (R elem : rs) {
            this.CODEC.encode(b, elem);
        }
    }
}

