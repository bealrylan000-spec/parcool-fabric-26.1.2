package com.alrex.parcool.utilities;

import java.nio.ByteBuffer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.Vec3;

public class BufferUtil {
    ByteBuffer buffer;

    private BufferUtil(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    public static BufferUtil wrap(ByteBuffer byteBuffer) {
        return new BufferUtil(byteBuffer);
    }

    public BufferUtil putBoolean(boolean bool) {
        this.buffer.put(bool ? (byte)1 : 0);
        return this;
    }

    public static boolean getBoolean(ByteBuffer buffer) {
        return buffer.get() != 0;
    }

    public BufferUtil putBlockPos(BlockPos pos) {
        return this.putVector3i((Vec3i)pos);
    }

    public BufferUtil putVector3i(Vec3i vec) {
        this.buffer.putInt(vec.getX()).putInt(vec.getY()).putInt(vec.getZ());
        return this;
    }

    public BufferUtil putVec3(Vec3 vec) {
        this.buffer.putDouble(vec.x()).putDouble(vec.y()).putDouble(vec.z());
        return this;
    }

    public static BlockPos getBlockPos(ByteBuffer buffer) {
        return new BlockPos(buffer.getInt(), buffer.getInt(), buffer.getInt());
    }

    public static Vec3i getVector3i(ByteBuffer buffer) {
        return new Vec3i(buffer.getInt(), buffer.getInt(), buffer.getInt());
    }

    public static Vec3 getVec3(ByteBuffer buffer) {
        return new Vec3(buffer.getDouble(), buffer.getDouble(), buffer.getDouble());
    }

    public ByteBuffer unwrap() {
        return this.buffer;
    }

    public static boolean haveSameContents(ByteBuffer buffer1, ByteBuffer buffer2) {
        if (buffer1.limit() != buffer2.limit()) {
            return false;
        }
        while (buffer1.hasRemaining() && buffer2.hasRemaining()) {
            if (buffer1.get() == buffer2.get()) continue;
            return false;
        }
        return true;
    }
}

