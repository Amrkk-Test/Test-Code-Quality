package com.triplelift.test_code_quality_tools_api.bdd;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(tags = "not @manual", plugin = { "json:docs/cucumber.json" }, features = "src/test/resources/features")
public class RunCucumberTests {
}