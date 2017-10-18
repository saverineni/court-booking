package org.movoto.selenium.example;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.runners.Parameterized.Parameter;
import static org.junit.runners.Parameterized.Parameters;

@RunWith(value = Parameterized.class)
public class CourtBookingTestWithCourtNumber {

    public static final String USER_NAME = "8047157";
    public static final String PASSWORD = "4321";
    public static final String LOGIN_URL = "https://bookings.traffordleisure.co.uk/Connect/MRMLogin.aspx";
    private WebDriver driver;
    static Logger logger = LoggerFactory.getLogger(CourtBookingTestWithCourtNumber.class);

    @Parameter(value = 0)
    public String availabilityTime;

    private WebDriverWait wait;

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"21:00"},// last dayOfWeek of the displayed week
   //             {"21:00"},// last but one dayOfWeek of the displayed week
        });
    }

    @Before
    public void prepare() {
        System.setProperty("webdriver.chrome.driver", "webdriver/chromedriver");
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, 20);
        driver.manage().window().maximize();
        driver.get(LOGIN_URL);
        //Login
        driver.findElement(By.id("ctl00_MainContent_InputLogin")).sendKeys(USER_NAME);
        driver.findElement(By.id("ctl00_MainContent_InputPassword")).sendKeys(PASSWORD);
        WebElement element = driver.findElement(By.cssSelector(".signin"));
        element.click();
        // click on Badminton 1 Hour
        WebElement badminton1Hour = driver.findElement(By.id("ctl00_MainContent__advanceSearchResultsUserControl_Activities_ctrl2_lnkActivitySelect_lg"));
        badminton1Hour.click();
    }

    @Test
    public void testLogin() throws IOException, InterruptedException {
        logger.info("Started Booking .....");
        // Navigate to the required dayOfWeek
        for (int i : Arrays.asList(1, 2, 3, 4, 5, 6, 7)) {
            wait.until(ExpectedConditions.elementToBeClickable(By.id("ctl00_MainContent_Button2"))).click();
        }

        //Actual page of availability
        List<WebElement> availableTds = driver.findElements(By.className("itemavailable"));
        boolean isAvailable = false;
        for (WebElement td : availableTds) {
            WebElement input = td.findElement(By.tagName("input"));
            String time = input.getAttribute("value");

            if (time.equals(availabilityTime)) {
                WebElement parentRow = td.findElement(By.xpath(".."));
                // get all children of the parent
                List<WebElement> siblingTdsForAvilableTd = parentRow.findElements(By.xpath("td"));
                int courtIndex = 0;
                for (WebElement courtTd : siblingTdsForAvilableTd) {
                    courtIndex++;
                    // if class is itemavailable
                    if (courtTd.getAttribute("class").equals("itemavailable")) {
                        wait.until(ExpectedConditions.elementToBeClickable(By.className("itemavailable"))).click();
                        isAvailable = true;
                        break;
                    }
                }

                logger.info(String.format("Found a court %d available at %s", courtIndex, availabilityTime));

            }
        }
        if (isAvailable) {
            // check if the button label is Book or cannot book for the same dayOfWeek
            Boolean isMaximumAllowedReached = driver.findElements(By.id("ctl00_MainContent_pnError")).size() > 0;
            if (!isMaximumAllowedReached) {
                logger.info(String.format("Booking the available court"));
                WebElement bookButton = driver.findElement(By.xpath("//input[@value='Book']"));
                bookButton.click();
            } else {
                logger.info(String.format("Cannot book more than 1 court on the same dayOfWeek"));
            }
        }
    }

    @After
    public void teardown() throws IOException {
        driver.quit();
    }

}
