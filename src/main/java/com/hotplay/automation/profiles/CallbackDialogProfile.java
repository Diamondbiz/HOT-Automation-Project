package com.hotplay.automation.profiles;

/**
 * The generic "callback dialog" used by HOT Play for post-login messages
 * (legal notice, subscription warnings, etc.).
 *
 * Structure captured from finalapprove.xml:
 *   - close X       : callback_dialog_close_btn
 *   - scroll area   : callback_dialog_scroll_view
 *   - message       : callback_dialog_message_tv
 *   - action button : callback_dialog_action_button  (single CTA, text varies)
 *
 * The button text differs per message ("אישור", "המשך", "סגור"...), so the
 * profile does NOT assert on it — the flow reads it dynamically.
 */
public final class CallbackDialogProfile {
    private CallbackDialogProfile() {}

    private static final String PKG = "com.applicaster.il.hotvod";

    public static final String ID_DIALOG       = PKG + ":id/callback_dialog_scroll_view";
    public static final String ID_CLOSE_BTN    = PKG + ":id/callback_dialog_close_btn";
    public static final String ID_MESSAGE      = PKG + ":id/callback_dialog_message_tv";
    public static final String ID_ACTION_BTN   = PKG + ":id/callback_dialog_action_button";

    /** The message prefix; substring that identifies the legal-notice variant. */
    public static final String MSG_PREFIX_LEGAL = "לקוח יקר";

    /** Expected action label on the legal notice. */
    public static final String ACTION_APPROVE = "אישור";

    public static ScreenProfile get() {
        return new ScreenProfile.Builder(
                "CallbackDialog",
                ScreenMarker.Type.RESOURCE_ID, ID_DIALOG)
                .element("Close X",     ScreenMarker.Type.RESOURCE_ID, ID_CLOSE_BTN)
                .element("Scroll area", ScreenMarker.Type.RESOURCE_ID, ID_DIALOG)
                .element("Message",     ScreenMarker.Type.RESOURCE_ID, ID_MESSAGE)
                .element("Action button", ScreenMarker.Type.RESOURCE_ID, ID_ACTION_BTN)
                .element("Action label אישור",
                        ScreenMarker.Type.TEXT_PRESENT_ANYWHERE, ACTION_APPROVE)
                .build();
    }
}