package ru.netology.web.test;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.web.data.DataHelper;
import ru.netology.web.page.DashBoardPage;
import ru.netology.web.page.LoginPageV2;


import java.time.Duration;
import java.util.Random;

import static com.codeborne.selenide.Selenide.$;
import static org.junit.jupiter.api.Assertions.*;
import static ru.netology.web.data.DataHelper.getAuthInfo;

public class MoneyTransferTest {
    private LoginPageV2 loginPage;

    @BeforeEach
    void setup() {
        loginPage = Selenide.open("http://localhost:9999", LoginPageV2.class);
    }

    @Test
    void shouldTransferMoneyBetweenOwnCardsFromSecond() {
        var toCard = DataHelper.getFirstCardInfo(); // карта куда
        var fromCard = DataHelper.getSecondCardInfo(); // карта откуда

        var info = getAuthInfo(); // получение инфо авторизации
        var verificationCode = DataHelper.getVerificationCodeFor(info); // получение кода подтверждения
        var verificationPage = loginPage.validLogin(info); // выполнение входа
        var dashBoardPage = verificationPage.validVerify(verificationCode); // ввод кода подтверждения и переход на дашборд


        int initialBalanceFirstCard = dashBoardPage.getCardBalance(toCard.getTestId()); // проверяем балансы перед операцией
        int initialBalanceSecondCard = dashBoardPage.getCardBalance(fromCard.getTestId());

        if (initialBalanceFirstCard <= 0) {
            dashBoardPage.renewCardBalances();
        }

        int amount = new Random().nextInt(dashBoardPage.getCardBalance(toCard.getTestId())); // генерируем сумму пополнения в рамках баланса второй карты до перевода
        var transfer = dashBoardPage.cardSelection(toCard.getTestId()); // создали объект трансфер, выбрали карту
        transfer.transfer(String.valueOf(amount), fromCard.getTestId()); // вызвали метод трансфер; перевели деньги

        var expectedFromCardBalanceAfterTransfer = initialBalanceSecondCard + amount;// ожидаемый баланс после перевода
        var expectedToCardBalanceAfterTransfer = initialBalanceFirstCard - amount;

        assertEquals(initialBalanceSecondCard + amount, expectedFromCardBalanceAfterTransfer);
        assertEquals(initialBalanceFirstCard - amount, expectedToCardBalanceAfterTransfer);

    }


    @Test
    void shouldTransferMoneyBetweenOwnCardsFromFirst() {
        var toCard = DataHelper.getSecondCardInfo(); // карта куда
        var fromCard = DataHelper.getFirstCardInfo(); // карта откуда

        var info = getAuthInfo(); // получение инфо авторизации
        var verificationCode = DataHelper.getVerificationCodeFor(info); // получение кода подтверждения
        var verificationPage = loginPage.validLogin(info); // выполнение входа
        var dashBoardPage = verificationPage.validVerify(verificationCode); // ввод кода подтверждения и переход на дашборд


        int initialBalanceFirstCard = dashBoardPage.getCardBalance(toCard.getTestId()); // проверяем баланс
        int initialBalanceSecondCard = dashBoardPage.getCardBalance(fromCard.getTestId());

        if (initialBalanceSecondCard <= 0) {
            dashBoardPage.renewCardBalances();
        }

        int amount = new Random().nextInt(dashBoardPage.getCardBalance(toCard.getTestId())); // генерируем сумму пополнения, не превышающую баланс 2 карты
        var transfer = dashBoardPage.cardSelection(toCard.getTestId()); // создали объект трансфер, выбрали карту
        transfer.transfer(String.valueOf(amount), fromCard.getTestId()); // вызвали метод трансфер; перевели деньги

        var expectedFromCardBalanceAfterTransfer = initialBalanceFirstCard + amount;// рассчитываем ожидаемый баланс первой карты после перевода
        var expectedToCardBalanceAfterTransfer = initialBalanceSecondCard - amount;// рассчитываем ожидаемый баланс второй карты после перевода

        assertEquals(initialBalanceFirstCard + amount, expectedFromCardBalanceAfterTransfer);
        assertEquals(initialBalanceSecondCard - amount, expectedToCardBalanceAfterTransfer);

    }

