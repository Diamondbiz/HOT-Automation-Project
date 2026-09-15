package com.hotplay.automation.profiles;

public final class OtpScreenProfile {
    private OtpScreenProfile() {}

    private static final String PKG = "com.applicaster.il.hotvod";

    public static ScreenProfile get() {
        return new ScreenProfile.Builder(
                "OtpScreen",
                ScreenMarker.Type.RESOURCE_ID, PKG + ":id/token_edittext")
                .element("Subtitle",       ScreenMarker.Type.RESOURCE_ID,
                        PKG + ":id/subtitle_textview")
                .element("Instruction text", ScreenMarker.Type.TEXT,
                        "אנא הזן את קוד האימות שנשלח ב-SMS")
                .element("OTP field",     ScreenMarker.Type.RESOURCE_ID,
                        PKG + ":id/token_edittext")
                .element("שלח button",    ScreenMarker.Type.RESOURCE_ID,
                        PKG + ":id/confirm_button")
                .element("שלח label",     ScreenMarker.Type.TEXT, "שלח")
                .element("שלח קוד שנית",  ScreenMarker.Type.RESOURCE_ID,
                        PKG + ":id/resend_token_textview")
                .build();
    }
}