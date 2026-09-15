package com.hotplay.automation.smoke;

import com.hotplay.automation.config.TestConfig;
import com.hotplay.automation.core.DeviceController;
import com.hotplay.automation.core.KeyCodes;
import com.hotplay.automation.core.UiNode;
import com.hotplay.automation.flows.CallbackDialogFlow;
import com.hotplay.automation.flows.SitePickerFlow;
import com.hotplay.automation.profiles.LiveMosaicProfile;
import com.hotplay.automation.profiles.LoginScreenProfile;
import com.hotplay.automation.profiles.OtpScreenProfile;
import com.hotplay.automation.validators.ScreenAssertionResult;
import com.hotplay.automation.validators.ScreenValidator;

import java.util.function.BooleanSupplier;

/**
 * End-to-end HOT login journey over ADB.
 *
 *   1. cold start
 *   2. assert Login screen
 *   3. fill ID + phone, tick terms, click התחבר
 *   4. wait for OTP, assert
 *   5. type 123456, click שלח
 *   6. handle site picker if shown       (SitePickerFlow — optional)
 *   7. handle callback dialog if shown   (CallbackDialogFlow — optional)
 *   8. wait for Live Mosaic, assert
 *
 * Run:
 *   cd /Users/Johnny/IdeaProjects/POC-Mobile-Android
 *   mvn clean test-compile exec:java \
 *       -Dexec.mainClass=com.hotplay.automation.smoke.FullLoginTest \
 *       -Dexec.classpathScope=test
 */
public class FullLoginTest {

    // ============= resource-ids from real device dumps =============

    private static final String PKG = TestConfig.HOT_PACKAGE;

    // Login screen
    private static final String ID_CLOSE_LOGIN = PKG + ":id/close_dialog_btn";
    private static final String ID_ID_FIELD    = PKG + ":id/account_edittext";
    private static final String ID_PHONE_FIELD = PKG + ":id/pin_edittext";
    private static final String ID_TERMS       = PKG + ":id/approve_terms_checkbox";
    private static final String ID_CONNECT     = PKG + ":id/connect_btn";

    // OTP screen
    private static final String ID_OTP_FIELD   = PKG + ":id/token_edittext";
    private static final String ID_SEND        = PKG + ":id/confirm_button";
    private static final String TXT_OTP_SUB    = "אנא הזן את קוד האימות שנשלח ב-SMS";

    // Live Mosaic
    private static final String ID_MOSAIC_GRID = PKG + ":id/all_channels_fragment_recycler_view";
    private static final String TXT_TOP_ALL    = "הכל";

    // ================================================================

