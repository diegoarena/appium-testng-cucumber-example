package com.darena.automation;

import io.appium.java_client.MobileDriver;
import io.cucumber.guice.ScenarioScoped;

/**
 *
 * @author diego arena <diego88arena@gmail.com>
 *
 */
@ScenarioScoped
public class TestContext {

    private MobileDriver driver;

    public MobileDriver getDriver() {
        return driver;
    }

    public void setDriver(MobileDriver driver) {
        this.driver = driver;
    }
}
