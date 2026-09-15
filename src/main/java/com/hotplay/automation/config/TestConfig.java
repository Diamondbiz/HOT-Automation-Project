package com.hotplay.automation.config;

import java.nio.file.Path;
import java.nio.file.Paths;

public final class TestConfig {

    private TestConfig() {}

    // ---- Device ----
    public static final String DEVICE_UDID   = "RFGL30LRKQY";
    public static final String DEVICE_MODEL  = "Samsung Galaxy S26";
    public static final String ANDROID_VER   = "Android 16";

    // ---- App under test ----
    public static final String PACKAGE  = "com.applicaster.il.hotvod";
    public static final String ACTIVITY = "il.net.hot.sharedvod.ui.activities.SplashActivity";

    // ---- Orientation ----
    public static final int PORTRAIT  = 0;
    public static final int LANDSCAPE = 1;

    // ---- Timeouts (ms) ----
    public static final long APP_LAUNCH_WAIT_MS  = 15_000;
    public static final long UI_SETTLE_WAIT_MS   = 1_500;
    public static final long ADB_COMMAND_TIMEOUT = 30_000;

    // ---- Project paths ----
    public static final Path PROJECT_ROOT = Paths.get(
            System.getProperty("user.home"),
            "IdeaProjects", "POC-Mobile-Android");
    public static final Path XML_DIR      = PROJECT_ROOT.resolve("xml");
    public static final Path SCREENS_DIR  = PROJECT_ROOT.resolve("screens");
    public static final Path LOGS_DIR     = PROJECT_ROOT.resolve("logs");
}