    public static void main(String[] args) {
        System.out.println("================ FullLoginTest ================");

        DeviceController device = new DeviceController(TestConfig.DEVICE_UDID);
        if (!device.isConnected()) device.connect();
        if (!device.isConnected()) {
            System.err.println("Device " + TestConfig.DEVICE_UDID + " unreachable");
            System.exit(1);
        }

        ScreenValidator validator = new ScreenValidator(device);

        try {
            // ---------- 1. COLD START ----------
            System.out.println("--- Cold start ---");
            device.clearAppData(TestConfig.HOT_PACKAGE);
            device.forceStopApp(TestConfig.HOT_PACKAGE);
            device.launchApp(TestConfig.HOT_PACKAGE, TestConfig.HOT_ACTIVITY);

            waitFor(() -> device.dumpUi().findById(ID_CONNECT) != null,
                    TestConfig.SCREEN_WAIT_TIMEOUT, "Login screen");

            // ---------- 2. ASSERT LOGIN ----------
            System.out.println("--- Assert Login screen ---");
            ScreenAssertionResult login = validator.validate(LoginScreenProfile.get());
            System.out.println(login.summary());
            if (!login.passed()) {
                device.screenshot(TestConfig.FAIL_DIR + "/login_fail.png");
                System.exit(2);
            }

            // ---------- 3. FILL LOGIN ----------
            System.out.println("--- Fill login form ---");
            typeInto(device, ID_ID_FIELD,    TestConfig.ID_NUMBER);
            typeInto(device, ID_PHONE_FIELD, TestConfig.PHONE_NUMBER);
            click(device, ID_TERMS, "terms checkbox");

            waitFor(() -> {
                UiNode n = device.dumpUi().findById(ID_CONNECT);
                return n != null && n.enabled;
            }, TestConfig.SCREEN_WAIT_TIMEOUT, "connect_btn enabled");

            click(device, ID_CONNECT, "התחבר");

            // ---------- 4. WAIT + ASSERT OTP ----------
            waitFor(() -> {
                UiNode root = device.dumpUi();
                return root.findById(ID_OTP_FIELD) != null
                        && root.findByText(TXT_OTP_SUB) != null;
            }, TestConfig.SCREEN_WAIT_TIMEOUT, "OTP screen");

            System.out.println("--- Assert OTP screen ---");
            ScreenAssertionResult otp = validator.validate(OtpScreenProfile.get());
            System.out.println(otp.summary());
            if (!otp.passed()) {
                device.screenshot(TestConfig.FAIL_DIR + "/otp_fail.png");
                System.exit(3);
            }

            // ---------- 5. FILL OTP ----------
            System.out.println("--- Fill OTP ---");
            click(device, ID_OTP_FIELD, "OTP field");
            sleep(300);
            sendDigits(device, TestConfig.OTP_CODE);

            waitFor(() -> {
                UiNode n = device.dumpUi().findById(ID_SEND);
                return n != null && n.enabled;
            }, TestConfig.SCREEN_WAIT_TIMEOUT, "confirm_button enabled");

            click(device, ID_SEND, "שלח");

            // ---------- 6. POST-OTP DIALOGS (either, both, or neither) ----------
            //
            // Two known post-OTP dialogs exist on HOT Play:
            //   a. Site picker      ("בחר אתר")         — multi-site accounts only
            //   b. Legal callback   ("לקוח יקר, ...")    — always shown on 1st login
            //
            // We try both, in either order — each flow returns false if its
            // dialog isn't there, so this is safe regardless of app version.
            //
            // Order matters when both can appear: try site picker first, because
            // in observed runs it appears before the legal notice. If your
            // device shows them the other way, swap the two lines below.
            new SitePickerFlow(device, validator).handleIfPresent();
            new CallbackDialogFlow(device, validator).handleIfPresent();

            // ---------- 7. WAIT + ASSERT LIVE MOSAIC ----------
            waitFor(() -> {
                UiNode root = device.dumpUi();
                return root.findById(ID_MOSAIC_GRID) != null
                        && root.findByText(TXT_TOP_ALL) != null;
            }, 30_000, "Live Mosaic");

            System.out.println("--- Assert Live Mosaic ---");
            ScreenAssertionResult mosaic = validator.validate(LiveMosaicProfile.get());
            System.out.println(mosaic.summary());
            device.screenshot(TestConfig.CURRENT_DIR + "/mosaic_pass.png");
            if (!mosaic.passed()) {
                device.screenshot(TestConfig.FAIL_DIR + "/mosaic_fail.png");
                System.exit(4);
            }

            System.out.println("================ FullLoginTest: PASS ================");
            System.exit(0);

        } catch (Throwable t) {
            t.printStackTrace();
            try { device.screenshot(TestConfig.FAIL_DIR + "/exception.png"); }
            catch (Throwable ignored) {}
            System.exit(99);
        }
    }

    // ============= Helpers =============

    private static void typeInto(DeviceController device, String resId, String value) {
        UiNode n = device.dumpUi().findById(resId);
        if (n == null) throw new IllegalStateException("Not found: " + resId);
        device.tap(n);
        sleep(300);
        device.text(value);
        System.out.println("  typed '" + value + "' into " + resId);
    }

    private static void click(DeviceController device, String resId, String human) {
        UiNode n = device.dumpUi().findById(resId);
        if (n == null) throw new IllegalStateException("Not found: "
                + human + " (" + resId + ")");
        device.tap(n);
        System.out.println("  clicked " + human);
    }

    private static void sendDigits(DeviceController device, String digits) {
        for (char c : digits.toCharArray()) {
            device.key(KeyCodes.forDigit(c));
            sleep(120);
        }
        System.out.println("  typed digits: " + digits);
    }

    private static void waitFor(BooleanSupplier cond, long timeoutMs, String what) {
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            try { if (cond.getAsBoolean()) { System.out.println("  ✔ " + what); return; } }
            catch (Exception ignored) {}
            sleep(TestConfig.POLL_INTERVAL);
        }
        throw new AssertionError(what + " not present within " + timeoutMs + "ms");
    }

    private static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException ignored) {}
    }
}