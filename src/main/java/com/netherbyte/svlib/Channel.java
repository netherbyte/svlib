package com.netherbyte.svlib;

public class Channel {
    public static final int UNDEFINED = -1;
    public static final int RELEASE = 0;
    public static final int ALPHA = 1;
    public static final int BETA = 2;
    public static final int PRE_RELEASE = 3;
    public static final int RELEASE_CANDIDATE = 4;
    public static final int DEVELOPMENT = 5;


    private int id;
    private String displayNames[] = {"release", "alpha", "beta", "pre", "rc", "dev"};

    public Channel(int id) {
        this.id = id;
    }

    public int get() {
        return id;
    }

    public String getDisplayName() {
        return displayNames[id];
    }
}
