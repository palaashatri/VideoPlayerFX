package com.example.videoplayerfx.util;

public class PlatformUtils {
    public static String getLibVlcPath() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            return "libvlc/win";
        } else if (os.contains("mac")) {
            return "libvlc/macos";
        } else {
            return "libvlc/linux";
        }
    }

    public static String getPlatformStylesheet() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            return "/css/windows.css";
        } else if (os.contains("mac")) {
            return "/css/macos.css";
        } else {
            return "/css/linux.css";
        }
    }

    public static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }

    public static boolean isMac() {
        return System.getProperty("os.name").toLowerCase().contains("mac");
    }

    public static boolean isLinux() {
    return System.getProperty("os.name").toLowerCase().contains("nux");
}
}