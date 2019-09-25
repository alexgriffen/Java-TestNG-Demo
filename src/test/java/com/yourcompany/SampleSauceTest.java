package com.yourcompany;

import com.saucelabs.common.SauceOnDemandAuthentication;
import com.saucelabs.common.SauceOnDemandSessionIdProvider;
import com.saucelabs.testng.SauceOnDemandAuthenticationProvider;
import com.saucelabs.testng.SauceOnDemandTestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.JavascriptExecutor;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import static org.testng.Assert.assertEquals;

public class SampleSauceTest {

    public String sauce_username = System.getenv("SAUCE_USERNAME");
    public String sauce_accesskey = System.getenv("SAUCE_ACCESS_KEY");

    /**
     * ThreadLocal variable which contains the  {@link WebDriver} instance which is used to perform browser interactions with.
     */
    private ThreadLocal<WebDriver> webDriver = new ThreadLocal<WebDriver>();

    /**
     * ThreadLocal variable which contains the Sauce Job Id.
     */
    private ThreadLocal<String> sessionId = new ThreadLocal<String>();

    /**
     * DataProvider that explicitly sets the browser combinations to be used.
     *
     * @param testMethod
     * @return
     * @throws JSONException 
     */
    @DataProvider(name = "hardCodedBrowsers", parallel = true)
    public static Object[][] sauceBrowserDataProvider(Method testMethod) {
        return new Object[][]{

                // Windows
                new Object[]{"chrome", "76.0", "Windows 10"},
                new Object[]{"MicrosoftEdge", "18.17763", "Windows 10"},
                new Object[]{"firefox", "62.0", "Windows 10"},
                new Object[]{"internet explorer", "11", "Windows 8.1"},
                new Object[]{"firefox", "55.0", "Windows 7"},
        
                // Mac
                new Object[]{"firefox", "64", "macOS 10.14"},
                new Object[]{"safari", "12", "macOS 10.13"},
                new Object[]{"safari", "11.0", "macOS 10.12"},
                new Object[]{"chrome", "70.0", "OS X 10.11"},
                new Object[]{"chrome", "69.0", "OS X 10.10"}

                // Headless
                // new Object[]{"firefox", "latest", "Linux"},
                // new Object[]{"firefox", "latest-1", "Linux"},
                // new Object[]{"firefox", "latest-2", "Linux"},
                // new Object[]{"chrome", "latest", "Linux"},
                // new Object[]{"chrome", "latest-1", "Linux"},
                // new Object[]{"chrome", "latest-2", "Linux"},
                // new Object[]{"chrome", "latest", "Linux"},
                // new Object[]{"chrome", "latest-1", "Linux"},
                // new Object[]{"chrome", "latest-2", "Linux"}
        };
    }

    /**
     * Constructs a new {@link RemoteWebDriver} instance which is configured to use the capabilities defined by the browser,
     * version and os parameters, and which is configured to run against ondemand.saucelabs.com, using
     * the username and access key populated by the {@link #authentication} instance.
     *
     * @param browser Represents the browser to be used as part of the test run.
     * @param version Represents the version of the browser to be used as part of the test run.
     * @param os Represents the operating system to be used as part of the test run.
     * @return
     * @throws MalformedURLException if an error occurs parsing the url
     */
    private WebDriver createDriver(String browser, String version, String os, String methodName) throws MalformedURLException {

        DesiredCapabilities capabilities = new DesiredCapabilities();

        capabilities.setCapability("username", sauce_username);
        capabilities.setCapability("accesskey", sauce_accesskey);

        capabilities.setCapability("browserName", browser);
        capabilities.setCapability("version", version);
        capabilities.setCapability("platform", os);
        
        //Sends test name as a desired capability to update the Sauce Labs dash board
        String jobName = methodName;
        capabilities.setCapability("name", jobName);
        capabilities.setCapability("extendedDebugging", true);
        capabilities.setCapability("capturePerformance", true);


        //Local Driver

        // WebDriver driver = new FireFoxDriver();
        
        //Creates Selenium Driver
        webDriver.set(new RemoteWebDriver(
                new URL("https://ondemand.saucelabs.com:443/wd/hub"),
                capabilities));

        // // Headless
        // webDriver.set(new RemoteWebDriver(
        //         new URL("https://ondemand.us-east-1.saucelabs.com/wd/hub"),
        //         capabilities));

        // EU
        // webDriver.set(new RemoteWebDriver(
        //         new URL("https://ondemand.eu-central-1.saucelabs.com/wd/hub"),
        //         capabilities));
        
      //Keeps track of the unique Selenium session ID used to identify jobs on Sauce Labs
        String id = ((RemoteWebDriver) getWebDriver()).getSessionId().toString();
        sessionId.set(id);
        
        //For CI plugins
        String message = String.format("SauceOnDemandSessionID=%1$s job-name=%2$s", id, jobName);
        System.out.println(message);

        return webDriver.get();
    }

