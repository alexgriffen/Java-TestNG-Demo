package com.yourcompany;

import org.json.JSONException;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.html5.Location;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.remote.*;
import org.openqa.selenium.remote.html5.RemoteLocationContext;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.testng.Assert;
import org.testng.AssertJUnit;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.lang.reflect.Method;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.HashMap;
import java.util.Map;


public class SampleSauceTest {

    public String sauce_username = System.getenv("SAUCE_USERNAME");
    public String sauce_accesskey = System.getenv("SAUCE_ACCESS_KEY");
    public String buildTag = System.getenv("BUILD_TAG");

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
                new Object[]{"latest", "Windows 10"},
                new Object[]{"latest-1", "Windows 10"},
                new Object[]{"latest-2", "Windows 10"},



                // Mac
                new Object[]{"latest", "macOS 10.15"},
                new Object[]{"latest-1", "macOS 10.15"},
                new Object[]{"latest-2", "macOS 10.15"}


                // EmuSim
//                new Object[]{"Chrome","10", "Android"},

        };
    }

    /**
     * Constructs a new {@link RemoteWebDriver} instance which is configured to use the capabilities defined by the browser,
     * version and os parameters, and which is configured to run against ondemand.saucelabs.com, using
     * the username and access key populated by the {@link #authentication} instance.
     *
     * @param version Represents the version of the browser to be used as part of the test run.
     * @param os Represents the operating system to be used as part of the test run.
     * @return
     * @throws MalformedURLException if an error occurs parsing the url
     */
    private WebDriver createDriver(String version, String os, String methodName) throws MalformedURLException {

        MutableCapabilities sauceOptions = new MutableCapabilities();

            sauceOptions.setCapability("username", sauce_username);
            sauceOptions.setCapability("accessKey", sauce_accesskey);
            String jobName = methodName;
            sauceOptions.setCapability("name", jobName);
//            sauceOptions.setCapability("name", jobName + " with disabled logs");
            sauceOptions.setCapability("build", buildTag);
        sauceOptions.setCapability("screenResolution", "1600x1200");
//            sauceOptions.setCapability("seleniumVersion", "3.141.59");
//            sauceOptions.setCapability("deviceName","Android GoogleAPI Emulator");
//            sauceOptions.setCapability("deviceOrientation", "portrait");
//            sauceOptions.setCapability("browserName", "Chrome");
//            sauceOptions.setCapability("platformVersion", "10.0");
//            sauceOptions.setCapability("platformName","Android");

//        InternetExplorerOptions ieOptions = new InternetExplorerOptions();
//            ieOptions.setCapability("platformName", os);
////            ieOptions.setCapability("w3c", true);
//            ieOptions.setCapability("sauce:options", sauceOptions);


        ChromeOptions browserOptions = new ChromeOptions();
            browserOptions.setExperimentalOption("w3c", true);
            browserOptions.setCapability("platformName", os);
            browserOptions.setCapability("browserVersion", version);
            browserOptions.setCapability("sauce:options", sauceOptions);

//            the following three lines are required for changing location in Chrome
//            Map prefs = new HashMap<String, Object>();
//            prefs.put("profile.default_content_setting_values.geolocation", 1);
//            browserOptions.setExperimentalOption("prefs", prefs);

//        DesiredCapabilities caps = DesiredCapabilities.android();
//        caps.setCapability("username", sauce_username);
//        caps.setCapability("accessKey", sauce_accesskey);
//        String jobName = methodName;
//        caps.setCapability("name", jobName);
//        caps.setCapability("appiumVersion", "1.18.1");
//        caps.setCapability("deviceName","Google Pixel 3a XL GoogleAPI Emulator");
//        caps.setCapability("deviceOrientation", "portrait");
//        caps.setCapability("browserName", "Chrome");
//        caps.setCapability("platformVersion", "11.0");
//        caps.setCapability("platformName","Android");



        //Creates Selenium Driver
//        webDriver.set(new RemoteWebDriver(new URL("http://ondemand.us-west-1.saucelabs.com/wd/hub"), ieOptions));
        webDriver.set(new RemoteWebDriver(new URL("http://ondemand.us-west-1.saucelabs.com/wd/hub"), browserOptions));


        //Keeps track of the unique Selenium session ID used to identify jobs on Sauce Labs
        String id = ((RemoteWebDriver) getWebDriver()).getSessionId().toString();
        sessionId.set(id);
        
        //For CI plugins
        String message = String.format("SauceOnDemandSessionID=%1$s job-name=%2$s", id, jobName);
        System.out.println(message);

        return webDriver.get();
    }

    /**
     * Runs a simple script to show changing location in Chrome.
     *
     * @param version Represents the version of the browser to be used as part of the test run.
     * @param os Represents the operating system to be used as part of the test run.
     * @param Method Represents the method, used for getting the name of the test/method
     * @throws Exception if an error occurs during the running of the test
     */

