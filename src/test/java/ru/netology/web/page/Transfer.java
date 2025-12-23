package ru.netology.web.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.Keys;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class Transfer {

    private final SelenideElement amountField = $("[data-test-id=amount] input");
    private final SelenideElement transferFromField = $("[data-test-id=from] input");
    private final SelenideElement buttonToTransfer = $("[data-test-id=action-transfer]");
    private final SelenideElement errorMessage = $("[data-test-id=error-notification]");

    public Transfer() {
        amountField.should(Condition.visible);
    }

    public DashBoardPage validTransfer(String amount, String id) {
        transfer(amount, id);
        return new DashBoardPage();
    }

    public void transfer(String amount, String id) {
        amountField.setValue(amount);
        transferFromField.setValue(id);
        buttonToTransfer.click();


    }


    public String checkErrorMessage(String errorText) {
        errorMessage.should(Condition.visible).shouldHave(Condition.text(errorText));

        return errorMessage.getText();
    }

    public String getErrorMessage() {
        return errorMessage.getText();
    }

}
