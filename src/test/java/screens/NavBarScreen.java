package com.darena.automation.screens;

import io.appium.java_client.MobileDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.support.PageFactory;

import java.time.Duration;

public class NavBarScreen {

    private static int DEFAULT_WAIT_TIME = 20;
    private MobileDriver driver;

    @AndroidFindBy(id = "org.wikipedia:id/drawer_icon_layout")
    public MobileElement drawerButtonMenu;
    @AndroidFindBy(id = "org.wikipedia:id/fragment_onboarding_skip_button")
    public MobileElement skipButton;
    @AndroidFindBy(id = "org.wikipedia:id/main_drawer_login_button")
    public MobileElement loginButton;

    public NavBarScreen(MobileDriver driver){
        this.driver = driver;
        PageFactory.initElements(new AppiumFieldDecorator(driver, Duration.ofSeconds(DEFAULT_WAIT_TIME)), this);
    }

    public NavBarScreen navigateToLoginScreen(){
        if(skipButton.isDisplayed()){
            skipButton.click();
        }
        drawerButtonMenu.click();
        loginButton.click();
        return this;
    }

}
