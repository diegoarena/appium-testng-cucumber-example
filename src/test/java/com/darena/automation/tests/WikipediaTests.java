package com.darena.automation.tests;

import com.darena.automation.BaseTest;
import com.darena.automation.page.HomeScreen;
import com.darena.automation.page.LoginScreen;
import com.darena.automation.page.NavBarScreen;
import org.testng.Assert;
import org.testng.annotations.Test;

public class WikipediaTests extends BaseTest {

    @Test
    public void successfullLogin(){
        new NavBarScreen(this.getDriver()).navigateToLoginScreen();
        LoginScreen login = new LoginScreen(this.getDriver()).login("diego","arena");
        Assert.assertEquals(login.getLoginResultTextView().getText(),"Incorrect username or password entered.\n" +
                "Please try again.");
    }

    @Test
    public void invalidCredentials(){
        new NavBarScreen(this.getDriver()).navigateToLoginScreen();
        LoginScreen login = new LoginScreen(this.getDriver()).login("diego","arena");
        Assert.assertEquals(login.getLoginResultTextView().getText(),"Incorrect username or password entered.\n" +
                "Please try again.");
    }

}
