package com.hotplay.automation.profiles;

/**
 * The "בחר אתר" (choose site) dialog that appears after a successful OTP
 * when the account is provisioned on more than one site.
 *
 * Resource-ids were captured from choosemobileuser.xml.
 * Note: every row in the ListView shares the generic id "android:id/text1",
 * so the rows cannot be distinguished by id — we match on text instead.
 */
public final class SitePickerProfile {
    private SitePickerProfile() {}

    private static final String PKG = "com.applicaster.il.hotvod";

    // ---- resource-ids on the dialog ----
    public static final String ID_DIALOG_LIST  = PKG + ":id/accounts_dialog_list_view";
    public static final String ID_DIALOG_TITLE = PKG + ":id/accounts_picker_title_tv";
    public static final String ID_DISMISS_BTN  = PKG + ":id/accounts_picker_dismiss_btn";
    public static final String ID_ROW          = "android:id/text1";

    // ---- visible text ----
    public static final String TXT_TITLE = "בחר אתר";

    /**
     * Stable substring that appears in exactly one row.
     * Full row text is:
     *   "אולג QA בדיקה 2, יקום(הולנד) קומה ראשונה 4, יקום,  דירה1000"
     * The full string contains a double-space before "דירה" (an RTL shaping
     * artifact), so we match on the prefix only.
     */
    public static final String TARGET_ROW_SUBSTRING = "אולג QA";

    /** Profile consumed by ScreenValidator. */
    public static ScreenProfile get() {
        return new ScreenProfile.Builder(
                "SitePicker",
                ScreenMarker.Type.RESOURCE_ID, ID_DIALOG_LIST)
                .element("Dialog title",   ScreenMarker.Type.RESOURCE_ID, ID_DIALOG_TITLE)
                .element("Title text",     ScreenMarker.Type.TEXT,        TXT_TITLE)
                .element("Dismiss button", ScreenMarker.Type.RESOURCE_ID, ID_DISMISS_BTN)
                .element("Site list",      ScreenMarker.Type.RESOURCE_ID, ID_DIALOG_LIST)
                .element("Row: אולג QA",   ScreenMarker.Type.TEXT_PRESENT_ANYWHERE,
                        TARGET_ROW_SUBSTRING)
                .build();
    }
}