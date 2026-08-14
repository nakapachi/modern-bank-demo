package dev.learningbank.service;

import dev.learningbank.domain.*;
import dev.learningbank.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class BankingService {
    private final BankAccountRepository accounts;
    private final LedgerEntryRepository entries;
    private final AppUserRepository users;

    public BankingService(BankAccountRepository accounts, LedgerEntryRepository entries, AppUserRepository users) {
        this.accounts = accounts;
        this.entries = entries;
        this.users = users;
    }

    @Transactional(readOnly = true)
    public String displayNameFor(String username) {
        return users.findByUsername(username)
            .map(AppUser::getDisplayName)
            .orElse(username);
    }

    @Transactional(readOnly = true)
    public List<BankAccount> accountsFor(String username) {
        return accounts.findAllByOwnerUsernameOrderById(username);
    }

    @Transactional(readOnly = true)
    public BankAccount accountFor(Long id, String username) {
        return accounts.findByIdAndOwnerUsername(id, username)
            .orElseThrow(() -> new BankingException("口座が見つかりません。"));
    }

    @Transactional(readOnly = true)
    public List<LedgerEntry> historyFor(Long id, String username) {
        accountFor(id, username);
        return entries.findAllByAccountIdOrderByOccurredAtDesc(id);
    }

    @Transactional
    public void deposit(Long id, String username, BigDecimal rawAmount) {
        BigDecimal amount = validAmount(rawAmount);
        BankAccount account = ownedAccountForUpdate(id, username);
        account.credit(amount);
        entries.save(new LedgerEntry(account, EntryType.DEPOSIT, amount, null));
    }

    @Transactional
    public void withdraw(Long id, String username, BigDecimal rawAmount) {
        BigDecimal amount = validAmount(rawAmount);
        BankAccount account = ownedAccountForUpdate(id, username);
        requireFunds(account, amount);
        account.debit(amount);
        entries.save(new LedgerEntry(account, EntryType.WITHDRAWAL, amount, null));
    }

    @Transactional
    public void transfer(Long sourceId, String username, String destinationNumber, BigDecimal rawAmount) {
        BigDecimal amount = validAmount(rawAmount);
        if (destinationNumber == null || !destinationNumber.matches("\\d{7}")) {
            throw new BankingException("振込先口座番号は7桁の数字で入力してください。");
        }
        BankAccount source = ownedAccountForUpdate(sourceId, username);
        if (source.getAccountNumber().equals(destinationNumber)) {
            throw new BankingException("同じ口座には振り込めません。");
        }
        BankAccount destination = accounts.findByNumberForUpdate(destinationNumber)
            .orElseThrow(() -> new BankingException("振込先口座が見つかりません。"));
        requireFunds(source, amount);
        source.debit(amount);
        destination.credit(amount);
        entries.save(new LedgerEntry(source, EntryType.TRANSFER_OUT, amount, destination.getAccountNumber()));
        entries.save(new LedgerEntry(destination, EntryType.TRANSFER_IN, amount, source.getAccountNumber()));
    }

    private BankAccount ownedAccountForUpdate(Long id, String username) {
        BankAccount account = accounts.findByIdForUpdate(id)
            .orElseThrow(() -> new BankingException("口座が見つかりません。"));
        if (!account.getOwner().getUsername().equals(username)) {
            throw new BankingException("この口座を操作する権限がありません。");
        }
        return account;
    }

    private BigDecimal validAmount(BigDecimal amount) {
        if (amount == null || amount.signum() <= 0 || amount.scale() > 2) {
            throw new BankingException("金額は0より大きい値を小数2桁以内で指定してください。");
        }
        return amount.setScale(2, RoundingMode.UNNECESSARY);
    }

    private void requireFunds(BankAccount account, BigDecimal amount) {
        if (account.getBalance().compareTo(amount) < 0) {
            throw new BankingException("残高が不足しています。");
        }
    }
}
