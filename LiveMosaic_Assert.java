package Tests;

import io.appium.java_client.android.AndroidDriver;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.*;

public class LiveMosaic_Assert {

    private AndroidDriver driver;
    private final List<String> detectedElements = new ArrayList<>();
    private final List<String> mismatchedElements = new ArrayList<>();
    private boolean allElementsDetected = true;
    private static final String SCREENSHOTS_DIR = "/Users/hanangoverman/IntelliJProjects/V5-AutomationTests/screenshots";
    private static final String ELEMENT_DUMPS_DIR = "/Users/hanangoverman/IntelliJProjects/V5-AutomationTests/element_dumps";
    private static final Duration DEFAULT_WAIT = Duration.ofSeconds(15);
    private static final String DEVICE_UDID = "192.168.1.165:5555";
    private static final String APP_PACKAGE = "il.net.hot.hot";
    private static final String APP_ACTIVITY = "il.net.hot.hot.TvMainActivity";

    @BeforeClass
    public void setUp() throws Exception {
        createDirectories();

        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("platformName", "Android");
        caps.setCapability("appium:deviceName", "HOTStreamerV4-b1d6");
        caps.setCapability("appium:udid", DEVICE_UDID);
        caps.setCapability("appium:platformVersion", "14");
        caps.setCapability("appium:automationName", "UiAutomator2");
        caps.setCapability("appium:noReset", true);
        caps.setCapability("appium:appPackage", APP_PACKAGE);
        caps.setCapability("appium:appActivity", APP_ACTIVITY);
        caps.setCapability("appium:autoGrantPermissions", true);
        caps.setCapability("appium:newCommandTimeout", 300);

        try {
            driver = new AndroidDriver(new java.net.URL("http://127.0.0.1:4723"), caps);
            System.out.println("Appium session started successfully");
        } catch (Exception e) {
            System.err.println("Failed to initialize Appium driver: " + e.getMessage());
            throw e;
        }
    }

