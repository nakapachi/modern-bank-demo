package dev.learningbank.config;

import dev.learningbank.domain.*;
import dev.learningbank.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.math.BigDecimal;

@Configuration
public class DemoDataConfig {
    @Bean
    CommandLineRunner demoData(AppUserRepository users, BankAccountRepository accounts,
                               BankBranchRepository branches, LedgerEntryRepository entries, PasswordEncoder encoder) {
        return args -> {
            BankBranch headOffice = branches.findById("001").orElseThrow();
            BankBranch igusa = branches.findById("002").orElseThrow();
            BankBranch fukagawa = branches.findById("003").orElseThrow();
            if (users.count() == 0) {
                AppUser alice = users.save(new AppUser("alice", encoder.encode("demo-pass"), "ナカタ ヒロミ",
                    "C000001", "alice@example.test", "090-0000-0001", UserRole.CUSTOMER));
                AppUser bob = users.save(new AppUser("bob", encoder.encode("demo-pass"), "ハト タロウ",
                    "C000002", "bob@example.test", "090-0000-0002", UserRole.CUSTOMER));
                BankAccount a1 = accounts.save(new BankAccount(AccountNumber.issue("100001"), new BigDecimal("150000.00"), alice, AccountType.ORDINARY, headOffice));
                accounts.save(new BankAccount(AccountNumber.issue("110001"), new BigDecimal("50000.00"), alice, AccountType.ORDINARY, igusa));
                accounts.save(new BankAccount(AccountNumber.issue("120001"), new BigDecimal("80000.00"), bob, AccountType.ORDINARY, fukagawa));
                entries.save(new LedgerEntry(a1, EntryType.DEPOSIT, new BigDecimal("150000.00"), null));
            }
            if (users.findByUsername("admin").isEmpty()) {
                users.save(new AppUser("admin", encoder.encode("admin-pass"), "管理者",
                    null, "admin@hato-bank.test", null, UserRole.ADMIN));
            }
        };
    }
}
