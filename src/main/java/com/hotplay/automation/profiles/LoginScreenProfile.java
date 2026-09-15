package com.hotplay.automation.profiles;

import java.util.List;

/**
 * Markers (resource-ids) that uniquely identify the HOT Play login screen.
 * Pure data — no logic, no ADB calls.
 */
public final class LoginScreenProfile {

    private LoginScreenProfile() {}

    public static final String NAME = "LoginScreen";

    public static final List<String> REQUIRED_IDS = List.of(
            "com.applicaster.il.hotvod:id/account_edittext",
            "com.applicaster.il.hotvod:id/pin_edittext",
            "com.applicaster.il.hotvod:id/connect_btn",
            "com.applicaster.il.hotvod:id/approve_terms_checkbox"
    );
}