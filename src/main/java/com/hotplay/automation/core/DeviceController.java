package com.hotplay.automation.core;

import com.hotplay.automation.config.TestConfig;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * All ADB I/O goes through here. No other class should call ProcessBuilder directly.
 */
public class DeviceController {

    // ---------- Public API ----------

    public void launchApp() {
        shell("am", "start", "-n",
                TestConfig.PACKAGE + "/" + TestConfig.ACTIVITY);
    }

    public void lockOrientation(int orientation) {
        shell("settings", "put", "system", "accelerometer_rotation", "0");
        shell("settings", "put", "system", "user_rotation", String.valueOf(orientation));
    }

    /** Returns the raw uiautomator XML as a String. */
    public String dumpUi() {
        shell("uiautomator", "dump", "/sdcard/ui.xml");
        return shell("exec-out", "cat", "/sdcard/ui.xml");
    }

    /** Writes a PNG of the current screen to the given path, overwriting any existing file. */
    public void screenshot(Path out) throws IOException {
        Files.createDirectories(out.getParent());
        List<String> cmd = List.of("adb", "-s", TestConfig.DEVICE_UDID,
                "exec-out", "screencap", "-p");
        Process p = new ProcessBuilder(cmd).start();
        try (InputStream in = p.getInputStream()) {
            Files.copy(in, out, StandardCopyOption.REPLACE_EXISTING);
        }
        waitFor(p);
    }

    /** Returns e.g. "com.applicaster.il.hotvod" (the foreground package), or "" if unknown. */
    public String currentFocus() {
        String out = shell("shell", "dumpsys", "window");
        for (String line : out.split("\n")) {
            int idx = line.indexOf("mCurrentFocus=");
            if (idx >= 0) {
                String tail = line.substring(idx);
                int slash = tail.indexOf('/');
                if (slash > 0) {
                    String pkgPart = tail.substring(0, slash);
                    int space = pkgPart.lastIndexOf(' ');
                    return pkgPart.substring(space + 1).trim();
                }
            }
        }
        return "";
    }

    /**
     * Polls until the foreground package matches the expected one, or times out.
     * Returns true if the expected package appeared, false on timeout.
     */
    public boolean waitForForeground(String expectedPackage, long timeoutMs) {
        long deadline = System.currentTimeMillis() + timeoutMs;
        while (System.currentTimeMillis() < deadline) {
            String focus = currentFocus();
            if (expectedPackage.equals(focus)) return true;
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        return false;
    }

    /** Exposes shell() for ad-hoc diagnostics (e.g. logcat). */
    public String rawShell(String... args) {
        return shell(args);
    }

    // ---------- Internals ----------

    private String shell(String... args) {
        List<String> cmd = new ArrayList<>();
        cmd.add("adb");
        cmd.add("-s");
        cmd.add(TestConfig.DEVICE_UDID);
        for (String a : args) cmd.add(a);

        try {
            ProcessBuilder pb = new ProcessBuilder(cmd);
            pb.redirectErrorStream(true);
            Process p = pb.start();

            StringBuilder sb = new StringBuilder();
            try (BufferedReader r = new BufferedReader(
                    new InputStreamReader(p.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = r.readLine()) != null) sb.append(line).append('\n');
            }
            waitFor(p);
            return sb.toString();
        } catch (IOException e) {
            throw new RuntimeException("ADB command failed: " + String.join(" ", cmd), e);
        }
    }

    private void waitFor(Process p) {
        try {
            if (!p.waitFor(TestConfig.ADB_COMMAND_TIMEOUT, TimeUnit.MILLISECONDS)) {
                p.destroyForcibly();
                throw new RuntimeException("ADB command timed out");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }
}