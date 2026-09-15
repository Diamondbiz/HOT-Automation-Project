package com.hotplay.automation.core;

import com.hotplay.automation.config.TestConfig;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

public class DeviceController {

    private final String udid;

    public DeviceController(String udid) { this.udid = udid; }
    public DeviceController() { this(TestConfig.DEVICE_UDID); }

    // ---- process plumbing (unchanged) ----

    private String sh(String... args) {
        try {
            String[] cmd = new String[args.length + 3];
            cmd[0] = "adb"; cmd[1] = "-s"; cmd[2] = udid;
            System.arraycopy(args, 0, cmd, 3, args.length);
            Process p = new ProcessBuilder(cmd).redirectErrorStream(true).start();
            String out = read(p.getInputStream());
            p.waitFor(20, TimeUnit.SECONDS);
            return out.trim();
        } catch (Exception e) {
            throw new RuntimeException("adb " + String.join(" ", args), e);
        }
    }

    private void shNoOut(String... args) { sh(args); }

    private static String read(InputStream in) throws IOException {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        byte[] buf = new byte[4096]; int n;
        while ((n = in.read(buf)) != -1) b.write(buf, 0, n);
        return b.toString(StandardCharsets.UTF_8);
    }

    // ---- connection ----

    public boolean isConnected() {
        try { return sh("get-state").contains("device"); }
        catch (Exception e) { return false; }
    }

    public void connect() {
        try { new ProcessBuilder("adb", "connect", udid)
                .redirectErrorStream(true).start().waitFor(); }
        catch (Exception e) { throw new RuntimeException("adb connect failed", e); }
    }

    // ---- app lifecycle ----

    public void clearAppData(String pkg)  { shNoOut("shell", "pm", "clear", pkg); }
    public void forceStopApp(String pkg)  { shNoOut("shell", "am", "force-stop", pkg); }
    public void launchApp(String pkg, String activity) {
        shNoOut("shell", "am", "start", "-n", pkg + "/" + activity);
    }

    // ---- UI hierarchy — NOW returns UiNode ----

    /** Dump the UI and parse it into a UiNode tree. */
    public UiNode dumpUi() {
        return XmlParser.parse(dumpUiXml());
    }

    /** Raw XML string (kept for logging / archival). */
    public String dumpUiXml() {
        shNoOut("shell", "uiautomator", "dump", "/sdcard/window_dump.xml");
        return sh("shell", "cat", "/sdcard/window_dump.xml");
    }

    public String dumpUiXmlToFile(String tag) {
        String xml = dumpUiXml();
        try {
            new File(TestConfig.XML_DIR).mkdirs();
            File f = new File(TestConfig.XML_DIR, tag + "_" + System.currentTimeMillis() + ".xml");
            try (Writer w = new OutputStreamWriter(new FileOutputStream(f), StandardCharsets.UTF_8)) {
                w.write(xml);
            }
            return f.getAbsolutePath();
        } catch (IOException e) { return null; }
    }

    // ---- screenshots ----

    public void screenshot(String localPath) {
        String remote = "/sdcard/screen.png";
        shNoOut("shell", "screencap", "-p", remote);
        new File(localPath).getParentFile().mkdirs();
        shNoOut("pull", remote, localPath);
        shNoOut("shell", "rm", remote);
    }

    // ---- input ----

    public void tap(int x, int y)  { shNoOut("shell", "input", "tap", String.valueOf(x), String.valueOf(y)); }
    public void tap(UiNode node)   { tap(node.centerX(), node.centerY()); }
    public void key(int keyCode)   { shNoOut("shell", "input", "keyevent", String.valueOf(keyCode)); }
    public void text(String s)     { shNoOut("shell", "input", "text", s.replace(" ", "%s")); }
}