package org.movoto.selenium.example;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.runners.Parameterized.Parameter;
import static org.junit.runners.Parameterized.Parameters;

@RunWith(value = Parameterized.class)
public class CourtBookingTest {

    private static final String WHATSAPP_LINK = "https://web.whatsapp.com/";
    private static final String SURESH_USER_NAME = "8047157";
    private static final String SURESH_PASSWORD = "4321";
    private static final String LOGIN_URL = "https://bookings.traffordleisure.co.uk/Connect/MRMLogin.aspx";
    private static final String ONE_HOUR_BADMINTON_SPORT_ID = "ctl00_MainContent__advanceSearchResultsUserControl_Activities_ctrl2_lnkActivitySelect_lg";
    private static final String PERSON_NAME = "Guntur Girl";
    private static final String CHANGE_SITE_BUTTON_ID = "ctl00__hyperLinkChangePreferredSiteFooter";
    private static final String DATE_TEXT_ID = "ctl00_MainContent_lblCurrentNavDate";
    private static final int TIME_OUT = 10;
    private WebDriver driver;
    static Logger logger = LoggerFactory.getLogger(CourtBookingTestWithCourtNumber.class);

    @Parameter(value = 0)
    public String availabilityTime;

    @Parameter(value = 1)
    public String leisureCentre;

    @Parameter(value = 2)
    public String userName;

    @Parameter(value = 3)
    public String password;

    @Parameter(value = 4)
    public int daysFromNow;

    private WebDriverWait wait;

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"10:00", "ALT", SURESH_USER_NAME, SURESH_PASSWORD, 0},  //Saturday (keep running program on friday)
                {"10:00", "SAL", SURESH_USER_NAME, SURESH_PASSWORD, 1},  //Saturday (keep running program on friday)
                {"10:00", "ALT", SURESH_USER_NAME, SURESH_PASSWORD, 2},  //Saturday (keep running program on friday)
                {"10:00", "SAL", SURESH_USER_NAME, SURESH_PASSWORD, 3},  //Saturday (keep running program on friday)
                {"10:00", "ALT", SURESH_USER_NAME, SURESH_PASSWORD, 4},  //Saturday (keep running program on friday)
                {"10:00", "SAL", SURESH_USER_NAME, SURESH_PASSWORD, 5},  //Saturday (keep running program on friday)
                {"10:00", "ALT", SURESH_USER_NAME, SURESH_PASSWORD, 6},    //Sunday (keep running program on saturday)
                {"16:00", "SAL", SURESH_USER_NAME, SURESH_PASSWORD, 7}    //Sunday (keep running program on saturday)
        });
    }

    @Before
    public void prepare() {
        System.setProperty("webdriver.chrome.driver", "webdriver/chromedriver");
        setDriverSettings();
        performLogin();
        selectLeisureCentre();
        scrollToFindElement(ONE_HOUR_BADMINTON_SPORT_ID);
    }

    private void setDriverSettings() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--kiosk");
        driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(TIME_OUT, TimeUnit.SECONDS);
        driver.manage().timeouts().pageLoadTimeout(TIME_OUT, TimeUnit.SECONDS);
        driver.manage().timeouts().setScriptTimeout(TIME_OUT, TimeUnit.SECONDS);
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, TIME_OUT);
    }


    @Test
    public void bookCourt() throws IOException, InterruptedException {
        NavigateToRequiredDay();

        String availabilityDateTime = driver.findElement(By.id(DATE_TEXT_ID)).getText();
        availabilityDateTime += " - " + availabilityTime;
        List<WebElement> availableTds = driver.findElements(By.className("itemavailable"));

        boolean isAvailable = isAnyCourtAvailable(availableTds);
        if (isAvailable) {
            logger.info(String.format("Found a court at %s on %s", leisureCentre, availabilityDateTime));
            Boolean isMaximumAllowedReached = driver.findElements(By.id("ctl00_MainContent_pnError")).size() > 0;
            if (!isMaximumAllowedReached) {
                driver.findElement(By.id("ctl00_MainContent_btnBasket")).click();
                String successText = driver.findElement(By.xpath("//div[contains(@class,'bookingConfirmedContent-content')]/h1")).getText();
                logger.info(String.format(successText + " at %s on %s", leisureCentre, availabilityDateTime));
            } else {
                String failureText = driver.findElement(By.xpath("//div[contains(@class,'alert-danger')]")).getText();
                logger.info(String.format("Cannot book the court - %s - %s", failureText, availabilityDateTime));
            }
        } else {
            logger.info(String.format("Court isn't available at %s for leisure centre %s", availabilityDateTime, leisureCentre));
        }
    }

    private boolean isAnyCourtAvailable(List<WebElement> availableTds) {
        boolean isAvailable = false;
        for (WebElement td : availableTds) {
            String time = td.findElement(By.tagName("input")).getAttribute("value");

            if (time.equals(availabilityTime)) {
                ((JavascriptExecutor) driver).executeScript("window.scrollTo(0," + td.getLocation().y + ")");
                td.click();
                isAvailable = true;
                break;
            }
        }
        return isAvailable;
    }

    private void selectLeisureCentre() {
        scrollToFindElement(CHANGE_SITE_BUTTON_ID);
        By id = By.id("ctl00_MainContent__ddlAvailableSites");
        wait.until(ExpectedConditions.elementToBeClickable(id));
        Select select = new Select(driver.findElement(id));
        select.selectByValue(leisureCentre);
        driver.findElement(By.id("ctl00_MainContent__btnConfirm")).click();

    }

    private void scrollToFindElement(String id) {
        WebElement elementToClick = driver.findElement(By.id(id));
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0," + elementToClick.getLocation().y + ")");
        elementToClick.click();
    }

    private void performLogin() {
        driver.get(LOGIN_URL);
        driver.findElement(By.id("ctl00_MainContent_InputLogin")).sendKeys(userName);
        driver.findElement(By.id("ctl00_MainContent_InputPassword")).sendKeys(password);
        driver.findElement(By.cssSelector(".signin")).click();
    }


    private void NavigateToRequiredDay() {
        logger.info("Started Booking .....");
        for (int i = 1; i <= daysFromNow; i++) {
            wait.until(ExpectedConditions.elementToBeClickable(By.id("ctl00_MainContent_Button2"))).click();
        }
    }

    private void postToWhatsapp(String availabilityDateTime) {
        driver.get(WHATSAPP_LINK);
        driver.findElement(By.cssSelector("span[title='" + PERSON_NAME + "']")).click();

        List<WebElement> list = driver.findElements(By.className("input"));

        WebElement selectedElement = list.get(1);
        selectedElement.sendKeys(String.format("Booked a court on %s", availabilityDateTime));

        WebElement buttonElement = driver.findElement(By.cssSelector("button[class='icon btn-icon icon-send send-container']"));
        buttonElement.click();
        logger.info(String.format("Posted to whatsapp"));

    }

    @After
    public void teardown() throws IOException {
        driver.quit();
    }

}
