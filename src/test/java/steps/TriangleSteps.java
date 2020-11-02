/*
 * Copyright (c) 2020. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package steps;

import com.darena.automation.TestContext;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;
import screens.TriangleScreen;
import javax.inject.Inject;

/**
 *
 * @author diego arena <diego88arena@gmail.com>
 *
 */
public class TriangleSteps {

    @Inject
    private TestContext testContext;
    @Inject
    private TriangleScreen triangleScreen;

    @Given("The user enters lado values: {int} and {int} and {int}")
    public void theUserEntersLadoValuesLadoAndLadoAndLado(int arg0, int arg1, int arg2) {
       triangleScreen.enterLadoValues(arg0,arg1,arg2);
    }

    @When("Click on calculate button")
    public void clickOnCalculateButton() {
        triangleScreen.clickOnCalculateButton();
    }

    @Then("The triangle type shown should be {string}")
    public void theTriangleTypeShownShouldBeTriangleType(String type) {
        Assert.assertEquals(triangleScreen.getTriangleType(),type, "Invalid triangle type");
    }



}
