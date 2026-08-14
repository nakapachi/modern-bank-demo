package dev.learningbank.service;

import dev.learningbank.domain.*;
import dev.learningbank.repository.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:banking-test;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE")
@Transactional
class BankingServiceTest {
    @Autowired BankingService banking;
    @Autowired BankAccountRepository accounts;

    @Test
    void transferMovesMoneyAtomically() {
        BankAccount source = accounts.findByAccountNumber("1000001").orElseThrow();
        BankAccount destination = accounts.findByAccountNumber("2000001").orElseThrow();
        BigDecimal sourceBefore = source.getBalance();
        BigDecimal destinationBefore = destination.getBalance();

        banking.transfer(source.getId(), "alice", destination.getAccountNumber(), new BigDecimal("1250.00"));

        assertThat(source.getBalance()).isEqualByComparingTo(sourceBefore.subtract(new BigDecimal("1250.00")));
        assertThat(destination.getBalance()).isEqualByComparingTo(destinationBefore.add(new BigDecimal("1250.00")));
    }

    @Test
    void cannotWithdrawMoreThanBalance() {
        BankAccount source = accounts.findByAccountNumber("1000002").orElseThrow();
        assertThatThrownBy(() -> banking.withdraw(source.getId(), "alice", new BigDecimal("999999.00")))
            .isInstanceOf(BankingException.class).hasMessageContaining("残高");
    }

    @Test
    void anotherCustomerCannotOperateAccount() {
        BankAccount source = accounts.findByAccountNumber("1000001").orElseThrow();
        assertThatThrownBy(() -> banking.withdraw(source.getId(), "bob", new BigDecimal("100.00")))
            .isInstanceOf(BankingException.class).hasMessageContaining("権限");
    }
}
