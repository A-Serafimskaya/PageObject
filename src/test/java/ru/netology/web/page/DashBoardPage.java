package ru.netology.web.page;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import ru.netology.web.data.DataHelper;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class DashBoardPage {

    private final SelenideElement header = $("[data-test-id=dashboard]");
    // к сожалению, разработчики не дали нам удобного селектора, поэтому так
    private ElementsCollection cards = $$(".list__item div");
    private final String balanceStart = "баланс: ";
    private final String balanceFinish = " р.";


    public DashBoardPage() {
        header.should(Condition.visible);


    }

    public int getFirstCardBalance() {
        var text = cards.first().text();
        return extractBalance(text);
    }

    private int extractBalance(String text) {
        var start = text.indexOf(balanceStart);
        var finish = text.indexOf(balanceFinish);
        var value = text.substring(start + balanceStart.length(), finish);
        return Integer.parseInt(value);
    }


    public int getCardBalance(String id) {
        for (SelenideElement card : cards) {
            String cardId = card.getAttribute("data-test-id");
            if (id.equals(cardId)) {
                String text = card.text();
                return extractBalance(text);
            }
        }
        throw new RuntimeException("Карта с data-test-id = " + id + " не найдена");
    }

    public Transfer cardSelection(String id) {
        for (SelenideElement card : cards) {
            String cardId = card.getAttribute("data-test-id");
            if (id.equals(cardId)) {
                card.$("[data-test-id=action-deposit]").click();
                return new Transfer();
            }

        }

        throw new RuntimeException("Карта с id " + id + " не найдена");

    }

    public void renewCardBalances() {
        $("[data-test-id=action-reload]").click();
    }


}






