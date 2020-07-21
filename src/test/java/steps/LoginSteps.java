package steps;

import com.darena.automation.TestContext;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;
import screens.LoginScreen;
import screens.NavBarScreen;
import javax.inject.Inject;

/**
 *
 * @author diego arena <diego88arena@gmail.com>
 *
 */
public class LoginSteps {

   @Inject
    private TestContext testContext;
    @Inject
    private LoginScreen loginScreen;

    @Given("A user with invalid credentials")
    public void a_user_with_invalid_credentials() throws Exception{
        System.out.println("code");
    }

    @When("I try to login")
    public void I_try_to_login() {
       new NavBarScreen(testContext).navigateToLoginScreen();
        loginScreen.login("diego","arena");
    }

    @Then("Screen shows an error message")
    public void screen_shows_an_error_message(String message) {
        Assert.assertEquals(loginScreen.getLoginResultTextView().getText(),message);
    }

    @And("I stay in the login screen")
    public void I_stay_in_the_login_screen() {
        Assert.assertTrue(loginScreen.getPageTitle().isDisplayed());

    }



}
