package dev.learningbank.service;

import dev.learningbank.domain.*;
import dev.learningbank.repository.BankAccountRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:admin-test;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE")
@Transactional
class AdminServiceTest {
    @Autowired AdminService admin;
    @Autowired BankAccountRepository accounts;

    @Test
    void createsCustomerAndOpensAccount() {
        AppUser customer = admin.createCustomer("C900001", "testcustomer", "test-pass-123",
            "テスト 顧客", "test@example.test", "090-9999-0001");

        BankAccount account = admin.openAccount(customer.getId(), AccountType.ORDINARY, "004",
            new BigDecimal("10000.00"));

        assertThat(account.getOwner().getId()).isEqualTo(customer.getId());
        assertThat(account.getBalance()).isEqualByComparingTo("10000.00");
        assertThat(account.getStatus()).isEqualTo(AccountStatus.ACTIVE);
        assertThat(account.getAccountNumber()).isEqualTo("1300011");
        assertThat(account.getBranch().getName()).isEqualTo("鎌倉支店");

        BankAccount nextOrdinary = admin.openAccount(customer.getId(), AccountType.ORDINARY, "004", BigDecimal.ZERO);
        BankAccount firstSavings = admin.openAccount(customer.getId(), AccountType.SAVINGS, "004", BigDecimal.ZERO);
        assertThat(nextOrdinary.getAccountNumber()).isEqualTo("1300029");
        assertThat(firstSavings.getAccountNumber()).isEqualTo("2300010");
    }

    @Test
    void preventsClosingAccountWithBalance() {
        BankAccount account = accounts.findByAccountNumber("1000017").orElseThrow();

        assertThatThrownBy(() -> admin.changeAccountStatus(account.getId(), AccountStatus.CLOSED))
            .isInstanceOf(BankingException.class)
            .hasMessageContaining("残高");
    }
}
