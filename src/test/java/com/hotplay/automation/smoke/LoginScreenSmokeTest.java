package com.hotplay.automation.smoke;

import com.hotplay.automation.config.TestConfig;
import com.hotplay.automation.core.DeviceController;
import com.hotplay.automation.core.UiNode;
import com.hotplay.automation.core.XmlParser;
import com.hotplay.automation.profiles.LoginScreenProfile;
import com.hotplay.automation.profiles.ScreenProfile;
import com.hotplay.automation.validators.ScreenAssertionResult;
import com.hotplay.automation.validators.ScreenValidator;

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

        System.out.println("→ Waiting for foreground (up to "
                + TestConfig.APP_LAUNCH_WAIT_MS + "ms)");
        boolean foreground = device.waitForForeground(
                TestConfig.PACKAGE, TestConfig.APP_LAUNCH_WAIT_MS);
        if (!foreground) {
            System.err.println("FAIL: expected " + TestConfig.PACKAGE
                    + " but got " + device.currentFocus());
            System.exit(1);
        }

        Thread.sleep(TestConfig.UI_SETTLE_WAIT_MS);

        System.out.println("→ Dumping UI hierarchy");
        String xml = device.dumpUi();
        Files.createDirectories(TestConfig.XML_DIR);
        Path xmlPath = TestConfig.XML_DIR.resolve("login-smoke.xml");
        Files.writeString(xmlPath, xml);
        System.out.println("→ XML saved: " + xmlPath);

        Path png = TestConfig.SCREENS_DIR.resolve("login-smoke.png");
        device.screenshot(png);
        System.out.println("→ PNG saved: " + png);

        List<UiNode> nodes = XmlParser.parse(xml);

        ScreenProfile profile = LoginScreenProfile.get();
        ScreenAssertionResult result = ScreenValidator.validate(profile, nodes);
        result.printReport();

        if (!result.passed()) {
            System.exit(1);
        }
    }
}