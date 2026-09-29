package com.alrex.parcool.common.zipline;

import com.alrex.parcool.common.zipline.Zipline;
import com.alrex.parcool.common.zipline.impl.GeneralQuadraticCurveZipline;
import com.alrex.parcool.common.zipline.impl.StraightZipline;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;

public enum ZiplineType {
    STRAIGHT("parcool.gui.text.zipline.type.tight"),
    STANDARD("parcool.gui.text.zipline.type.normal"),
    LOOSE("parcool.gui.text.zipline.type.loose"),
    VERY_LOOSE("parcool.gui.text.zipline.type.very_loose");

    private final String translationID;

    private ZiplineType(String translation) {
        this.translationID = translation;
    }

    public Component getTranslationName() {
        return Component.translatable((String)this.translationID);
    }

    public Zipline getZipline(Vec3 point1, Vec3 point2) {
        if (this == STRAIGHT) {
            return new StraightZipline(point1, point2);
        }
        if (this == STANDARD) {
            if (Math.abs(point1.y() - point2.y()) < 1.0E-4) {
                return new StraightZipline(point1, point2);
            }
            return new GeneralQuadraticCurveZipline(point1, point2, 0.0);
        }
        if (this == LOOSE) {
            return new GeneralQuadraticCurveZipline(point1, point2, 0.35 + 0.035 * point2.distanceTo(point1));
        }
        if (this == VERY_LOOSE) {
            return new GeneralQuadraticCurveZipline(point1, point2, 0.6 + 0.06 * point2.distanceTo(point1));
        }
        return new StraightZipline(point1, point2);
    }
}

