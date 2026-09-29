package com.alrex.parcool.common.block.zipline;

import com.alrex.parcool.common.zipline.ZiplineType;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

public class ZiplineInfo {
    private final ZiplineType type;
    private final int color;

    public ZiplineInfo(ZiplineType type, int color) {
        this.color = color;
        this.type = type;
    }

    public int getColor() {
        return this.color;
    }

    public ZiplineType getType() {
        return this.type;
    }

    public Tag save() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("color", this.color);
        tag.putByte("type", (byte)this.getType().ordinal());
        return tag;
    }

    public static ZiplineInfo load(@Nullable Tag tag) {
        if (tag instanceof CompoundTag) {
            CompoundTag cTag = (CompoundTag)tag;
            int color = cTag.contains("color") ? cTag.getInt("color") : -11763738;
            ZiplineType type = cTag.contains("type") ? ZiplineType.values()[cTag.getByte("type") % ZiplineType.values().length] : ZiplineType.LOOSE;
            return new ZiplineInfo(type, color);
        }
        return new ZiplineInfo(ZiplineType.LOOSE, -11763738);
    }
}

