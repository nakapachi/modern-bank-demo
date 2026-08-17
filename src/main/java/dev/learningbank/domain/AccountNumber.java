package dev.learningbank.domain;

public final class AccountNumber {
    private AccountNumber() {}

    public static String issue(String sixDigitBase) {
        if (sixDigitBase == null || !sixDigitBase.matches("\\d{6}")) {
            throw new IllegalArgumentException("口座番号の発番元は6桁の数字で指定してください。");
        }
        int sum = 0;
        for (int i = 0; i < sixDigitBase.length(); i++) {
            int digit = sixDigitBase.charAt(i) - '0';
            if ((sixDigitBase.length() - i) % 2 == 1) {
                digit *= 2;
                if (digit > 9) digit -= 9;
            }
            sum += digit;
        }
        return sixDigitBase + ((10 - sum % 10) % 10);
    }

    public static boolean isValid(String accountNumber) {
        return accountNumber != null
            && accountNumber.matches("\\d{7}")
            && issue(accountNumber.substring(0, 6)).equals(accountNumber);
    }
}
