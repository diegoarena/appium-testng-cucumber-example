
package runner;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import io.cucumber.java.Scenario;

/**
 *
 * @author diego arena <diego88arena@gmail.com>
 *
 */

@CucumberOptions(
        features = {"src/test/java/features/"},
        plugin = {"com.darena.automation.plugins.ReportPortalPlugin:src/test/resources/reportportal.properties"},
        glue = {"steps"},
        tags = {"@smoke"},
        strict = true
)
public class SmokeRunner extends AbstractTestNGCucumberTests {
}
