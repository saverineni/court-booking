package org.movoto.selenium.example;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;
import java.net.URL;

class DriverFactory {
    static ChromeDriver createDriver(String browser) {
        switch (browser) {
            case "chrome":
                return createChromeDriver();
            default:
                throw new IllegalArgumentException("Cannot create driver from browser type [$browser]");
        }
    }

    private static ChromeDriver createChromeDriver() {
        File chromeDriver = null;
        ChromeOptions options = new ChromeOptions();
         if (isMac()) {
            URL url = Thread.currentThread().getContextClassLoader().getResource("drivers/mac/chrome/chromedriver");
            chromeDriver = new File(url.getPath());
        } else if (isLinux()) {
            URL url = Thread.currentThread().getContextClassLoader().getResource("drivers/linux/chrome/chromedriver");
            chromeDriver = new File(url.getPath());
            options.addArguments("--headless","--disable-gpu", "--no-sandbox");
        }
        System.setProperty("webdriver.chrome.driver", chromeDriver.getAbsolutePath());
        return new ChromeDriver(options);
    }


    private static String getOSName() {
        return System.getProperty("os.name").toLowerCase();
    }

    private static boolean isWindows() {
        return (getOSName().indexOf("win") >= 0);
    }

    private static boolean isMac() {
        return (getOSName().indexOf("mac") >= 0);
    }

    private static boolean isLinux() {
        return (getOSName().indexOf("linux") >= 0);
    }
}