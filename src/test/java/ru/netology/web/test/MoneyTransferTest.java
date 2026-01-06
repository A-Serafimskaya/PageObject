package ru.netology.web.test;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.web.data.DataHelper;
import ru.netology.web.page.LoginPageV2;
import ru.netology.web.page.Transfer;

import java.time.Duration;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static ru.netology.web.data.DataHelper.*;


public class MoneyTransferTest {
    private LoginPageV2 loginPage;

    @BeforeEach
    void setup() {
        loginPage = Selenide.open("http://localhost:9999", LoginPageV2.class);
    }

    // 1
    @Test
    void shouldTransferMoneyBetweenOwnCardsFromSecond() {
        var toCard = DataHelper.getFirstCardInfo(); // карта куда
        var fromCard = DataHelper.getSecondCardInfo(); // карта откуда

        var info = getAuthInfo(); // получение инфо авторизации
        var verificationCode = DataHelper.getVerificationCodeFor(info); // получение кода подтверждения
        var verificationPage = loginPage.validLogin(info); // выполнение входа
        var dashBoardPage = verificationPage.validVerify(verificationCode); // ввод кода подтверждения и переход на дашборд

        int initialBalanceFirstCard = dashBoardPage.getCardBalance(toCard); // проверяем балансы перед операцией
        int initialBalanceSecondCard = dashBoardPage.getCardBalance(fromCard);

        int amount = DataHelper.transferValidAmountCalculator(initialBalanceSecondCard); // генерируем сумму пополнения в рамках баланса второй карты до перевода
        var transfer = dashBoardPage.cardSelection(toCard); // создали объект трансфер, выбрали карту
        transfer.transfer(amount, fromCard);

        int finalBalanceFirstCard = dashBoardPage.getCardBalance(toCard);
        int finalBalanceSecondCard = dashBoardPage.getCardBalance(fromCard);

        var expectedFromCardBalanceAfterTransfer = initialBalanceSecondCard - amount;// ожидаемый баланс после перевода
        var expectedToCardBalanceAfterTransfer = initialBalanceFirstCard + amount;

        assertEquals(expectedFromCardBalanceAfterTransfer - amount, finalBalanceSecondCard);
        assertEquals(expectedToCardBalanceAfterTransfer + amount, finalBalanceFirstCard);

    }

    // 2
    @Test
    void shouldTransferMoneyBetweenOwnCardsFromFirst() {
        var toCard = DataHelper.getSecondCardInfo(); // карта куда
        var fromCard = DataHelper.getFirstCardInfo(); // карта откуда

        var info = getAuthInfo(); // получение инфо авторизации
        var verificationCode = DataHelper.getVerificationCodeFor(info); // получение кода подтверждения
        var verificationPage = loginPage.validLogin(info); // выполнение входа
        var dashBoardPage = verificationPage.validVerify(verificationCode); // ввод кода подтверждения и переход на дашборд

        int initialBalanceFirstCard = dashBoardPage.getCardBalance(fromCard);
        int initialBalanceSecondCard = dashBoardPage.getCardBalance(toCard);

        int amount = DataHelper.transferValidAmountCalculator(initialBalanceFirstCard);
        var transfer = dashBoardPage.cardSelection(toCard); // создали объект трансфер, выбрали карту
        transfer.transfer(amount, fromCard); // вызвали метод трансфер; перевели деньги

        int finalBalanceFirstCard = dashBoardPage.getCardBalance(fromCard);
        int finalBalanceSecondCard = dashBoardPage.getCardBalance(toCard);

        var expectedFromCardBalanceAfterTransfer = initialBalanceFirstCard - amount;// рассчитываем ожидаемый баланс первой карты после перевода
        var expectedToCardBalanceAfterTransfer = initialBalanceSecondCard + amount;// рассчитываем ожидаемый баланс второй карты после перевода

        assertEquals(expectedFromCardBalanceAfterTransfer, finalBalanceFirstCard);
        assertEquals(expectedToCardBalanceAfterTransfer, finalBalanceSecondCard);
    }

    // 3
    @Test
    void shouldNotTransferMoneyBetweenOwnCardsOverBalance() {

        String expectedMessage = "Ошибка!";

        var toCard = DataHelper.getFirstCardInfo(); // карта куда
        var fromCard = DataHelper.getSecondCardInfo(); // карта откуда

        var info = getAuthInfo(); // получение инфо авторизации
        var verificationCode = DataHelper.getVerificationCodeFor(info); // получение кода подтверждения
        var verificationPage = loginPage.validLogin(info); // выполнение входа
        var dashBoardPage = verificationPage.validVerify(verificationCode); // ввод кода подтверждения и переход на дашборд

        int initialBalanceFirstCard = dashBoardPage.getCardBalance(fromCard);
        int initialBalanceSecondCard = dashBoardPage.getCardBalance(toCard); // проверяем баланс перед операцией


        int offset = 1000; // или другой размер, который подходит для теста
        int amount = DataHelper.transferInValidAmountCalculator(initialBalanceSecondCard, offset);

        var transfer = dashBoardPage.cardSelection(toCard);

        transfer.transfer(amount, fromCard);

        transfer.shouldSeeErrorMessage(expectedMessage);
        assertEquals(dashBoardPage.getCardBalance(fromCard), initialBalanceSecondCard);
        assertEquals(dashBoardPage.getCardBalance(toCard), initialBalanceFirstCard);

    }

}
