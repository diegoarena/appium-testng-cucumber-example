package screens;

import com.darena.automation.TestContext;
import com.google.inject.Inject;
import io.appium.java_client.MobileElement;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.support.PageFactory;
import java.time.Duration;

/**
 *
 * @author diego arena <diego88arena@gmail.com>
 *
 */
public class LoginScreen {

    private static int DEFAULT_WAIT_TIME = 20;

    @AndroidFindBy (xpath = "//*[@text='Username']")
    private MobileElement userTextField;
    @AndroidFindBy (xpath = "//*[@text='Password']")
    private MobileElement passwordField;
    @AndroidFindBy (id = "org.wikipedia:id/login_button")
    private MobileElement loginButton;
    @AndroidFindBy (id = "org.wikipedia:id/snackbar_text")
    private MobileElement loginResultTextView;

    public MobileElement getPageTitle() {
        return pageTitle;
    }

    @AndroidFindBy (xpath = "//*[@text='Log in to Wikipedia']")
    private MobileElement pageTitle;

    @Inject
    public LoginScreen(TestContext testContext) {
        PageFactory.initElements(new AppiumFieldDecorator(testContext.getDriver(), Duration.ofSeconds(DEFAULT_WAIT_TIME)), this);
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

    public MobileElement getUserTextField(){
        return userTextField;
    }
}
