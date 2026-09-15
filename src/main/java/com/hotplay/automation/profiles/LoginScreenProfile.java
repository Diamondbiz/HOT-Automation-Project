package com.hotplay.automation.profiles;

public final class LoginScreenProfile {

    private LoginScreenProfile() {}

    private static final String PKG = "com.applicaster.il.hotvod:id/";

    public static ScreenProfile get() {
        return ScreenProfile.builder("LoginScreen")
                .marker("accountField",   PKG + "account_edittext")
                .marker("pinField",       PKG + "pin_edittext")
                .marker("connectButton",  PKG + "connect_btn")
                .marker("termsCheckbox",  PKG + "approve_terms_checkbox")
                .build();
    }
}