//    @Test(dataProvider = "hardCodedBrowsers")
//    public void geoTest(String version, String os, Method method) throws Exception {
//
//        WebDriver driver = createDriver(version, os, method.getName());
//
//        RemoteExecuteMethod rem = new RemoteExecuteMethod((RemoteWebDriver) driver);
//        RemoteLocationContext rlc = new RemoteLocationContext(rem);
//        rlc.setLocation(new Location(51.5007, -0.1246, 0));
//
//        ((JavascriptExecutor)driver).executeScript("sauce: disable log");
//        ((JavascriptExecutor)driver).executeScript("sauce:context=Change location to Big Ben in London");
//
//        driver.get("https://www.where-am-i.net/"); //just to double check that the location was changed
//        waitForPageLoaded(driver);
//        ((RemoteWebDriver) driver).getScreenshotAs(OutputType.BASE64);
//
//        ((JavascriptExecutor)driver).executeScript("sauce:context=Check price in London");
//        driver.get("https://groceries.asda.com/product/gin/bombay-london-dry-gin/910002046391");
//        waitForPageLoaded(driver);
//        ((RemoteWebDriver) driver).getScreenshotAs(OutputType.BASE64);
//
//        ((JavascriptExecutor)driver).executeScript("sauce:context=Change Location to Tokyo Tower");
//        rlc.setLocation(new Location(35.6586, 139.7454, 0));
//        driver.get("https://www.where-am-i.net/"); //again, just double checking the location was changed
//        waitForPageLoaded(driver);
//        ((RemoteWebDriver) driver).getScreenshotAs(OutputType.BASE64);
//
//        ((JavascriptExecutor)driver).executeScript("sauce: enable log");
//        ((JavascriptExecutor)driver).executeScript("sauce:context=Check price in Tokyo");
//        driver.get("https://groceries.asda.com/product/gin/bombay-london-dry-gin/910002046391");
//        waitForPageLoaded(driver);
//        ((RemoteWebDriver) driver).getScreenshotAs(OutputType.BASE64);

        //there's no real assert here - this is just showing how to change the location inside of Chrome
//    }

