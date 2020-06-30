package com.darena.automation;

import com.darena.automation.util.AppiumServer;
import com.darena.automation.util.CapabilityReader;
import io.appium.java_client.MobileDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import org.aspectj.lang.annotation.Before;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import java.util.concurrent.TimeUnit;

public abstract class BaseTest {

    public MobileDriver<MobileElement> driver;

    @BeforeSuite
    public void setupAppium() throws Exception{
        AppiumServer.startServer();
        driver = new AndroidDriver<MobileElement>(AppiumServer.getServerUrl(), CapabilityReader.readCapabilities("src/test/resources/deviceCapabilities.json"));
        driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
    }

    @AfterSuite
    public void stopAppium(){
        AppiumServer.stopServer();
    }

    @AfterMethod
    public void closeApp(){
        getDriver().closeApp();
    }

    @BeforeMethod
    public void openApp(){
        getDriver().launchApp();
    }

    public MobileDriver getDriver(){
        return driver;
    }
}
