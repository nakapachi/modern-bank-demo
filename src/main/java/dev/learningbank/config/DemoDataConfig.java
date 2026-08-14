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
                               LedgerEntryRepository entries, PasswordEncoder encoder) {
        return args -> {
            if (users.count() != 0) return;
            AppUser alice = users.save(new AppUser("alice", encoder.encode("demo-pass"), "ナカタ ヒロミ"));
            AppUser bob = users.save(new AppUser("bob", encoder.encode("demo-pass"), "ハト タロウ"));
            BankAccount a1 = accounts.save(new BankAccount("1000001", new BigDecimal("150000.00"), alice));
            accounts.save(new BankAccount("1000002", new BigDecimal("50000.00"), alice));
            accounts.save(new BankAccount("2000001", new BigDecimal("80000.00"), bob));
            entries.save(new LedgerEntry(a1, EntryType.DEPOSIT, new BigDecimal("150000.00"), null));
        };
    }
}
