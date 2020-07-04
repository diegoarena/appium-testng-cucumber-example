package screens;

import com.darena.automation.TestContext;
import io.appium.java_client.MobileElement;
import io.appium.java_client.pagefactory.AndroidFindBy;
import io.appium.java_client.pagefactory.AppiumFieldDecorator;
import org.openqa.selenium.support.PageFactory;
import javax.inject.Inject;
import java.time.Duration;

/**
 *
 * @author diego arena <diego88arena@gmail.com>
 *
 */
public class HomeScreen {

    private static int DEFAULT_WAIT_TIME = 20;

    @AndroidFindBy(id = "org.wikipedia:id/drawer_icon_layout")
    private MobileElement drawerButtonMenu;
    @AndroidFindBy(id = "org.wikipedia:id/fragment_onboarding_skip_button")
    private MobileElement skipButton;

    @Inject
    public HomeScreen(TestContext testContext){
        PageFactory.initElements(new AppiumFieldDecorator(testContext.getDriver(), Duration.ofSeconds(DEFAULT_WAIT_TIME)), this);
    }

    public HomeScreen clickDrawerButtonMenu(){
        if(skipButton.isDisplayed()){
            skipButton.click();
        }
        drawerButtonMenu.click();
        return this;
    }
}
