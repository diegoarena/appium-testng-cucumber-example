package steps;

import com.darena.automation.AppiumConfigurator;
import com.darena.automation.Const;
import com.darena.automation.TestContext;
import com.google.inject.Inject;
import io.cucumber.java.After;
import io.cucumber.java.AfterStep;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import java.io.File;

/**
 *
 * @author diego arena <diego88arena@gmail.com>
 *
 */
public class Hook {

    @Inject
    private TestContext testContext;

    @Before
    public void configureAppium() throws Exception{
        new AppiumConfigurator().setupAppium(testContext);
    }

    @AfterStep()
    public void takeScreenshot(Scenario scenario) throws Exception{
        // Take a screenshot...
       if (scenario.isFailed()) {
            String featureFileName = new File(scenario.getUri().getPath()).getName();
            byte[] screenshot = ((TakesScreenshot) testContext.getDriver()).getScreenshotAs(OutputType.BYTES);
            scenario.embed(screenshot,"image/png",scenario.getName()+".png");
       }
    }

}
