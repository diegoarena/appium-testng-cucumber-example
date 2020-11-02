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
public class TriangleScreen {

    private static int DEFAULT_WAIT_TIME = 20;

    @AndroidFindBy(id = "com.eliasnogueira.trianguloapp:id/txtLado1")
    private MobileElement ladoUnoTextfield;
    @AndroidFindBy(id = "com.eliasnogueira.trianguloapp:id/txtLado2")
    private MobileElement ladoDosTextfield;
    @AndroidFindBy(id = "com.eliasnogueira.trianguloapp:id/txtLado3")
    private MobileElement ladoTresTextfield;
    @AndroidFindBy(id = "com.eliasnogueira.trianguloapp:id/txtResultado")
    private MobileElement triangleTypeLabel;
    @AndroidFindBy(id = "com.eliasnogueira.trianguloapp:id/btnCalcular")
    private MobileElement calcularButton;

    @Inject
    public TriangleScreen(TestContext testContext){
        PageFactory.initElements(new AppiumFieldDecorator(testContext.getDriver(), Duration.ofSeconds(DEFAULT_WAIT_TIME)), this);
    }

    public TriangleScreen enterLadoValues(Integer lado1, Integer lado2, Integer lado3){
        ladoUnoTextfield.setValue(lado1.toString());
        ladoDosTextfield.setValue(lado2.toString());
        ladoTresTextfield.setValue(lado3.toString());
        return this;
    }

    public void clickOnCalculateButton(){
        calcularButton.click();
    }

    public String getTriangleType(){
        return triangleTypeLabel.getText();
    }

}
