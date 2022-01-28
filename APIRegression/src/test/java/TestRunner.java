import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

/**
 * The API_R1.2 Test runner
 */
//@RunWith(Cucumber.class)
// This is initiator file for autoation testing
@CucumberOptions(
        features = "features",
        glue = {"com.napt.tbi.projects.ActiveOmni"},
        tags = {"@project_ao_sta_r12"},
        plugin = {"pretty", "html:target/cucumber_target.html", "json:target/cucumber.json"})
public class TestRunner extends AbstractTestNGCucumberTests {

}