    private void createDirectories() throws IOException {
        for (String dir : Arrays.asList(SCREENSHOTS_DIR, ELEMENT_DUMPS_DIR)) {
            Path path = Paths.get(dir);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
                if (!System.getProperty("os.name").toLowerCase().contains("win")) {
                    Files.setPosixFilePermissions(path, EnumSet.of(
                            PosixFilePermission.OWNER_READ,
                            PosixFilePermission.OWNER_WRITE,
                            PosixFilePermission.OWNER_EXECUTE));
                }
            }
        }
    }

    private String detectScreenNameFromViewHierarchy(String pageSource) {
        try {
            // Look for top-level container IDs that indicate the current screen
            if (pageSource.contains("mod_LiveMosaic")) {
                return "Live Mosaic";
            } else if (pageSource.contains("player_container")) {
                return "Video Player";
            } else if (pageSource.contains("main_menu")) {
                return "Main Menu";
            } else if (pageContentContainsAny(pageSource, 
                "settings_container", "settings_screen")) {
                return "Settings";
            } else if (pageSource.contains("mod_Search_")) {  
                return "Search";
            } else if (pageSource.contains("app_Live")) {
                return "Live TV";
            } else if (pageSource.contains("app_VOD")) {
                return "VOD Library";
            } else if (pageSource.contains("app_Catchup")) {
                return "Catch-up TV";
            } else if (pageSource.contains("app_Recordings")) {
                return "My Recordings";
            }
            
            // If no specific screen detected, try to get the first non-FrameLayout root view
            String[] rootViewIds = {
                "//*[contains(@resource-id, 'main')]",
                "//*[contains(@class, 'android.webkit.WebView')]",
                "//*[contains(@class, 'android.widget.FrameLayout')]/*[1]"
            };
            
            for (String xpath : rootViewIds) {
                try {
                    WebElement rootView = driver.findElement(By.xpath(xpath));
                    String resourceId = rootView.getAttribute("resource-id");
                    if (resourceId != null && !resourceId.isEmpty()) {
                        // Extract the meaningful part of the resource ID
                        String[] parts = resourceId.split("/");
                        if (parts.length > 1) {
                            return parts[1].replace("_", " ");
                        }
                        return resourceId;
                    }
                } catch (Exception e) {
                    // Ignore and try next selector
                }
            }
            
            return "Unknown Screen";
            
        } catch (Exception e) {
            System.err.println("Error detecting screen from view hierarchy: " + e.getMessage());
            return "Error: " + e.getMessage();
        }
    }
    
    private boolean pageContentContainsAny(String pageSource, String... patterns) {
        for (String pattern : patterns) {
            if (pageSource.contains(pattern)) {
                return true;
            }
        }
        return false;
    }
    
    private String printCurrentScreenName() {
        try {
            // Get the page source once
            String pageSource = driver.getPageSource();
            
            // Detect screen name from the page source
            String screenName = detectScreenNameFromViewHierarchy(pageSource);
            
            // If not found, fall back to activity name
            if ("Unknown Screen".equals(screenName)) {
                String currentActivity = driver.currentActivity();
                if (currentActivity != null) {
                    if (currentActivity.contains("TvMainActivity")) {
                        screenName = "Main TV Screen";
                    } else if (currentActivity.contains("LiveMosaic")) {
                        screenName = "Live Mosaic";
                    } else if (currentActivity.contains("Player")) {
                        screenName = "Video Player";
                    } else {
                        screenName = currentActivity;
                    }
                }
            }
            
            // Print the screen name in a clear format
            System.out.println("\n" + "=".repeat(80));
            System.out.println("CURRENT SCREEN OPEN: \"" + screenName.toUpperCase() + "\"");
            System.out.println("=" + " ".repeat(78) + "=\n");
            
            // Save the screen dump with the same page source
            saveScreenDump(screenName, pageSource);
            
            return screenName;
            
        } catch (Exception e) {
            System.err.println("Could not determine current screen: " + e.getMessage());
            return "Unknown Screen";
        }
    }

    private void saveScreenDump(String screenName, String pageSource) {
        try {
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS").format(new Date());
            String fileName = String.format("%s_%s.xml", screenName.replace(" ", "_"), timestamp);
            File outputFile = new File(ELEMENT_DUMPS_DIR, fileName);
            
            // Write to file
            try (FileWriter writer = new FileWriter(outputFile)) {
                writer.write(pageSource);
                System.out.println("Screen XML saved to: " + outputFile.getAbsolutePath());
            }
        } catch (Exception e) {
            System.err.println("Failed to save screen dump: " + e.getMessage());
        }
    }

    @Test
    public void runTest() {
        try {
            // First screen check and save
            String currentScreen = printCurrentScreenName();
            
            clickHOTIcon();
            
            // Second screen check and save after navigation
            currentScreen = printCurrentScreenName();
            
            checkCurrentScreen();
            detectSynopsis();
            detectChannelInfo();
            detectPiPWindow();
        } catch (Exception e) {
            System.err.println("Test failed with exception: " + e.getMessage());
            takeScreenshot("test_failure");
            throw e;
        }
    }

    private void clickHOTIcon() {
        System.out.println("Attempting to click HOT icon...");
        WebDriverWait wait = new WebDriverWait(driver, DEFAULT_WAIT);
        try {
            WebElement hotIcon = wait.until(
                    ExpectedConditions.elementToBeClickable(
                            By.xpath("//android.widget.ImageView[@content-desc='HOT']")));
            hotIcon.click();
            System.out.println("Successfully clicked HOT icon");
            sleep(3000);
            dump("AFTER_HOT_ICON_CLICK");
        } catch (TimeoutException e) {
            System.err.println("HOT icon not clickable within " + DEFAULT_WAIT.getSeconds() + " seconds");
            throw e;
        }
    }

    private boolean verifyActiveWindow() {
        System.out.println("\n=== Verifying Active Window ===");
        try {
            // Look for the active WebView that has focus
            List<WebElement> activeWebViews = driver.findElements(
                By.xpath("//android.webkit.WebView[@focusable='true' and @focused='true']")
            );
            
            if (!activeWebViews.isEmpty()) {
                WebElement activeWebView = activeWebViews.get(0);
                System.out.println("✅ Active WebView found:");
                System.out.println("   Class: " + activeWebView.getAttribute("class"));
                System.out.println("   Package: " + activeWebView.getAttribute("package"));
                System.out.println("   Focusable: " + activeWebView.getAttribute("focusable"));
                System.out.println("   Focused: " + activeWebView.getAttribute("focused"));
                return true;
            } else {
                System.out.println("❌ No active WebView found with focus");
                return false;
            }
        } catch (Exception e) {
            System.err.println("Error verifying active window: " + e.getMessage());
            return false;
        }
    }

    private void checkCurrentScreen() {
        System.out.println("\n=== Checking screen elements ===");
        
        // Verify active window first
        boolean isActiveWindowValid = verifyActiveWindow();
        if (!isActiveWindowValid) {
            System.out.println("⚠️  Active window verification failed");
        }

        // Define expected elements in a more maintainable way
        String[] expectedTexts = {
                "הכל", "NETFLIX", "מועדפים", "סרטים", "סדרות ובידור",
                "אקטואליה חדשות ושפות", "ילדים", "ספורט", "מוזיקה",
                "תעודה טבע ופנאי", "רדיו"
        };

        // Check all text elements
        for (String text : expectedTexts) {
            checkTextElement(text);
        }

        // Check WebView elements
        checkWebViewElements();

        // Log test results
        logTestResults();
    }

    private void logTestResults() {
        String testResult;
        if (allElementsDetected) {
            testResult = "passed";
            System.out.println("\n✅ All elements were found!");
        } else if (detectedElements.isEmpty()) {
            testResult = "failed";
            System.out.println("\n❌ Total mismatch - test FAILED");
        } else {
            testResult = "partially_passed";
            System.out.println("\n⚠️  Partial mismatch - test partially passed");
        }

        // Print summary
        System.out.println("\n=== Test Results ===");
        System.out.println("Detected elements: " + detectedElements.size());
        System.out.println("Missing elements: " + mismatchedElements.size());

        if (!mismatchedElements.isEmpty()) {
            System.out.println("\nMissing elements:");
            mismatchedElements.forEach(System.out::println);
        }

        takeScreenshot(testResult);
    }

    private void takeScreenshot(String result) {
        try {
            // Get current screen name
            String pageSource = driver.getPageSource();
            String screenName = detectScreenNameFromViewHierarchy(pageSource);
            if ("Unknown Screen".equals(screenName)) {
                screenName = "screen";
            } else {
                screenName = screenName.replace(" ", "_");
            }
            
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS").format(new Date());
            String fileName = String.format("%s/%s_%s_%s.png", 
                SCREENSHOTS_DIR, screenName, result, timestamp);

            File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            File dest = new File(fileName);
            FileUtils.copyFile(src, dest);

            System.out.println("\n📸 Screenshot saved: " + dest.getAbsolutePath());
        } catch (Exception e) {
            System.err.println("Failed to take screenshot: " + e.getMessage());
        }
    }

    private void checkWebViewElements() {
        System.out.println("\nChecking WebView elements...");
        String[] webViewElements = {
                "mod_LiveMosaic",
                "mod_LiveMosaic_Synopsis",
                "mod_LiveMosaic_NavCategories",
                "mod_LiveMosaic_SynopsisTitle",
                "mod_LiveMosaic_SynopsisContent"
        };

        try {
            Set<String> contexts = driver.getContextHandles();
            System.out.println("Available contexts: " + contexts);

            for (String ctx : contexts) {
                if (ctx.startsWith("WEBVIEW")) {
                    System.out.println("Switching to context: " + ctx);
                    driver.context(ctx);

                    for (String elementId : webViewElements) {
                        checkElementWithWait(By.id(elementId), elementId);
                    }

                    driver.context("NATIVE_APP");
                    return;
                }
            }
            System.out.println("⚠️  No WebView context found");
        } catch (Exception e) {
            System.err.println("Error checking WebView: " + e.getMessage());
            if (driver != null) {
                driver.context("NATIVE_APP"); // Ensure we switch back to native context
            }
        }
    }

    private void checkElementWithWait(By by, String name) {
        try {
            WebElement el = new WebDriverWait(driver, Duration.ofSeconds(5))
                    .until(ExpectedConditions.presenceOfElementLocated(by));

            if (el.isDisplayed()) {
                detectedElements.add(name);
                System.out.println("✅ " + name + " found");
            } else {
                throw new Exception("Element not displayed");
            }
        } catch (Exception e) {
            mismatchedElements.add(name);
            allElementsDetected = false;
            System.out.println("❌ " + name + " NOT found");
        }
    }

    private void checkTextElement(String text) {
        try {
            boolean found = driver.findElements(By.className("android.widget.TextView"))
                    .stream()
                    .anyMatch(tv -> text.equals(tv.getText()) && tv.isDisplayed());

            if (found) {
                detectedElements.add("Text: " + text);
                System.out.println("✅ Text '" + text + "' found");
            } else {
                throw new Exception("Text not found");
            }
        } catch (Exception e) {
            mismatchedElements.add("Text: " + text);
            allElementsDetected = false;
            System.out.println("❌ Text '" + text + "' NOT found");
        }
    }

    private void dump(String action) {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS").format(new Date());
        String fileName = String.format("%s/element_analysis_%s_%s.txt",
                ELEMENT_DUMPS_DIR, action, timestamp);

        try (FileWriter fw = new FileWriter(fileName)) {
            fw.write("=== ELEMENT ANALYSIS: " + action + " ===\n");
            fw.write("Timestamp: " + new Date() + "\n\n");
            fw.write(driver.getPageSource());
            System.out.println("📝 Element dump saved: " + fileName);
        } catch (IOException e) {
            System.err.println("Failed to save element dump: " + e.getMessage());
        }
    }

    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void detectSynopsis() {
        System.out.println("\n=== Synopsis Detection ===");
        boolean foundInWebView = false;

        try {
            // First try WebView context if available
            Set<String> contexts = driver.getContextHandles();
            System.out.println("Available contexts: " + contexts);

            for (String ctx : contexts) {
                if (ctx.startsWith("WEBVIEW")) {
                    try {
                        System.out.println("Trying WebView context: " + ctx);
                        driver.context(ctx);

                        // Check for synopsis elements in WebView
                        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
                        try {
                            WebElement synopsis = wait.until(ExpectedConditions.presenceOfElementLocated(
                                    By.id("mod_LiveMosaic_Synopsis")));
                            System.out.println("✅ Synopsis detected in WebView");
                            foundInWebView = true;

                            // Process WebView elements
                            processWebViewElements();

                        } catch (Exception e) {
                            System.out.println("No synopsis found in WebView: " + e.getMessage());
                        }

                        // Switch back to native context
                        driver.context("NATIVE_APP");

                    } catch (Exception e) {
                        System.err.println("Error in WebView context: " + e.getMessage());
                        driver.context("NATIVE_APP"); // Ensure we switch back
                    }
                    break; // Only try the first WebView context
                }
            }

            // If not found in WebView, try native context
            if (!foundInWebView) {
                System.out.println("Trying native context for synopsis detection");
                tryNativeSynopsisDetection();
            }

        } catch (Exception e) {
            System.err.println("Error during synopsis detection: " + e.getMessage());
            try {
                driver.context("NATIVE_APP"); // Ensure we're back in native context
            } catch (Exception ex) {
                System.err.println("Error switching back to native context: " + ex.getMessage());
            }
        }
    }

    private void processWebViewElements() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));

        // Check for synopsis title and content
        try {
            WebElement title = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.id("mod_LiveMosaic_SynopsisTitle")));
            System.out.println("   Title: " + title.getText());
        } catch (Exception e) {
            System.out.println("   No synopsis title found");
        }

        try {
            WebElement content = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.id("mod_LiveMosaic_SynopsisContent")));
            System.out.println("   Content: " + content.getText());
        } catch (Exception e) {
            System.out.println("   No synopsis content found");
        }

        // Check for LiveMosaicPipPoster
        try {
            WebElement pipPoster = wait.until(ExpectedConditions.presenceOfElementLocated(
                    By.id("LiveMosaicPipPoster")));
            System.out.println("   PiP Poster detected");
            System.out.println("   Highlighted program: " + pipPoster.getAttribute("data-program-name"));
            System.out.println("   Program number: " + pipPoster.getAttribute("data-program-number"));
        } catch (Exception e) {
            System.out.println("   No PiP Poster found");
        }
    }

    private void tryNativeSynopsisDetection() {
        try {
            // Try to find synopsis elements in native context
            List<WebElement> textElements = driver.findElements(By.className("android.widget.TextView"));
            boolean foundSynopsis = false;

            for (WebElement element : textElements) {
                String text = element.getText();
                if (text != null && !text.isEmpty()) {
                    // Look for common synopsis patterns
                    if (text.length() > 50 || text.contains(":") || text.contains("\n")) {
                        System.out.println("✅ Possible synopsis found in native view:");
                        System.out.println("   " + text.replace("\n", "\n   "));
                        foundSynopsis = true;
                        break;
                    }
                }
            }

            if (!foundSynopsis) {
                System.out.println("❌ No synopsis elements found in native view");

                // Dump the current view hierarchy for debugging
                try {
                    String pageSource = driver.getPageSource();
                    String dumpFile = ELEMENT_DUMPS_DIR + "/synopsis_dump_" +
                            new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".xml";
                    FileUtils.writeStringToFile(new File(dumpFile), pageSource, "UTF-8");
                    System.out.println("   View hierarchy dumped to: " + dumpFile);
                } catch (Exception e) {
                    System.err.println("   Failed to dump view hierarchy: " + e.getMessage());
                }
            }

        } catch (Exception e) {
            System.err.println("Error during native synopsis detection: " + e.getMessage());
        }
    }

    private void detectChannelInfo() {
        System.out.println("\n=== Channel Information ===");
        try {
            // Try to find channel logo and extract channel number
            List<WebElement> imageViews = driver.findElements(By.className("android.widget.ImageView"));
            for (WebElement img : imageViews) {
                String contentDesc = img.getAttribute("contentDescription");
                if (contentDesc != null && contentDesc.matches(".*[cC]hannel\\s*\\d+.*")) {
                    String channelNumber = contentDesc.replaceAll(".*?(\\d+).*", "$1");
                    System.out.println("📺 Channel number: " + channelNumber);
                    break;
                }
            }

            // Try to find program title for channel name
            List<WebElement> textViews = driver.findElements(By.className("android.widget.TextView"));
            for (WebElement textView : textViews) {
                String text = textView.getText();
                if (text != null && !text.isEmpty() && text.length() > 2) {  // Filter out short texts
                    System.out.println("🔤 Channel/Program: " + text);
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("Error detecting channel info: " + e.getMessage());
        }
    }

    private void detectPiPWindow() {
        System.out.println("\n=== Picture-in-Picture Detection ===");
        try {
            Thread.sleep(2000);
            String[] pipSelectors = {
                    "//*[@resource-id='LiveMosaicPipPoster_Android']",  // Specific ID from user
                    "//*[contains(@resource-id, 'LiveMosaicPipPoster')]",
                    "//*[contains(@resource-id, 'pip')]",
                    "//*[contains(@resource-id, 'picture_in_picture')]",
                    "//*[contains(@content-desc, 'PiP')]",
                    "//*[contains(@content-desc, 'Picture in Picture')]"
            };

            boolean pipFound = false;
            for (String selector : pipSelectors) {
                try {
                    List<WebElement> pipElements = driver.findElements(By.xpath(selector));
                    if (!pipElements.isEmpty()) {
                        WebElement pip = pipElements.get(0);
                        System.out.println("🖼️  PiP Window Detected");
                        System.out.println("   Selector: " + selector);
                        System.out.println("   Element: " + pip.getAttribute("class") +
                                " | " + pip.getAttribute("resource-id"));
                        System.out.println("   Bounds: " + pip.getAttribute("bounds"));
                        System.out.println("   Content-desc: " + pip.getAttribute("content-desc"));

                        // Extract program info from PiP element
                        extractProgramInfo(pip);
                        // Extract additional info from synopsis
                        extractFromSynopsis();

                        pipFound = true;
                        break;
                    }
                } catch (Exception e) {
                    System.err.println("Error checking selector " + selector + ": " + e.getMessage());
                }
            }

            if (!pipFound) {
                System.out.println("❌ No PiP Window Detected");
                // Dump view hierarchy for debugging
                dumpViewHierarchy("pip_not_found");
            }
        } catch (Exception e) {
            System.err.println("Error detecting PiP window: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                driver.context("NATIVE_APP");
            } catch (Exception e) {}
        }
    }

    private void extractProgramInfo(WebElement pipElement) {
        System.out.println("\n📺 Program Information from PiP:");
        try {
            // Get basic element info
            String resourceId = pipElement.getAttribute("resource-id");
            String contentDesc = pipElement.getAttribute("content-desc");
            String bounds = pipElement.getAttribute("bounds");

            System.out.println("   Resource ID: " + (resourceId != null ? resourceId : "N/A"));
            System.out.println("   Content Description: " + (contentDesc != null ? contentDesc : "N/A"));
            System.out.println("   Bounds: " + (bounds != null ? bounds : "N/A"));

            // Check for any data attributes that might contain program info
            for (String attr : new String[]{"data-program-name", "data-program-number",
                    "data-channel-name", "data-channel-number"}) {
                String value = pipElement.getAttribute(attr);
                if (value != null && !value.isEmpty()) {
                    System.out.println("   " + attr + ": " + value);
                }
            }
        } catch (Exception e) {
            System.out.println("   Could not extract program info from PiP element: " + e.getMessage());
        }
    }

    private void extractFromSynopsis() {
        System.out.println("\n🔍 Checking Synopsis for Program Info:");
        try {
            // Try to find synopsis elements
            String[] synopsisSelectors = {
                    "//*[contains(@resource-id, 'mod_LiveMosaic_Synopsis')]"
            };

            for (String selector : synopsisSelectors) {
                try {
                    List<WebElement> synopsisElements = driver.findElements(By.xpath(selector));
                    if (!synopsisElements.isEmpty()) {
                        WebElement synopsis = synopsisElements.get(0);
                        System.out.println("✅ Found synopsis element");

                        // Extract channel info
                        extractSynopsisElement("Channel Logo/Name", "//*[@resource-id='mod_LiveMosaic_SynopsisLogo']");
                        extractSynopsisElement("Program Title", "//*[@resource-id='mod_LiveMosaic_SynopsisTitle']");
                        extractSynopsisElement("Genre", "//*[@resource-id='mod_LiveMosaic_Genre']");
                        extractSynopsisElement("Rating", "//*[@resource-id='mod_LiveMosaic_Rating']");
                        extractSynopsisElement("Synopsis", "//*[@resource-id='mod_LiveMosaic_SynopsisContent']");

                        return;
                    }
                } catch (Exception e) {
                    System.err.println("Error checking synopsis selector " + selector + ": " + e.getMessage());
                }
            }

            System.out.println("   No synopsis information found");
            // Dump view hierarchy for debugging
            dumpViewHierarchy("synopsis_not_found");
        } catch (Exception e) {
            System.err.println("Error extracting from synopsis: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void extractSynopsisElement(String label, String xpath) {
        try {
            List<WebElement> elements = driver.findElements(By.xpath(xpath));
            if (!elements.isEmpty()) {
                String text = elements.get(0).getText();
                if (text != null && !text.trim().isEmpty()) {
                    System.out.println("   " + label + ": " + text);
                } else {
                    System.out.println("   " + label + ": [No text content]");
                }
            } else {
                System.out.println("   " + label + ": [Element not found]");
            }
        } catch (Exception e) {
            System.err.println("   Error extracting " + label + ": " + e.getMessage());
        }
    }

    private void dumpViewHierarchy(String prefix) {
        try {
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss_SSS").format(new Date());
            String filename = String.format("%s_%s.xml", prefix, timestamp);
            File outputDir = new File("element_dumps");
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }

            String pageSource = driver.getPageSource();
            try (FileWriter writer = new FileWriter(new File(outputDir, filename))) {
                writer.write(pageSource);
                System.out.println("   View hierarchy saved to: " + outputDir.getAbsolutePath() + "/" + filename);
            }
        } catch (Exception e) {
            System.err.println("Failed to save view hierarchy: " + e.getMessage());
        }
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            try {
                driver.quit();
                System.out.println("\nTest completed. Appium session terminated.");
            } catch (Exception e) {
                System.err.println("Error while quitting driver: " + e.getMessage());
            }
        }
    }
}
