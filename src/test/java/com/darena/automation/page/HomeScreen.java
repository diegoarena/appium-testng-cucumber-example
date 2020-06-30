/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.darena.automation.page;

import io.appium.java_client.MobileDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.support.PageFactory;
import java.time.Duration;

public class HomeScreen {

    private static int DEFAULT_WAIT_TIME = 20;
    private MobileDriver driver;

    @AndroidFindBy(id = "org.wikipedia:id/drawer_icon_layout")
    public MobileElement drawerButtonMenu;
    @AndroidFindBy(id = "org.wikipedia:id/fragment_onboarding_skip_button")
    public MobileElement skipButton;

    public HomeScreen(MobileDriver driver){
        this.driver = driver;
        PageFactory.initElements(new AppiumFieldDecorator(driver, Duration.ofSeconds(DEFAULT_WAIT_TIME)), this);
    }

    public HomeScreen clickDrawerButtonMenu(){
        if(skipButton.isDisplayed()){
            skipButton.click();
        }
        drawerButtonMenu.click();
        return this;
    }
}
