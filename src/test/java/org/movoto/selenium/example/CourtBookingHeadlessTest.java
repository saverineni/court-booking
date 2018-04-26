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
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.DateFormatSymbols;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.junit.runners.Parameterized.Parameter;
import static org.junit.runners.Parameterized.Parameters;
import static org.movoto.selenium.example.CourtBookingHeadlessTest.LeisureCentres.ALT;
import static org.movoto.selenium.example.CourtBookingHeadlessTest.LeisureCentres.SAL;

@RunWith(value = Parameterized.class)
public class CourtBookingHeadlessTest {

    private static final String WHATSAPP_LINK = "https://web.whatsapp.com/";
    private static final String SURESH_USER_NAME = "8047157";
    private static final String SURESH_PASSWORD = "4321";
    private static final String MANJU_USER_NAME = "8051562";
    private static final String MANJU_PASSWORD = "4544";
    private static final String BINDU_USER_NAME = "7058801";
    private static final String BINDU_PASSWORD = "5243";
    private static final String LOGIN_URL = "https://bookings.traffordleisure.co.uk/Connect/MRMLogin.aspx";
    private static final String ALT_ONE_HOUR_BADMINTON_SPORT_ID = "ctl00_MainContent__advanceSearchResultsUserControl_Activities_ctrl2_lnkActivitySelect_lg";
    private static final String SAL_ONE_HOUR_BADMINTON_SPORT_ID = "ctl00_MainContent__advanceSearchResultsUserControl_Activities_ctrl1_lnkActivitySelect_lg";
    private static final String PERSON_NAME = "Guntur Girl";
    private static final String CHANGE_SITE_BUTTON_ID = "ctl00__hyperLinkChangePreferredSiteFooter";
    private static final String DATE_TEXT_ID = "ctl00_MainContent_lblCurrentNavDate";
    private static final int TIME_OUT = 20;
    private WebDriver driver;
    static Logger logger = LoggerFactory.getLogger(CourtBookingHeadlessTest.class);


    @Parameter(value = 0)
    public String availabilityTime;

    @Parameter(value = 1)
    public LeisureCentres leisureCentre;

    @Parameter(value = 2)
    public String userName;

    @Parameter(value = 3)
    public String password;

    @Parameter(value = 4)
    public int daysFromNow;

    @Parameter(value = 5)
    public int dayOfWeek;

    private WebDriverWait wait;

    public enum LeisureCentres {
        ALT, SAL
    }

    // Saturday  8am–9pm  Sunday    8am–10pm  Monday	  8am–10pm  Tuesday	  8am–10pm
    //  Wednesday 8am–10pm Thursday  8am–10pm  Friday	  8am–10pm

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{

//                //Wednesday
                {"20:30", ALT, SURESH_USER_NAME, SURESH_PASSWORD, 7, Calendar.WEDNESDAY},
                {"21:00", ALT, MANJU_USER_NAME, MANJU_PASSWORD, 7, Calendar.WEDNESDAY},
//                //Thursday
                {"19:00", SAL, SURESH_USER_NAME, SURESH_PASSWORD, 7, Calendar.THURSDAY},
                {"19:00", SAL, MANJU_USER_NAME, MANJU_PASSWORD, 7, Calendar.THURSDAY},
//                //Friday
                {"20:30", ALT, SURESH_USER_NAME, SURESH_PASSWORD, 7, Calendar.FRIDAY},
                {"21:00", ALT, MANJU_USER_NAME, MANJU_PASSWORD, 7, Calendar.FRIDAY},
//                //Sunday
                {"08:00", ALT, SURESH_USER_NAME, SURESH_PASSWORD, 7, Calendar.SUNDAY},
                {"08:00", ALT, MANJU_USER_NAME, MANJU_PASSWORD, 7, Calendar.SUNDAY},

        });
    }


    @Before
    public void prepare() {
        java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(java.util.logging.Level.OFF);
        setDriverSettings();
        performLogin();
        selectLeisureCentre();
        if (leisureCentre == ALT) {
            scrollToFindElement(ALT_ONE_HOUR_BADMINTON_SPORT_ID);
        } else {
            scrollToFindElement(SAL_ONE_HOUR_BADMINTON_SPORT_ID);
        }

    }

    private void setDriverSettings() {
        driver = new HtmlUnitDriver(true);
        driver.manage().timeouts().implicitlyWait(TIME_OUT, TimeUnit.SECONDS);
        driver.manage().timeouts().pageLoadTimeout(TIME_OUT, TimeUnit.SECONDS);
        driver.manage().timeouts().setScriptTimeout(TIME_OUT, TimeUnit.SECONDS);
        wait = new WebDriverWait(driver, TIME_OUT);
    }


    @Test
    public void bookCourt() throws IOException, InterruptedException {

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        if (dayOfWeek == cal.get(Calendar.DAY_OF_WEEK)) {
            logger.info(String.format("****EACH RUN****AT*****" + leisureCentre + "****USER****" + userName));
            logger.info(String.format("Running on  %s", getWeekDay(cal)));
            NavigateToRequiredDay();

            String availabilityDateTime = driver.findElement(By.id(DATE_TEXT_ID)).getText();
            availabilityDateTime += " - " + availabilityTime;

            boolean isAvailable = isAnyCourtAvailable();
            if (isAvailable) {
                logger.info(String.format("Found a court at %s on %s", leisureCentre, availabilityDateTime));
                Boolean isMaximumAllowedReached = driver.findElements(By.id("ctl00_MainContent_pnError")).size() > 0;
                if (!isMaximumAllowedReached) {
//                    logger.info("On the confirm booking PAGE - URL ************" + driver.getCurrentUrl());
                    WebElement bookButton = driver.findElement(By.id("ctl00_MainContent_btnBasket"));
                    if (bookButton.isDisplayed()) {
                        logger.info("Clicking BOOK button ");
                        try {
                            bookButton.submit();
                        } catch (Exception e) {
                            logger.info("Exception occurred ");
                            logger.error(e.getMessage());
                        }
                        logger.info("Clicked BOOK button ");
                    }
                    logger.info("Before getting success text");
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

    }

    private String getWeekDay(Calendar cal) {
        String[] weekdays = new DateFormatSymbols().getWeekdays();
        return weekdays[cal.get(Calendar.DAY_OF_WEEK)];
    }

    private boolean isAnyCourtAvailable() {
        List<WebElement> availableTds = driver.findElements(By.xpath(("//td[contains(@class,'itemavailable')]/input[contains(@value,'" + availabilityTime + "')]")));
        boolean isAvailable = false;
        if (availableTds.size() > 0) {
            WebElement firstAvailableTd = availableTds.get(0);
            ((JavascriptExecutor) driver).executeScript("window.scrollTo(0," + firstAvailableTd.getLocation().y + ")");
            firstAvailableTd.click();
            isAvailable = true;
        }
        return isAvailable;
    }

    private void selectLeisureCentre() {
        scrollToFindElement(CHANGE_SITE_BUTTON_ID);
        By id = By.id("ctl00_MainContent__ddlAvailableSites");
        wait.until(ExpectedConditions.elementToBeClickable(id));
        Select select = new Select(driver.findElement(id));
        select.selectByValue(leisureCentre.name());
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

    @After
    public void teardown() throws IOException {
        driver.close();
        driver.quit();
    }

}
