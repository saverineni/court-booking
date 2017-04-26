package org.movoto.selenium.example;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.MarionetteDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertTrue;

public class ChromeDriverTest {

    public static final String USER_NAME = "8047157";
    public static final String PASSWORD = "4321";
    private String testUrl;
    private WebDriver driver;

    @Before
    public void prepare() {
        System.setProperty("webdriver.chrome.driver", "webdriver/chromedriver");
        testUrl = "https://bookings.traffordleisure.co.uk/Connect/MRMLogin.aspx";
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get(testUrl);
    }

    @Test
    public void testLogin() throws IOException {
        //Login
        driver.findElement(By.id("ctl00_MainContent_InputLogin")).sendKeys(USER_NAME);
        driver.findElement(By.id("ctl00_MainContent_InputPassword")).sendKeys(PASSWORD);
        WebElement element = driver.findElement(By.cssSelector(".signin"));
        element.click();

        // select activity
        driver.findElement(By.id("ctl00_MainContent__advanceSearchResultsUserControl_Activities_ctrl2_lnkActivitySelect_lg")).click();

        System.out.println(driver.getPageSource());
        WebElement element1 = driver.findElement(By.xpath("//td[@class='itemavailable']"));
        element1.submit();


    }

    @After
    public void teardown() throws IOException {
        //driver.quit();
    }

}
