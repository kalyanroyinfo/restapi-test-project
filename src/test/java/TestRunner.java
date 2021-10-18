import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/resources/feature",
        glue = {"com.learming.kr.stepDefinitions"},
        monochrome = true,
        strict = true
)
public class TestRunner {
}