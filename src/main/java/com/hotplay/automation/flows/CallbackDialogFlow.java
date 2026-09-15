package com.hotplay.automation.flows;

import com.hotplay.automation.config.TestConfig;
import com.hotplay.automation.core.DeviceController;
import com.hotplay.automation.core.KeyCodes;
import com.hotplay.automation.core.UiNode;
import com.hotplay.automation.profiles.CallbackDialogProfile;
import com.hotplay.automation.validators.ScreenAssertionResult;
import com.hotplay.automation.validators.ScreenValidator;

/**
 * Handles the generic HOT "callback dialog" (legal notice, notices, etc.).
 *
 * Behaviour:
 *   - Polls for the dialog for a short window.
 *   - If it never appears, returns false silently.
 *   - If it appears, asserts its structure, prints the message, then presses
 *     the action button (אישור / המשך / etc. — read dynamically).
 *   - Waits for the dialog to dismiss before returning.
 */
public class CallbackDialogFlow {

    private static final long APPEAR_TIMEOUT_MS    = 6_000;
    private static final long DISMISS_TIMEOUT_MS   = 6_000;

    private final DeviceController device;
    private final ScreenValidator  validator;

    public CallbackDialogFlow(DeviceController device, ScreenValidator validator) {
        this.device    = device;
        this.validator = validator;
    }

    public boolean handleIfPresent() {
        if (!waitForAppearance()) return false;

        System.out.println("--- Callback dialog detected ---");

        // ---- assert structure ----
        ScreenAssertionResult result = validator.validate(CallbackDialogProfile.get());
        System.out.println(result.summary());
        device.screenshot(TestConfig.CURRENT_DIR + "/callback_dialog.png");
        if (!result.passed()) {
            device.screenshot(TestConfig.FAIL_DIR + "/callback_dialog_fail.png");
            throw new AssertionError("Callback dialog assertion failed: "
                    + result.failures());
        }

        // ---- log the message (informative, not asserted) ----
        UiNode msg = device.dumpUi().findById(CallbackDialogProfile.ID_MESSAGE);
        if (msg != null) {
            System.out.println("  message: " + msg.text.replace("\n", " / "));
        }

        // ---- click action button ----
        UiNode action = device.dumpUi().findById(CallbackDialogProfile.ID_ACTION_BTN);
        if (action == null) {
            throw new AssertionError("Callback dialog action button disappeared");
        }
        System.out.println("  pressing action: " + action.text);
        device.tap(action);
        sleep(300);
        device.key(KeyCodes.DPAD_CENTER);   // TV focus-based fallback

        waitForDismissal();
        System.out.println("  ✔ callback dialog dismissed");
        return true;
    }

    // ------------------------------------------------------------------

    private boolean waitForAppearance() {
        long deadline = System.currentTimeMillis() + APPEAR_TIMEOUT_MS;
        while (System.currentTimeMillis() < deadline) {
            UiNode root = device.dumpUi();
            if (root.findById(CallbackDialogProfile.ID_DIALOG) != null
                    && root.findById(CallbackDialogProfile.ID_ACTION_BTN) != null) {
                return true;
            }
            sleep(300);
        }
        return false;
    }

    private void waitForDismissal() {
        long deadline = System.currentTimeMillis() + DISMISS_TIMEOUT_MS;
        while (System.currentTimeMillis() < deadline) {
            UiNode root = device.dumpUi();
            if (root.findById(CallbackDialogProfile.ID_DIALOG) == null) return;
            sleep(300);
        }
        // Not fatal — the mosaic wait will catch a real hang.
    }

    private static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
    }
}