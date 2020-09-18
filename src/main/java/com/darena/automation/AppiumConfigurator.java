package com.darena.automation;

import com.darena.automation.util.AppiumServer;
import com.darena.automation.util.CapabilityReader;
import io.appium.java_client.MobileDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;

/**
 *
 * @author diego arena <diego88arena@gmail.com>
 *
 */
public class AppiumConfigurator {

    private  MobileDriver<MobileElement> driver;

    public void setupAppium(TestContext testContext) throws Exception{
        AppiumServer.startServer();
        driver = new AndroidDriver<MobileElement>(AppiumServer.getServerUrl(), CapabilityReader.readCapabilities("src/test/resources/deviceCapabilities.json"));
        testContext.setDriver(driver);
    }

    public void stopAppium(){
        AppiumServer.stopServer();
    }

    public void closeApp(){
        getDriver().closeApp();
    }

    public void openApp(){
       getDriver().launchApp();
    }

    public  MobileDriver getDriver(){
        return driver;
    }
}
