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
public class AltiCourtBookingTest {

    public static final String WHATSAPP_LINK = "https://web.whatsapp.com/";
    public static final String USER_NAME = "8047157";
    public static final String PASSWORD = "4321";
    public static final String LOGIN_URL = "https://bookings.traffordleisure.co.uk/Connect/MRMLogin.aspx";
    public static final String BADMINTON_SPORT_ID = "ctl00_MainContent__advanceSearchResultsUserControl_Activities_ctrl2_lnkActivitySelect_lg";
    private static final String PERSON_NAME = "Guntur Girl";
    private WebDriver driver;
    static Logger logger = LoggerFactory.getLogger(CourtBookingTestWithCourtNumber.class);

    @Parameter(value = 0)
    public String availabilityTime;

    @Parameter(value = 1)
    public String leisureCentre;

    private String availabilityDateTime = null;

    private WebDriverWait wait;

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"21:30", "ALT"},  //Saturday (keep running program on friday)
                {"20:30", "SAL"},   //Tuesday (keep running program on monday)
                {"09:00", "SAL"}    //Sunday (keep running program on saturday)
        });
    }

    @Before
    public void prepare() {
        System.setProperty("webdriver.chrome.driver", "webdriver/chromedriver");
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
        driver.manage().timeouts().pageLoadTimeout(20, TimeUnit.SECONDS);
        driver.manage().timeouts().setScriptTimeout(20, TimeUnit.SECONDS);
        driver.manage().window().maximize();
        wait = new WebDriverWait(driver, 20);
        performLogin();
        selectLeisureCentre();
        selectBadmintonSport();
    }


    @Test
    public void testLogin() throws IOException, InterruptedException {
        NavigateToRequiredDay();
        List<WebElement> availableTds = driver.findElements(By.className("itemavailable"));
        boolean isAvailable = false;
        for (WebElement td : availableTds) {
            WebElement input = td.findElement(By.tagName("input"));
            String time = input.getAttribute("value");

            if (time.equals(availabilityTime)) {
                availabilityDateTime = driver.findElement(By.xpath("//*[@id='ctl00_MainContent_lblCurrentNavDate']")).getText();
                availabilityDateTime += " - " + availabilityTime;
                td.click();
                isAvailable = true;
                logger.info(String.format("Found a court at %s on %s", leisureCentre, availabilityDateTime));
                break;
            }
        }
        if (isAvailable) {
            // check if the button label is Book or cannot book for the same day
            Boolean isMaximumAllowedReached = driver.findElements(By.id("ctl00_MainContent_pnError")).size() > 0;
            if (!isMaximumAllowedReached) {
                logger.info(String.format("Booked a court on at %s %s", leisureCentre, availabilityDateTime));
                driver.findElement(By.xpath("//input[@value='Book']")).click();
            } else {
                logger.info(String.format("Cannot book more than 1 court on the same day"));
            }
        } else {
            logger.info(String.format("Court isn't available at %s for leisure centre %s", availabilityTime, leisureCentre));
        }

        //write to whatsapp
        // postToWhatsapp();
    }

    private void postToWhatsapp() {
        driver.get(WHATSAPP_LINK);
        driver.findElement(By.cssSelector("span[title='" + PERSON_NAME + "']")).click();

        List<WebElement> list = driver.findElements(By.className("input"));

        WebElement selectedElement = list.get(1);
        selectedElement.sendKeys(String.format("Booked a court on %s", availabilityDateTime));

        WebElement buttonElement = driver.findElement(By.cssSelector("button[class='icon btn-icon icon-send send-container']"));
        buttonElement.click();
        logger.info(String.format("Posted to whatsapp"));

    }

    private void selectLeisureCentre() {
//        WebElement webElement = wait.until(ExpectedConditions.elementToBeClickable((By.xpath("//*[@id='ctl00__hyperLinkChangePreferredSiteFooter']"))));
//        webElement.click();

        driver.get("https://bookings.traffordleisure.co.uk/Connect/PreferredSiteSelection.aspx");
        By xpath = By.xpath("//*[@id='ctl00_MainContent__ddlAvailableSites']");
        WebElement webElement = wait.until(ExpectedConditions.elementToBeClickable(xpath));
        Select select = new Select(driver.findElement(xpath));
        select.selectByValue(leisureCentre);
        driver.findElement(By.xpath("//*[@id=\"ctl00_MainContent__btnConfirm\"]")).click();
    }

    private void selectBadmintonSport() {
        //Now wait for invisibility of progress bar first
        //myWaitVar.until(ExpectedConditions.invisibilityOfElementLocated(By.id("page_loader")));
        //Now wait till "Cancel" button is showing up. At cases, it may take some time.
        //WebElement el = myWaitVar.until(ExpectedConditions.elementToBeClickable(By.id("cancelRegister")));
        //el.click();
        WebElement webElement = wait.until(ExpectedConditions.elementToBeClickable(By.id(BADMINTON_SPORT_ID)));
        webElement.click();
    }

    private void performLogin() {
        driver.get(LOGIN_URL);
        driver.findElement(By.id("ctl00_MainContent_InputLogin")).sendKeys(USER_NAME);
        driver.findElement(By.id("ctl00_MainContent_InputPassword")).sendKeys(PASSWORD);
        driver.findElement(By.cssSelector(".signin")).click();
    }


    private void NavigateToRequiredDay() {
        logger.info("Started Booking .....");
        for (int i : Arrays.asList(1, 2, 3, 4, 5, 6, 7)) {
            wait.until(ExpectedConditions.elementToBeClickable(By.id("ctl00_MainContent_Button2"))).click();
        }
    }

    @After
    public void teardown() throws IOException {
          driver.quit();
    }

}
