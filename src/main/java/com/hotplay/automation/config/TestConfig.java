package com.hotplay.automation.config;

public final class TestConfig {
    private TestConfig() {}

    // --- Device ---
    public static final String DEVICE_UDID = "192.168.1.167:5555";

    // --- App (real values from your dumps) ---
    public static final String HOT_PACKAGE  = "com.applicaster.il.hotvod";
    public static final String HOT_ACTIVITY =
            "il.net.hot.sharedvod.ui.activities.SplashActivity";

    // --- Test data ---
    public static final String ID_NUMBER    = "999286578";
    public static final String PHONE_NUMBER = "0543501323";
    public static final String OTP_CODE     = "123456";

    // --- Paths ---
    public static final String PROJECT_ROOT  = "/Users/Johnny/IdeaProjects/POC";
    public static final String SCREENS_DIR   = PROJECT_ROOT + "/Screens";
    public static final String EXPECTED_DIR  = SCREENS_DIR + "/Expected";
    public static final String CURRENT_DIR   = SCREENS_DIR + "/Current screen";
    public static final String FAIL_DIR      = SCREENS_DIR + "/Fail";
    public static final String LOGS_DIR      = PROJECT_ROOT + "/logs";
    public static final String XML_DIR       = PROJECT_ROOT + "/xml";

    // --- Timeouts (ms) ---
    public static final long APP_LOAD_TIMEOUT    = 30_000;
    public static final long SCREEN_WAIT_TIMEOUT = 15_000;
    public static final long POLL_INTERVAL       = 500;

    // --- Image comparison ---
    public static final double SIMILARITY_THRESHOLD = 95.0;
    public static final int    PIXEL_TOLERANCE      = 10;
}