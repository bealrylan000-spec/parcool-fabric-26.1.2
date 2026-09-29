package com.alrex.parcool.client.hud;

import net.minecraft.util.Tuple;

public class Position {
    public static final Position DEFAULT = new Position(Horizontal.Left, Vertical.Top, 0, 0);
    private final Horizontal alignmentHorizontal;
    private final Vertical alignmentVertical;
    private final int marginHorizontal;
    private final int marginVertical;

    public Position(Horizontal alignH, Vertical alignV, int marginH, int marginV) {
        this.alignmentHorizontal = alignH;
        this.alignmentVertical = alignV;
        this.marginHorizontal = marginH;
        this.marginVertical = marginV;
    }

    public Tuple<Integer, Integer> calculate(int width, int height, int screenWidth, int screenHeight) {
        int x = this.alignmentHorizontal == Horizontal.Right ? screenWidth - this.marginHorizontal - width : this.marginHorizontal;
        int y = this.alignmentVertical == Vertical.Bottom ? screenHeight - this.marginVertical - height : this.marginVertical;
        return new Tuple((Object)x, (Object)y);
    }

    public static enum Horizontal {
        Left,
        Right;

    }

    public static enum Vertical {
        Top,
        Bottom;

    }
}

