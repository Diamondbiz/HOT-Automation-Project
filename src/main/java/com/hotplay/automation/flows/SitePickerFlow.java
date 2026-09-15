package com.hotplay.automation.flows;

import com.hotplay.automation.config.TestConfig;
import com.hotplay.automation.core.DeviceController;
import com.hotplay.automation.core.KeyCodes;
import com.hotplay.automation.core.UiNode;
import com.hotplay.automation.profiles.SitePickerProfile;
import com.hotplay.automation.validators.ScreenAssertionResult;
import com.hotplay.automation.validators.ScreenValidator;

/**
 * Handles the "בחר אתר" site-picker dialog that may appear after OTP success.
 *
 * Design notes:
 *  - The dialog is optional: single-site accounts skip it, so we poll and
 *    return silently if it never shows up.
 *  - All rows share resource-id "android:id/text1", so we select by text
 *    substring ("אולג QA"), not by id.
 *  - After tapping the row we send DPAD_CENTER as a belt-and-braces fallback,
 *    because some Android TV ListViews only honour focus-based selection.
 */
public class SitePickerFlow {

    /** How long to wait for the dialog before deciding it won't appear. */
    private static final long APPEAR_TIMEOUT_MS = 8_000;

    private final DeviceController device;
    private final ScreenValidator  validator;

    public SitePickerFlow(DeviceController device, ScreenValidator validator) {
        this.device = device;
        this.validator = validator;
    }

    /**
     * Waits for the picker, asserts it, selects the target row, and waits for
     * the dialog to dismiss. Returns true if the picker was handled, false if
     * it never appeared (single-site account).
     */
    public boolean handleIfPresent() {
        if (!waitForAppearance()) {
            System.out.println("  (no site picker — single-site account)");
            return false;
        }

        System.out.println("--- Site picker detected ---");

        // ---- assert via the profile ----
        ScreenAssertionResult result = validator.validate(SitePickerProfile.get());
        System.out.println(result.summary());
        device.screenshot(TestConfig.CURRENT_DIR + "/site_picker.png");
        if (!result.passed()) {
            device.screenshot(TestConfig.FAIL_DIR + "/site_picker_fail.png");
            throw new AssertionError("Site picker assertion failed: "
                    + result.failures());
        }

        // ---- select the target row ----
        UiNode row = device.dumpUi()
                .findByTextContaining(SitePickerProfile.TARGET_ROW_SUBSTRING);
        if (row == null) {
            throw new AssertionError("Site picker row not found: "
                    + SitePickerProfile.TARGET_ROW_SUBSTRING);
        }
        System.out.println("  selecting row: " + row.text);
        device.tap(row);

        // Belt-and-braces: TV ListViews sometimes ignore tap() and only
        // respond to a DPAD_CENTER on the focused row.
        sleep(300);
        device.key(KeyCodes.DPAD_CENTER);

        // ---- wait for dialog to actually dismiss ----
        waitForDismissal();
        System.out.println("  ✔ site picker dismissed");
        return true;
    }

    // ------------------------------------------------------------------

    private boolean waitForAppearance() {
        long deadline = System.currentTimeMillis() + APPEAR_TIMEOUT_MS;
        while (System.currentTimeMillis() < deadline) {
            UiNode root = device.dumpUi();
            boolean hasList  = root.findById(SitePickerProfile.ID_DIALOG_LIST) != null;
            boolean hasTitle = root.findByText(SitePickerProfile.TXT_TITLE) != null;
            if (hasList && hasTitle) return true;
            sleep(400);
        }
        return false;
    }

    private void waitForDismissal() {
        long deadline = System.currentTimeMillis() + 8_000;
        while (System.currentTimeMillis() < deadline) {
            UiNode root = device.dumpUi();
            if (root.findById(SitePickerProfile.ID_DIALOG_LIST) == null) return;
            sleep(300);
        }
        // Not fatal — the mosaic wait in the test will catch a real hang.
    }

    private static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
    }
}