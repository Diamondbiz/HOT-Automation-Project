package com.hotplay.automation.smoke;

import com.hotplay.automation.config.TestConfig;
import com.hotplay.automation.core.DeviceController;
import com.hotplay.automation.core.UiNode;
import com.hotplay.automation.core.XmlParser;
import com.hotplay.automation.profiles.LoginScreenProfile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class LoginScreenSmokeTest {

    public static void main(String[] args) throws Exception {
        DeviceController device = new DeviceController();

        System.out.println("→ Device: " + TestConfig.DEVICE_MODEL + " / " + TestConfig.ANDROID_VER);

        System.out.println("→ Locking orientation to portrait");
        device.lockOrientation(TestConfig.PORTRAIT);

        System.out.println("→ Launching " + TestConfig.PACKAGE);
        device.launchApp();

        System.out.println("→ Waiting for foreground: " + TestConfig.PACKAGE
                + " (up to " + TestConfig.APP_LAUNCH_WAIT_MS + "ms)");
        boolean foreground = device.waitForForeground(
                TestConfig.PACKAGE, TestConfig.APP_LAUNCH_WAIT_MS);

        String focus = device.currentFocus();
        System.out.println("→ Final foreground: " + focus);

        if (!foreground) {
            System.err.println("FAIL: expected " + TestConfig.PACKAGE
                    + " but got " + focus);
            dumpDiagnostics(device);
            System.exit(1);
        }

        // Give the app a moment to finish rendering after focus arrives.
        Thread.sleep(TestConfig.UI_SETTLE_WAIT_MS);

        System.out.println("→ Dumping UI hierarchy");
        String xml = device.dumpUi();
        Files.createDirectories(TestConfig.XML_DIR);
        Path xmlPath = TestConfig.XML_DIR.resolve("login-smoke.xml");
        Files.writeString(xmlPath, xml);
        System.out.println("→ XML saved: " + xmlPath);

        System.out.println("→ Saving screenshot");
        Path png = TestConfig.SCREENS_DIR.resolve("login-smoke.png");
        device.screenshot(png);
        System.out.println("→ PNG saved: " + png);

        List<UiNode> nodes = XmlParser.parse(xml);
        System.out.println("→ Parsed " + nodes.size() + " nodes");

        boolean allFound = true;
        for (String id : LoginScreenProfile.REQUIRED_IDS) {
            boolean found = nodes.stream().anyMatch(n -> id.equals(n.resourceId));
            System.out.printf("   %s %s%n", found ? "✅" : "❌", id);
            if (!found) allFound = false;
        }

        System.out.println();
        if (allFound) {
            System.out.println("✅ SMOKE TEST PASSED — " + LoginScreenProfile.NAME);
        } else {
            System.out.println("❌ SMOKE TEST FAILED — missing markers above");
            System.exit(1);
        }
    }

    /**
     * If the app never reached the foreground, print anything in logcat that
     * mentions the package or a crash, so we can see why.
     */
    private static void dumpDiagnostics(DeviceController device) {
        System.out.println();
        System.out.println("--- Diagnostics (filtered logcat, last 300 lines) ---");
        String logs = device.rawShell("logcat", "-d", "-t", "300");
        for (String line : logs.split("\n")) {
            String lower = line.toLowerCase();
            if (lower.contains("hotvod")
                    || lower.contains("applicaster")
                    || lower.contains("fatal")
                    || lower.contains("androidruntime")) {
                System.out.println("   [log] " + line);
            }
        }
        System.out.println("--- End diagnostics ---");
    }
}