    @Test
    void shouldNotTransferMoneyBetweenOwnCardsOverBalance() {

        String expectedMessage = "Ошибка!";

        var toCard = DataHelper.getFirstCardInfo(); // карта куда
        var fromCard = DataHelper.getSecondCardInfo(); // карта откуда

        var info = getAuthInfo(); // получение инфо авторизации
        var verificationCode = DataHelper.getVerificationCodeFor(info); // получение кода подтверждения
        var verificationPage = loginPage.validLogin(info); // выполнение входа
        var dashBoardPage = verificationPage.validVerify(verificationCode); // ввод кода подтверждения и переход на дашборд


        int initialBalanceFirstCard = dashBoardPage.getCardBalance(toCard.getTestId());
        int initialBalanceSecondCard = dashBoardPage.getCardBalance(fromCard.getTestId());    // проверяем баланс перед операцией
        if (initialBalanceSecondCard <= 0) {
            dashBoardPage.renewCardBalances();
        }

        int fromCardBalance = dashBoardPage.getCardBalance(fromCard.getTestId());
        int maxLimit = fromCardBalance + 1000; // пример верхней границы
        int amount = new Random().nextInt(maxLimit - fromCardBalance) + fromCardBalance + 1; // генерируем сумму пополнения больше баланса второй карты до перевода

        var transfer = dashBoardPage.cardSelection(toCard.getTestId()); // создали объект трансфер, выбрали карту
        transfer.transfer(String.valueOf(amount), fromCard.getTestId()); // вызвали метод трансфер; перевели деньги

        $("div[data-test-id='error-notification']").shouldBe(Condition.visible, Duration.ofSeconds(15));
        String errorMessage = $("div[data-test-id='error-notification']").getText();
        assertTrue(errorMessage.contains("Ошибка!"), "Сообщение не содержит 'Ошибка!'");
        assertEquals(dashBoardPage.getCardBalance(fromCard.getTestId()), initialBalanceSecondCard);
        assertEquals(dashBoardPage.getCardBalance(toCard.getTestId()), initialBalanceFirstCard);

    }

    @Test
    void shouldNotTransferMoneyBetweenOwnCards() {
        String expectedMessage = "Ошибка!";

        var toCard = DataHelper.getFirstCardInfo(); // карта куда
        var fromCard = DataHelper.getSecondCardInfo(); // карта откуда

        var info = getAuthInfo(); // получение инфо авторизации
        var verificationCode = DataHelper.getVerificationCodeFor(info); // получение кода подтверждения
        var verificationPage = loginPage.validLogin(info); // выполнение входа
        var dashBoardPage = verificationPage.validVerify(verificationCode); // ввод кода подтверждения и переход на дашборд


        int initialBalanceFirstCard = dashBoardPage.getCardBalance(toCard.getTestId());
        int initialBalanceSecondCard = dashBoardPage.getCardBalance(fromCard.getTestId());    // проверяем баланс перед операцией
        if (initialBalanceSecondCard <= 0) {
            dashBoardPage.renewCardBalances();
        }

        int fromCardBalance = dashBoardPage.getCardBalance(fromCard.getTestId());
        int amount = fromCardBalance + 1;

        var transfer = dashBoardPage.cardSelection(toCard.getTestId()); // создали объект трансфер, выбрали карту
        transfer.transfer(String.valueOf(amount), fromCard.getTestId()); // вызвали метод трансфер; перевели деньги

        $("div[data-test-id='error-notification']").shouldBe(Condition.visible, Duration.ofSeconds(15));
        String errorMessage = $("div[data-test-id='error-notification']").getText();
        assertTrue(errorMessage.contains("Ошибка!"), "Сообщение не содержит 'Ошибка!'");
        assertEquals(dashBoardPage.getCardBalance(fromCard.getTestId()), initialBalanceSecondCard);
        assertEquals(dashBoardPage.getCardBalance(toCard.getTestId()), initialBalanceFirstCard);

    }
}
