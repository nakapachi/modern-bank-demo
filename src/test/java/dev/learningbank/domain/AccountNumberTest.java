package dev.learningbank.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class AccountNumberTest {
    @Test
    void issuesLuhnCheckDigitAsSeventhDigit() {
        assertThat(AccountNumber.issue("100000")).isEqualTo("1000009");
        assertThat(AccountNumber.issue("100001")).isEqualTo("1000017");
        assertThat(AccountNumber.issue("200000")).isEqualTo("2000008");
    }

    @Test
    void validatesCompleteAccountNumber() {
        assertThat(AccountNumber.isValid("1000009")).isTrue();
        assertThat(AccountNumber.isValid("1000001")).isFalse();
        assertThat(AccountNumber.isValid("123456")).isFalse();
    }
}