//    @Test(dataProvider = "hardCodedBrowsers")
//    public void loginTest(String version, String os, Method method) throws Exception {
//        WebDriver driver = createDriver(version, os, method.getName());
//        driver.get("https://www.saucedemo.com/");
//        driver.findElement(By.xpath("//input[@placeholder='Username']")).sendKeys("performance_glitch_user");
//        driver.findElement(By.xpath("//input[@id='password']")).sendKeys("secret_sauce");
//        driver.findElement(By.xpath("//input[@class='btn_action']")).click();
//        ((RemoteWebDriver) driver).getScreenshotAs(OutputType.BASE64);
////        driver.wait(500);
//
//        Assert.assertTrue(driver.getCurrentUrl().contains("inventory"));
////        AssertJUnit.assertTrue(driver.getCurrentUrl().contains("inventory"));
//    }

    @Test(dataProvider = "hardCodedBrowsers")
    public void loginTestSalesforce0(String version, String os, Method method) throws Exception {
        WebDriver driver = createDriver(version, os, method.getName());
        driver.get("https://test.salesforce.com/");
        driver.findElement(By.xpath("//input[@id='username']")).sendKeys("alex.griffen@saucelabs.com");
        ((JavascriptExecutor)driver).executeScript("sauce:context=disabling logs for password entry");
        ((JavascriptExecutor)driver).executeScript("sauce: disable log");
        driver.findElement(By.xpath("//input[@id='password']")).sendKeys("badpassword");
        ((JavascriptExecutor)driver).executeScript("sauce: enable log");
        ((JavascriptExecutor)driver).executeScript("sauce:context=enabling logs after password entry");
        driver.findElement(By.xpath("//*[@id=\"Login\"]")).click();
        ((RemoteWebDriver) driver).getScreenshotAs(OutputType.BASE64);
//        driver.wait(500);

        Assert.assertTrue(driver.getCurrentUrl().contains("test.salesforce.com"));
//        AssertJUnit.assertTrue(driver.getCurrentUrl().contains("inventory"));
    }

    @Test(dataProvider = "hardCodedBrowsers")
    public void loginTestSalesforce1(String version, String os, Method method) throws Exception {
        WebDriver driver = createDriver(version, os, method.getName());
        driver.get("https://test.salesforce.com/");
        driver.findElement(By.xpath("//input[@id='username']")).sendKeys("alex.griffen@saucelabs.com");
        ((JavascriptExecutor)driver).executeScript("sauce:context=disabling logs for password entry");
        ((JavascriptExecutor)driver).executeScript("sauce: disable log");
        driver.findElement(By.xpath("//input[@id='password']")).sendKeys("badpassword");
        ((JavascriptExecutor)driver).executeScript("sauce: enable log");
        ((JavascriptExecutor)driver).executeScript("sauce:context=enabling logs after password entry");
        driver.findElement(By.xpath("//*[@id=\"Login\"]")).click();
        ((RemoteWebDriver) driver).getScreenshotAs(OutputType.BASE64);
//        driver.wait(500);

        Assert.assertTrue(driver.getCurrentUrl().contains("test.salesforce.com"));
//        AssertJUnit.assertTrue(driver.getCurrentUrl().contains("inventory"));
    }

    @Test(dataProvider = "hardCodedBrowsers")
    public void loginTestSalesforce2(String version, String os, Method method) throws Exception {
        WebDriver driver = createDriver(version, os, method.getName());
        driver.get("https://test.salesforce.com/");
        driver.findElement(By.xpath("//input[@id='username']")).sendKeys("alex.griffen@saucelabs.com");
        ((JavascriptExecutor)driver).executeScript("sauce:context=disabling logs for password entry");
        ((JavascriptExecutor)driver).executeScript("sauce: disable log");
        driver.findElement(By.xpath("//input[@id='password']")).sendKeys("badpassword");
        ((JavascriptExecutor)driver).executeScript("sauce: enable log");
        ((JavascriptExecutor)driver).executeScript("sauce:context=enabling logs after password entry");
        driver.findElement(By.xpath("//*[@id=\"Login\"]")).click();
        ((RemoteWebDriver) driver).getScreenshotAs(OutputType.BASE64);
//        driver.wait(500);

        Assert.assertTrue(driver.getCurrentUrl().contains("test.salesforce.com"));
//        AssertJUnit.assertTrue(driver.getCurrentUrl().contains("inventory"));
    }

    @Test(dataProvider = "hardCodedBrowsers")
    public void loginTestSalesforce3(String version, String os, Method method) throws Exception {
        WebDriver driver = createDriver(version, os, method.getName());
        driver.get("https://test.salesforce.com/");
        driver.findElement(By.xpath("//input[@id='username']")).sendKeys("alex.griffen@saucelabs.com");
        ((JavascriptExecutor)driver).executeScript("sauce:context=disabling logs for password entry");
        ((JavascriptExecutor)driver).executeScript("sauce: disable log");
        driver.findElement(By.xpath("//input[@id='password']")).sendKeys("badpassword");
        ((JavascriptExecutor)driver).executeScript("sauce: enable log");
        ((JavascriptExecutor)driver).executeScript("sauce:context=enabling logs after password entry");
        driver.findElement(By.xpath("//*[@id=\"Login\"]")).click();
        ((RemoteWebDriver) driver).getScreenshotAs(OutputType.BASE64);
//        driver.wait(500);

        Assert.assertTrue(driver.getCurrentUrl().contains("test.salesforce.com"));
//        AssertJUnit.assertTrue(driver.getCurrentUrl().contains("inventory"));
    }

    @Test(dataProvider = "hardCodedBrowsers")
    public void loginTestSalesforce4(String version, String os, Method method) throws Exception {
        WebDriver driver = createDriver(version, os, method.getName());
        driver.get("https://test.salesforce.com/");
        driver.findElement(By.xpath("//input[@id='username']")).sendKeys("alex.griffen@saucelabs.com");
        ((JavascriptExecutor)driver).executeScript("sauce:context=disabling logs for password entry");
        ((JavascriptExecutor)driver).executeScript("sauce: disable log");
        driver.findElement(By.xpath("//input[@id='password']")).sendKeys("badpassword");
        ((JavascriptExecutor)driver).executeScript("sauce: enable log");
        ((JavascriptExecutor)driver).executeScript("sauce:context=enabling logs after password entry");
        driver.findElement(By.xpath("//*[@id=\"Login\"]")).click();
        ((RemoteWebDriver) driver).getScreenshotAs(OutputType.BASE64);
//        driver.wait(500);

        Assert.assertTrue(driver.getCurrentUrl().contains("test.salesforce.com"));
//        AssertJUnit.assertTrue(driver.getCurrentUrl().contains("inventory"));
    }

    @Test(dataProvider = "hardCodedBrowsers")
    public void loginTestSalesforce5(String version, String os, Method method) throws Exception {
        WebDriver driver = createDriver(version, os, method.getName());
        driver.get("https://test.salesforce.com/");
        driver.findElement(By.xpath("//input[@id='username']")).sendKeys("alex.griffen@saucelabs.com");
        ((JavascriptExecutor)driver).executeScript("sauce:context=disabling logs for password entry");
        ((JavascriptExecutor)driver).executeScript("sauce: disable log");
        driver.findElement(By.xpath("//input[@id='password']")).sendKeys("badpassword");
        ((JavascriptExecutor)driver).executeScript("sauce: enable log");
        ((JavascriptExecutor)driver).executeScript("sauce:context=enabling logs after password entry");
        driver.findElement(By.xpath("//*[@id=\"Login\"]")).click();
        ((RemoteWebDriver) driver).getScreenshotAs(OutputType.BASE64);
//        driver.wait(500);

        Assert.assertTrue(driver.getCurrentUrl().contains("test.salesforce.com"));
//        AssertJUnit.assertTrue(driver.getCurrentUrl().contains("inventory"));
    }

    @Test(dataProvider = "hardCodedBrowsers")
    public void loginTestSalesforce6(String version, String os, Method method) throws Exception {
        WebDriver driver = createDriver(version, os, method.getName());
        driver.get("https://test.salesforce.com/");
        driver.findElement(By.xpath("//input[@id='username']")).sendKeys("alex.griffen@saucelabs.com");
        ((JavascriptExecutor)driver).executeScript("sauce:context=disabling logs for password entry");
        ((JavascriptExecutor)driver).executeScript("sauce: disable log");
        driver.findElement(By.xpath("//input[@id='password']")).sendKeys("badpassword");
        ((JavascriptExecutor)driver).executeScript("sauce: enable log");
        ((JavascriptExecutor)driver).executeScript("sauce:context=enabling logs after password entry");
        driver.findElement(By.xpath("//*[@id=\"Login\"]")).click();
        ((RemoteWebDriver) driver).getScreenshotAs(OutputType.BASE64);
//        driver.wait(500);

        Assert.assertTrue(driver.getCurrentUrl().contains("test.salesforce.com"));
//        AssertJUnit.assertTrue(driver.getCurrentUrl().contains("inventory"));
    }

    @Test(dataProvider = "hardCodedBrowsers")
    public void loginTestSalesforce7(String version, String os, Method method) throws Exception {
        WebDriver driver = createDriver(version, os, method.getName());
        driver.get("https://test.salesforce.com/");
        driver.findElement(By.xpath("//input[@id='username']")).sendKeys("alex.griffen@saucelabs.com");
        ((JavascriptExecutor)driver).executeScript("sauce:context=disabling logs for password entry");
        ((JavascriptExecutor)driver).executeScript("sauce: disable log");
        driver.findElement(By.xpath("//input[@id='password']")).sendKeys("badpassword");
        ((JavascriptExecutor)driver).executeScript("sauce: enable log");
        ((JavascriptExecutor)driver).executeScript("sauce:context=enabling logs after password entry");
        driver.findElement(By.xpath("//*[@id=\"Login\"]")).click();
        ((RemoteWebDriver) driver).getScreenshotAs(OutputType.BASE64);
//        driver.wait(500);

        Assert.assertTrue(driver.getCurrentUrl().contains("test.salesforce.com"));
//        AssertJUnit.assertTrue(driver.getCurrentUrl().contains("inventory"));
    }

    @Test(dataProvider = "hardCodedBrowsers")
    public void loginTestSalesforce8(String version, String os, Method method) throws Exception {
        WebDriver driver = createDriver(version, os, method.getName());
        driver.get("https://test.salesforce.com/");
        driver.findElement(By.xpath("//input[@id='username']")).sendKeys("alex.griffen@saucelabs.com");
        ((JavascriptExecutor)driver).executeScript("sauce:context=disabling logs for password entry");
        ((JavascriptExecutor)driver).executeScript("sauce: disable log");
        driver.findElement(By.xpath("//input[@id='password']")).sendKeys("badpassword");
        ((JavascriptExecutor)driver).executeScript("sauce: enable log");
        ((JavascriptExecutor)driver).executeScript("sauce:context=enabling logs after password entry");
        driver.findElement(By.xpath("//*[@id=\"Login\"]")).click();
        ((RemoteWebDriver) driver).getScreenshotAs(OutputType.BASE64);
//        driver.wait(500);

        Assert.assertTrue(driver.getCurrentUrl().contains("test.salesforce.com"));
//        AssertJUnit.assertTrue(driver.getCurrentUrl().contains("inventory"));
    }

    @Test(dataProvider = "hardCodedBrowsers")
    public void loginTestSalesforce9(String version, String os, Method method) throws Exception {
        WebDriver driver = createDriver(version, os, method.getName());
        driver.get("https://test.salesforce.com/");
        driver.findElement(By.xpath("//input[@id='username']")).sendKeys("alex.griffen@saucelabs.com");
        ((JavascriptExecutor)driver).executeScript("sauce:context=disabling logs for password entry");
        ((JavascriptExecutor)driver).executeScript("sauce: disable log");
        driver.findElement(By.xpath("//input[@id='password']")).sendKeys("badpassword");
        ((JavascriptExecutor)driver).executeScript("sauce: enable log");
        ((JavascriptExecutor)driver).executeScript("sauce:context=enabling logs after password entry");
        driver.findElement(By.xpath("//*[@id=\"Login\"]")).click();
        ((RemoteWebDriver) driver).getScreenshotAs(OutputType.BASE64);
//        driver.wait(500);

        Assert.assertTrue(driver.getCurrentUrl().contains("test.salesforce.com"));
//        AssertJUnit.assertTrue(driver.getCurrentUrl().contains("inventory"));
    }

    @Test(dataProvider = "hardCodedBrowsers")
    public void loginTestSalesforce10(String version, String os, Method method) throws Exception {
        WebDriver driver = createDriver(version, os, method.getName());
        driver.get("https://test.salesforce.com/");
        driver.findElement(By.xpath("//input[@id='username']")).sendKeys("alex.griffen@saucelabs.com");
        ((JavascriptExecutor)driver).executeScript("sauce:context=disabling logs for password entry");
        ((JavascriptExecutor)driver).executeScript("sauce: disable log");
        driver.findElement(By.xpath("//input[@id='password']")).sendKeys("badpassword");
        ((JavascriptExecutor)driver).executeScript("sauce: enable log");
        ((JavascriptExecutor)driver).executeScript("sauce:context=enabling logs after password entry");
        driver.findElement(By.xpath("//*[@id=\"Login\"]")).click();
        ((RemoteWebDriver) driver).getScreenshotAs(OutputType.BASE64);
//        driver.wait(500);

        Assert.assertTrue(driver.getCurrentUrl().contains("test.salesforce.com"));
//        AssertJUnit.assertTrue(driver.getCurrentUrl().contains("inventory"));
    }

    @Test(dataProvider = "hardCodedBrowsers")
    public void loginTestSalesforce11(String version, String os, Method method) throws Exception {
        WebDriver driver = createDriver(version, os, method.getName());
        driver.get("https://test.salesforce.com/");
        driver.findElement(By.xpath("//input[@id='username']")).sendKeys("alex.griffen@saucelabs.com");
        ((JavascriptExecutor)driver).executeScript("sauce:context=disabling logs for password entry");
        ((JavascriptExecutor)driver).executeScript("sauce: disable log");
        driver.findElement(By.xpath("//input[@id='password']")).sendKeys("badpassword");
        ((JavascriptExecutor)driver).executeScript("sauce: enable log");
        ((JavascriptExecutor)driver).executeScript("sauce:context=enabling logs after password entry");
        driver.findElement(By.xpath("//*[@id=\"Login\"]")).click();
        ((RemoteWebDriver) driver).getScreenshotAs(OutputType.BASE64);
//        driver.wait(500);

        Assert.assertTrue(driver.getCurrentUrl().contains("test.salesforce.com"));
//        AssertJUnit.assertTrue(driver.getCurrentUrl().contains("inventory"));
    }

    @Test(dataProvider = "hardCodedBrowsers")
    public void loginTestSalesforce12(String version, String os, Method method) throws Exception {
        WebDriver driver = createDriver(version, os, method.getName());
        driver.get("https://test.salesforce.com/");
        driver.findElement(By.xpath("//input[@id='username']")).sendKeys("alex.griffen@saucelabs.com");
        ((JavascriptExecutor)driver).executeScript("sauce:context=disabling logs for password entry");
        ((JavascriptExecutor)driver).executeScript("sauce: disable log");
        driver.findElement(By.xpath("//input[@id='password']")).sendKeys("badpassword");
        ((JavascriptExecutor)driver).executeScript("sauce: enable log");
        ((JavascriptExecutor)driver).executeScript("sauce:context=enabling logs after password entry");
        driver.findElement(By.xpath("//*[@id=\"Login\"]")).click();
        ((RemoteWebDriver) driver).getScreenshotAs(OutputType.BASE64);
//        driver.wait(500);

        Assert.assertTrue(driver.getCurrentUrl().contains("test.salesforce.com"));
//        AssertJUnit.assertTrue(driver.getCurrentUrl().contains("inventory"));
    }

    @Test(dataProvider = "hardCodedBrowsers")
    public void loginTestSalesforce13(String version, String os, Method method) throws Exception {
        WebDriver driver = createDriver(version, os, method.getName());
        driver.get("https://test.salesforce.com/");
        driver.findElement(By.xpath("//input[@id='username']")).sendKeys("alex.griffen@saucelabs.com");
        ((JavascriptExecutor)driver).executeScript("sauce:context=disabling logs for password entry");
        ((JavascriptExecutor)driver).executeScript("sauce: disable log");
        driver.findElement(By.xpath("//input[@id='password']")).sendKeys("badpassword");
        ((JavascriptExecutor)driver).executeScript("sauce: enable log");
        ((JavascriptExecutor)driver).executeScript("sauce:context=enabling logs after password entry");
        driver.findElement(By.xpath("//*[@id=\"Login\"]")).click();
        ((RemoteWebDriver) driver).getScreenshotAs(OutputType.BASE64);
//        driver.wait(500);

        Assert.assertTrue(driver.getCurrentUrl().contains("test.salesforce.com"));
//        AssertJUnit.assertTrue(driver.getCurrentUrl().contains("inventory"));
    }

    @Test(dataProvider = "hardCodedBrowsers")
    public void loginTestSalesforce14(String version, String os, Method method) throws Exception {
        WebDriver driver = createDriver(version, os, method.getName());
        driver.get("https://test.salesforce.com/");
        driver.findElement(By.xpath("//input[@id='username']")).sendKeys("alex.griffen@saucelabs.com");
        ((JavascriptExecutor)driver).executeScript("sauce:context=disabling logs for password entry");
        ((JavascriptExecutor)driver).executeScript("sauce: disable log");
        driver.findElement(By.xpath("//input[@id='password']")).sendKeys("badpassword");
        ((JavascriptExecutor)driver).executeScript("sauce: enable log");
        ((JavascriptExecutor)driver).executeScript("sauce:context=enabling logs after password entry");
        driver.findElement(By.xpath("//*[@id=\"Login\"]")).click();
        ((RemoteWebDriver) driver).getScreenshotAs(OutputType.BASE64);
//        driver.wait(500);

        Assert.assertTrue(driver.getCurrentUrl().contains("test.salesforce.com"));
//        AssertJUnit.assertTrue(driver.getCurrentUrl().contains("inventory"));
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

    public void waitForPageLoaded(WebDriver driver) {
        ExpectedCondition<Boolean> expectation = new
                ExpectedCondition<Boolean>() {
                    public Boolean apply(WebDriver driver) {
                        return ((JavascriptExecutor) driver).executeScript("return document.readyState").toString().equals("complete");
                    }
                };
        try {
            Thread.sleep(5000);
            WebDriverWait wait = new WebDriverWait(driver, 30);
            wait.until(expectation);
        } catch (Throwable error) {
            Assert.fail("Timeout waiting for Page Load Request to complete.");
        }
    }

    @AfterMethod
    public void tearDown(ITestResult result) throws Exception {
        boolean status = result.isSuccess();
        ((JavascriptExecutor)webDriver.get()).executeScript("sauce:job-result="+ status);
        webDriver.get().quit();
    }
}