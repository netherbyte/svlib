package com.netherbyte.svlib;

import com.netherbyte.nbl4j.Logger;
import com.netherbyte.nbl4j.LoggerManager;
import com.netherbyte.nbl4j.Loggers;

public class Version {
    private static final Logger logger = Loggers.get("SVLib");
    private final int major, minor, patch, build;
    private final String name;
    private final boolean custom;
    private final Channel channel;
    /**
     * 0=major
     * 1=major, minor
     * 2= major, minor, patch
     * 3=major, minor, patch, build
     * 4=major, minor, patch, channel
     * 5=major, minor, patch, channel, build
     * 6=name
     */
    private final int type;

    public Version(int major) {
        this.major = major;
        this.minor = 0;
        this.patch = 0;
        this.build = 0;
        this.name = null;
        this.custom = false;
        this.channel = null;
        this.type = 0;
    }

    public Version(int major, int minor) {
        this.major = major;
        this.minor = minor;
        this.patch = 0;
        this.build = 0;
        this.name = null;
        this.custom = false;
        this.channel = null;
        this.type = 1;
    }

    public Version(int major, int minor, int patch) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.build = 0;
        this.name = null;
        this.custom = false;
        this.channel = null;
        this.type = 2;
    }

    public Version(int major, int minor, int patch, int build) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.build = build;
        this.name = null;
        this.custom = false;
        this.channel = null;
        this.type = 3;
    }

    public Version(int major, int minor, int patch, Channel channel) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.build = 0;
        this.name = null;
        this.custom = false;
        this.channel = channel;
        this.type = 4;
    }

    public Version(int major, int minor, int patch, Channel channel, int build) {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
        this.build = build;
        this.name = null;
        this.custom = false;
        this.channel = channel;
        this.type = 5;
    }

    public Version(String name) {
        this.major = 0;
        this.minor = 0;
        this.patch = 0;
        this.build = 0;
        this.name = name;
        this.custom = true;
        this.channel = null;
        this.type = 6;
    }

    public int getMajor() {
        if (!custom) return major;
        else return -1;
    }

    public int getMinor() {
        if (!custom) return minor;
        else return -1;
    }

    public int getPatch() {
        if (!custom) return patch;
        else return -1;
    }

    public int getBuild() {
        if (!custom) return build;
        else return -1;
    }

    public Channel getChannel() {
        if (!custom) return channel;
        else return new Channel(Channel.UNDEFINED);
    }

    public boolean isCustom() {
        return custom;
    }

    public String getName() {
        if (custom) return name;
        else return null;
    }

    public String getFormattedName() {
        if (custom) return name;
        switch (type) {
            case 0 -> {
                return major + ".0.0";
            }
            case 1 -> {
                return major + "." + minor + ".0";
            }
            case 2 -> {
                return major + "." + minor + "." + patch;
            }
            case 3 -> {
                return major + "." + minor + "." + patch + "." + build;
            }
            case 4 -> {
                assert channel != null;
                return major + "." + minor + "." + patch + "-" + channel.getDisplayName();
            }
            case 5 -> {
                assert channel != null;
                return major + "." + minor + "." + patch + "-" + channel.getDisplayName() + "+" + build;
            }
            case 6 -> {
                logger.warn("This line of code is just redundant and not meant to be reached. Please report this to https://bugs.insert.website/here");
                return name;
            }
            default -> throw new IndexOutOfBoundsException("Type ranges are 0-6 but received " + type);
        }
    }


    /**
     * Parses a version from a string
     *
     * @param x String to parse
     * @return Version
     */
    public static Version parse(String x) {
        // i would like to use regex but i hate writing regex expressions
        // "let the intern do it" -sid
        // but there is no intern

        final String regex = "[0-9]?.?[0-9]?.?[0-9]?[-|[0-9]]?[alpha|beta|dev|pre|rc]?.?[0-9]";
        // dont know how to use this regex so i have to use the manual way

        // split into 2 parts
        final String[] a = x.split("-");
        if (a.length == 1) {
            // the version is something like 1.0.0.0
            final String[] b = a[0].split("\\.");
            switch (b.length) {
                case 1 -> {return new Version(Integer.parseInt(b[0]));}
                case 2 -> {return new Version(Integer.parseInt(b[0]), Integer.parseInt(b[1]));}
                case 3 -> {return new Version(Integer.parseInt(b[0]), Integer.parseInt(b[1]), Integer.parseInt(b[2]));}
                case 4 -> {return new Version(Integer.parseInt(b[0]), Integer.parseInt(b[1]), Integer.parseInt(b[2]), Integer.parseInt(b[3]));}

            }
        } else if (a.length == 2) {
            // the version is something like 1.0.0-alpha.1
            int b1 = -1;
            int b2 = -1;
            int b3 = -1;
            final String[] b = a[0].split("\\.");
            switch (b.length) {
                case 1 -> b1 = Integer.parseInt(b[0]);
                case 2 -> {
                    b1 = Integer.parseInt(b[0]);
                    b2 = Integer.parseInt(b[1]);
                }
                case 3 -> {
                    b1 = Integer.parseInt(b[0]);
                    b2 = Integer.parseInt(b[1]);
                    b3 = Integer.parseInt(b[2]);
                }
            }
            String c1 = null;
            int c2 = -1;
            final String[] c = a[1].split("\\.");
            switch (c.length) {
                case 1 -> {
                    c1 = c[0];
                    return switch (c1) {
                        case "alpha" -> new Version(b1, b2, b3, new Channel(Channel.ALPHA));
                        case "beta" -> new Version(b1, b2, b3, new Channel(Channel.BETA));
                        case "dev" -> new Version(b1, b2, b3, new Channel(Channel.DEVELOPMENT));
                        case "pre" -> new Version(b1, b2, b3, new Channel(Channel.PRE_RELEASE));
                        case "rc" -> new Version(b1, b2, b3, new Channel(Channel.RELEASE_CANDIDATE));
                        default -> new Version(b1, b2, b3, new Channel(Channel.UNDEFINED));
                    };
                }
                case 2 -> {
                    c1 = c[0];
                    c2 = Integer.parseInt(c[1]);
                    return switch (c1) {
                        case "alpha" -> new Version(b1, b2, b3, new Channel(Channel.ALPHA), c2);
                        case "beta" -> new Version(b1, b2, b3, new Channel(Channel.BETA), c2);
                        case "dev" -> new Version(b1, b2, b3, new Channel(Channel.DEVELOPMENT), c2);
                        case "pre" -> new Version(b1, b2, b3, new Channel(Channel.PRE_RELEASE), c2);
                        case "rc" -> new Version(b1, b2, b3, new Channel(Channel.RELEASE_CANDIDATE), c2);
                        default -> new Version(b1, b2, b3, new Channel(Channel.UNDEFINED), c2);
                    };
                }
            }
        } else {
            logger.error("Failed to parse version: " + x + "\r\nDoes it use SemVer 2.0.0?");
            throw new IllegalArgumentException();
        }
        return null;
    }


    public static class Math {
        public static boolean isCompatible(Version req, Version ver) {
            if (req.getMajor() <= ver.getMajor()) {
                if (req.getMinor() <= ver.getMinor()) {
                    if (req.getPatch() <= ver.getPatch()) {
                        return req.getBuild() <= ver.getBuild();
                    }
                }
            }
            return false;
        }
    }
}
