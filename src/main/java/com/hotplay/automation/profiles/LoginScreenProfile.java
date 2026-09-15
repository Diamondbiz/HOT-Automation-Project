package com.hotplay.automation.profiles;

public final class LoginScreenProfile {
    private LoginScreenProfile() {}

    private static final String PKG = "com.applicaster.il.hotvod";

    public static ScreenProfile get() {
        return new ScreenProfile.Builder(
                "LoginScreen",
                ScreenMarker.Type.RESOURCE_ID, PKG + ":id/connect_btn")
                .element("X close icon",  ScreenMarker.Type.RESOURCE_ID,
                        PKG + ":id/close_dialog_btn")
                .element("ID field",      ScreenMarker.Type.RESOURCE_ID,
                        PKG + ":id/account_edittext")
                .element("Phone field",   ScreenMarker.Type.RESOURCE_ID,
                        PKG + ":id/pin_edittext")
                .element("Terms checkbox", ScreenMarker.Type.RESOURCE_ID,
                        PKG + ":id/approve_terms_checkbox")
                .element("Connect button", ScreenMarker.Type.RESOURCE_ID,
                        PKG + ":id/connect_btn")
                .element("התחבר label",   ScreenMarker.Type.TEXT, "התחבר")
                .build();
    }
}