    @AfterMethod
    public void tearDown(ITestResult result) throws Exception {
    	boolean status = result.isSuccess();
    	((JavascriptExecutor)webDriver.get()).executeScript("sauce:job-result="+ status); 
        webDriver.get().quit();
    }

    /**
     * Runs a simple test verifying the title of the wikipedia.org home page.
     *
     * @param browser Represents the browser to be used as part of the test run.
     * @param version Represents the version of the browser to be used as part of the test run.
     * @param os Represents the operating system to be used as part of the test run.
     * @param Method Represents the method, used for getting the name of the test/method
     * @throws Exception if an error occurs during the running of the test
     */


    @Test(dataProvider = "hardCodedBrowsers")
    public void invalidLoginFlow(String browser, String version, String os, Method method) throws Exception {

        WebDriver driver = createDriver(browser, version, os, method.getName());
        login(driver, "MySauceUsername", "incorrectPassword");
        isErrorPresent(driver);
    }

    @Test(dataProvider = "hardCodedBrowsers")
    public void demoPageTitle(String browser, String version, String os, Method method) throws Exception {

        WebDriver driver = createDriver(browser, version, os, method.getName());
        driver.get("https://www.saucedemo.com");
        assertEquals(driver.getTitle(), "Swag Labs");
    }

    @Test(dataProvider = "hardCodedBrowsers")
    public void validLoginFlow(String browser, String version, String os, Method method) throws Exception {

        WebDriver driver = createDriver(browser, version, os, method.getName());
        login(driver, "standard_user", "secret_sauce");
        assert(driver.findElement(By.id("inventory_container")).isDisplayed());
    }

    @Test(dataProvider = "hardCodedBrowsers")
    public void problemLoginFlow(String browser, String version, String os, Method method) throws Exception {

        WebDriver driver = createDriver(browser, version, os, method.getName());
        login(driver, "problem_user", "secret_sauce");
        assert(driver.findElement(By.id("inventory_container")).isDisplayed());
    }

    @Test(dataProvider = "hardCodedBrowsers")
    public void lockedOutLoginFlow(String browser, String version, String os, Method method) throws Exception {

        WebDriver driver = createDriver(browser, version, os, method.getName());
        login(driver, "locked_out_user", "secret_sauce");
        isErrorPresent(driver);
    }

    /**
     * @return the {@link WebDriver} for the current thread
     */
    public WebDriver getWebDriver() {
        System.out.println("WebDriver" + webDriver.get());
        return webDriver.get();
    }

    /**
     *
     * @return the Sauce Job id for the current thread
     */
    public String getSessionId() {
        return sessionId.get();
    }

    public void login(WebDriver driver, String username, String password) {
        WebDriverWait wait = new WebDriverWait(driver, 15);
        ((JavascriptExecutor)driver).executeScript("sauce:context=Navigate to the Sauce Labs Login Page"); 
        driver.get("https://www.saucedemo.com");
        ((JavascriptExecutor)driver).executeScript("sauce:context=Enter Username");        
        WebElement usernameInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("user-name")));
        usernameInput.sendKeys(username);
        ((JavascriptExecutor)driver).executeScript("sauce:context=Enter Incorrect Password");     
        driver.findElement(By.id("password")).sendKeys(password);
        ((JavascriptExecutor)driver).executeScript("sauce:context=Click Submit Button");
        driver.findElement(By.cssSelector("#login_button_container > div > form > input.btn_action")).click();

    }

    public void isErrorPresent(WebDriver driver) {
        WebDriverWait wait = new WebDriverWait(driver, 15);
        ((JavascriptExecutor)driver).executeScript("sauce:context=Assert Error Message is Present");         
        String errorSelector = "#login_button_container > div > form > h3";        
        WebElement errorMessage = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(errorSelector)));
        assert(errorMessage.isDisplayed());
    }

}