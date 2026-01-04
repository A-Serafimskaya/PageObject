package ru.netology.web.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.Keys;
import ru.netology.web.data.DataHelper;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class Transfer {

    private final SelenideElement amountField = $("[data-test-id='amount'] input");
    private final SelenideElement transferFromField = $("[data-test-id=from] input");
    private final SelenideElement buttonToTransfer = $("[data-test-id=action-transfer]");
    public static SelenideElement errorMessage = $("[data-test-id=error-notification]");

    public Transfer() {
        amountField.should(Condition.visible, Duration.ofSeconds(15));
    }

    public DashBoardPage validTransfer(int amount, DataHelper.CardInfo secondCard) {
        transfer(amount, secondCard);
        return new DashBoardPage();
    }

    public void transfer(int amount, DataHelper.CardInfo fromCard) {
        amountField.setValue(String.valueOf(amount));
        transferFromField.setValue(String.valueOf(fromCard.getNumber()));
        buttonToTransfer.click();


    }


    public void checkErrorMessage(String errorText) {
        errorMessage.should(Condition.visible).shouldHave(Condition.text(errorText));
    }

    public void transfer(String s, DataHelper.CardInfo fromCard) {
    }

}
