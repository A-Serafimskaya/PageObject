package ru.netology.web.data;

import lombok.Value;
import ru.netology.web.page.DashBoardPage;

import java.util.Random;

public class DataHelper {
    private DataHelper() {
    }

    @Value
    public static class AuthInfo {
        String login;
        String password;
    }

    @Value
    public static class VerificationCode {
        String code;
    }

    @Value
    public static class CardInfo {

        String number;
        String testId;
    }

    public static AuthInfo getAuthInfo() {
        return new AuthInfo("vasya", "qwerty123");
    }

    public static VerificationCode getVerificationCodeFor(AuthInfo authInfo) {
        return new VerificationCode("12345");
    }


    public static CardInfo getFirstCardInfo() {
        return new CardInfo("5559 0000 0000 0001", "92df3f1c-a033-48e6-8390-206f6b1f56c0");
    }

    public static CardInfo getSecondCardInfo() {
        return new CardInfo("5559 0000 0000 0002", "0f3f5c2a-249e-4c3d-8287-09f7a039391d");
    }


    public static int transferValidAmountCalculator(int cardBalance) {
        if (cardBalance <= 0) {
            return 0; // либо бросить исключение, если баланс нулевой
        }
        return new Random().nextInt(cardBalance);
    }

    public static int transferInValidAmountCalculator(int cardBalance, int offset) {

        if (cardBalance <= 0) {
            return 0;
        }
        int maxLimit = cardBalance + offset;
        return new Random().nextInt(maxLimit - cardBalance) + cardBalance + 1;
    }
}



