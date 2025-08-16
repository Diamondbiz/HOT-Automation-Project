
![Automation Project PoC](https://github.com/user-attachments/assets/1517e0a9-4c0c-4fac-b53f-14a39b0d00a0)


[![HOT TV App for Android TV](https://img.shields.io/badge/HOT_TV_App-Android_TV_Install_Guide-4285F4?logo=androidtv&logoColor=white)](https://campaign.hot.net.il/routines/installinghottvapp/)
[![UiAutomator2](https://img.shields.io/badge/UIautomator2-2.29.0-green?logo=android)](https://appium.io/docs/en/2.0/quickstart/uiauto2-driver/)

[![Update Chrome](https://img.shields.io/badge/Chrome-Check%20for%20Updates-blue?logo=google-chrome)](chrome://settings/help)
[![GitHub Stars](https://img.shields.io/github/stars/Diamondbiz/HOT-POC?style=social)](https://github.com/Diamondbiz/HOT-POC/stargazers)
[![GitHub Forks](https://img.shields.io/github/forks/Diamondbiz/HOT-POC?style=social)](https://github.com/Diamondbiz/HOT-POC/network/members)


[![GitHub Issues](https://img.shields.io/github/issues/Diamondbiz/HOT-POC?color=ff0000&label=ISSUES)](https://github.com/Diamondbiz/HOT-POC/issues)
[![Appium Docs](https://img.shields.io/badge/Appium_Docs-2.0-%238a2be2?logo=appium&logoColor=white)](https://appium.io/docs/en/2.0/)



[![Selenium WebDriver](https://img.shields.io/badge/Selenium_WebDriver-Docs-%2343B02A?logo=selenium&logoColor=white)](https://www.selenium.dev/documentation/webdriver/)

[![Android Debug Bridge](https://img.shields.io/badge/ADB-Documentation-%233DDC84?logo=android&logoColor=white)](https://developer.android.com/tools/adb)

[![npm](https://img.shields.io/npm/v/npm?label=npm%20latest&color=blue&logo=npm)](https://www.npmjs.com/package/npm)
[![Node.js & npm](https://img.shields.io/static/v1?label=Node.js%20%26%20npm&message=Latest%20LTS&color=brightgreen&logo=node.js&logoColor=white)](https://nodejs.org)



HOT Smart TV Automation Testing (PoC)

Project Overview

This Proof of Concept (PoC) automates functional testing for the HOT Smart TV application, a hybrid (web app on Android) streaming platform. The automation framework leverages Appium 2.0 with UiAutomator2 for native Android interactions and Selenium WebDriver for web-based components.


Testing Environment
Hardware:
1. PC/Laptop - MACBOOK Pro M1 
2. Device Under Test: Android 14 (Streaming Device)

Soft:

1. Tested Application: HOT Smart TV (Hybrid - WebView + Native)
   The SDK file will be provided by developers.

2. Automation Framework: Appium 2.0
   CLI: version check: 
        version update:

3. Native Android Tool: UiAutomator 2.0
   CLI: version check: 
        version update:

4. Programming Language: Java
   CLI: version check: java -version
        version update: Latest LTS: Oracle JDK - https://www.oracle.com/java/technologies/downloads/?er=221886
                                    OpenJDK - https://openjdk.org/projects/jdk/

5.IDE: IntelliJ IDE

6. NodeJS: 
    CLI: version check: node -v
         Resoudce and update: https://nodejs.org/en
         Version install/update:
         # Using nvm (recommended)
         nvm install --lts
         nvm use --lts
         # Homebrew (macOS)
         brew upgrade node
         # Windows (via nvm-windows)
         nvm install latest

6. Additional Tools:
* npm (Dependency Management)
    CLI: version check: npm -v
         version update: npm install -g npm@latest

* Selenium WebDriver (Web Automation)
    CLI: version check: 
         version update:
         Version update via the web: https://www.selenium.dev/downloads/

* adb bridge


7. Google Chrome browser- Last version source: https://chromereleases.googleblog.com/
-CLI: version check on MAC:
   -GUI Method:
   1. Open Google Chrome
   2. Click Chrome → About Google Chrome in the menu bar
   3. You'll see the full version (e.g., 124.0.6367.91)
-Version update:
   1. Open Google Chrome
   2. Click Chrome → About Google Chrome in the menu bar
   3. You'll see the full version (e.g., 124.0.6367.91)
   4. Click on update and then restart the browser
    
-Command Line Alternative (macOS)
-If you installed Chrome via Homebrew: /Applications/Google\ Chrome.app/Contents/MacOS/Google\ Chrome --version
-Update homebrew (if needed): brew update && brew upgrade
-Version Update via CLI:
         1. Brew upgrade --cask google-chrome
         2. Veryfy update: /Applications/Google\ Chrome.app/Contents/MacOS/Google\ Chrome --version
         3. Restart the browser

PoC Project main goals:

The main goals of test automation creation are to enhance software quality, improve efficiency, and support continuous delivery while reducing risks and costs. Here’s a breakdown of the key objectives:


1. Improve Testing Efficiency
- Speed: Execute tests faster than manual testing (e.g., run hundreds of tests in minutes).
- Reusability: Reuse test scripts across different platforms, OS, versions/features.
- 24/7 Execution: Run tests overnight or in CI/CD pipelines (e.g., with GitHub Actions/Jenkins).

2. Enhance Test Coverage
- Broad Scenarios: Cover more features, edge cases, and configurations (e.g., cross-browser/device testing).
- Regression Testing: Automatically verify existing functionality after changes.

3. Ensure Consistency & Accuracy
- Eliminate Human Error: Avoid manual mistakes in repetitive tests.
- Standardized Checks: Follow predefined assertions (e.g., validate APIs with statusCode: 200).

4. Support Agile/DevOps Practices
- CI/CD Integration: Automate tests in pipelines (e.g., run on every git push).
- Fast Feedback: Provide immediate results to developers (e.g., block broken builds).

5. Reduce Costs & Resources
- Long-Term Savings: Lower costs compared to manual testing over time.
- Parallel Execution: Run tests simultaneously on multiple environments (e.g., Selenium Grid).

6. Facilitate Early Bug Detection
- Shift-Left Testing: Catch issues early in development (e.g., unit/integration tests).
- Continuous Monitoring: Track performance/errors in production-like environments.

7. Improve Reporting & Transparency
- Detailed Logs: Generate automated reports (e.g., Allure/Extent Reports).
- Metrics Tracking: Measure flakiness, pass/fail rates, and test duration.

8. Enable Scalability
- Adapt to Growth: Scale tests for new features without linear effort increases.
- Cross-Platform: Support web, mobile (Appium), APIs, and desktop apps.

Key Tools to Achieve These Goals
Goal	Example Tools
Web Testing - Selenium, Cypress, Playwright
Mobile Testing -Appium, Espresso, XCUITest
API Testing	Postman, RestAssured, SuperTest
Performance - JMeter, k6
CI/CD Integration	- Jenkins, GitHub Actions, CircleCI, Docker























