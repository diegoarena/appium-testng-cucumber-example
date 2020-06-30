package com.darena.automation.page;

import io.appium.java_client.MobileDriver;
import io.appium.java_client.MobileElement;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.support.PageFactory;

import java.time.Duration;

public class LoginScreen {

    private static int DEFAULT_WAIT_TIME = 20;
    private MobileDriver driver;

    @AndroidFindBy (xpath = "//*[@text='Username']")
    public MobileElement userTextField;
    @AndroidFindBy (xpath = "//*[@text='Password']")
    public MobileElement passwordField;
    @AndroidFindBy (id = "org.wikipedia:id/login_button")
    public MobileElement loginButton;
    @AndroidFindBy (id = "org.wikipedia:id/snackbar_text")
    public MobileElement loginResultTextView;

    public LoginScreen(MobileDriver driver) {
        this.driver = driver;
        PageFactory.initElements(new AppiumFieldDecorator(driver, Duration.ofSeconds(DEFAULT_WAIT_TIME)), this);
    }

    public LoginScreen login(String user, String password){
        userTextField.sendKeys(user);
        passwordField.sendKeys(password);
        loginButton.click();
        return this;
    }

    public MobileElement getLoginResultTextView(){
        return loginResultTextView;
    }



}
