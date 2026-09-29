package com.alrex.parcool.client.gui;

public enum ColorTheme {
    Red(-1153433600, -1153425408, -870313968, -5608909, -5622989, -3355444, -8355712, -1118482, -5592406, -30584),
    Green(-1157611520, -1157611456, -871362528, -13391190, -13391309, -3355444, -8355712, -1118482, -5592406, -7798904),
    Blue(-1157627840, -1153433536, -871362528, -5622870, -13421654, -3355444, -8355712, -1118482, -5592406, -7829249),
    Yellow(-1153417216, -1155514304, -870309872, -11890381, -7237325, -3355444, -8355712, -1118482, -5592406, -120),
    Black(-1153417152, -1155522528, -871362544, -8947849, -11184811, -3355444, -8355712, -1118482, -5592406, -1);

    private final int header1;
    private final int header2;
    private final int background;
    private final int topBar1;
    private final int topBar2;
    private final int separator;
    private final int subSeparator;
    private final int text;
    private final int subText;
    private final int strongText;

    private ColorTheme(int header1, int header2, int background, int topBar1, int topBar2, int separator, int subSeparator, int text, int subText, int strongText) {
        this.header1 = header1;
        this.header2 = header2;
        this.background = background;
        this.topBar1 = topBar1;
        this.topBar2 = topBar2;
        this.separator = separator;
        this.subSeparator = subSeparator;
        this.text = text;
        this.subText = subText;
        this.strongText = strongText;
    }

    public int getHeader1() {
        return this.header1;
    }

    public int getHeader2() {
        return this.header2;
    }

    public int getBackground() {
        return this.background;
    }

    public int getSeparator() {
        return this.separator;
    }

    public int getSubSeparator() {
        return this.subSeparator;
    }

    public int getSubText() {
        return this.subText;
    }

    public int getText() {
        return this.text;
    }

    public int getTopBar1() {
        return this.topBar1;
    }

    public int getTopBar2() {
        return this.topBar2;
    }

    public int getStrongText() {
        return this.strongText;
    }
}

