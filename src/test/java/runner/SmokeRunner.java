
package runner;

import com.darena.automation.TestContext;
import com.google.inject.Inject;
import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import io.cucumber.java.Scenario;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 *
 * @author diego arena <diego88arena@gmail.com>
 *
 */

@CucumberOptions(
        features = {"src/test/java/features/"},
        //plugin = {"com.darena.automation.plugins.ReportPortalPlugin:src/test/resources/reportportal.properties"},
        plugin = {"json:target/cucumber-report/cucumber.json"},
        glue = {"steps"},
        tags = {"@smoke"},
        strict = true
)

public class SmokeRunner extends AbstractTestNGCucumberTests {

    @Inject
    private TestContext testContext;

    @Parameters({ "configFile" })
    @BeforeTest
    public void setUpScenario(String configFile) {
        System.out.println("->>>>>>>>>>THREAD ID"+Thread.currentThread().getId());
        //testContext.setConfigurationFile(configFile);
        //BaseSteps.getInstance().getBrowserInstantiation(browser);
    }

    @Override
    @DataProvider(parallel = false)
    public Object[][] scenarios() {
        return super.scenarios();
    }


}
