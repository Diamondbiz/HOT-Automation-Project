package com.hotplay.automation.core;

public final class KeyCodes {
    private KeyCodes() {}
    public static final int DPAD_UP = 19, DPAD_DOWN = 20,
            DPAD_LEFT = 21, DPAD_RIGHT = 22,
            DPAD_CENTER = 23, BACK = 4, ENTER = 66;

    /** Keyevent code for a decimal digit. */
    public static int forDigit(char c) {
        if (c < '0' || c > '9') throw new IllegalArgumentException("not a digit: " + c);
        return 7 + (c - '0');   // '0'→7, '1'→8, ..., '9'→16
    }
}