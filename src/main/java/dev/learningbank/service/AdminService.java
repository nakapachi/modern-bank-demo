package dev.learningbank.service;

import dev.learningbank.domain.*;
import dev.learningbank.repository.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Locale;

@Service
public class AdminService {
    private final AppUserRepository users;
    private final BankAccountRepository accounts;
    private final LedgerEntryRepository entries;
    private final BankBranchRepository branches;
    private final AccountNumberIssuer accountNumberIssuer;
    private final PasswordEncoder encoder;

    public AdminService(AppUserRepository users, BankAccountRepository accounts,
                        LedgerEntryRepository entries, BankBranchRepository branches,
                        AccountNumberIssuer accountNumberIssuer, PasswordEncoder encoder) {
        this.users = users;
        this.accounts = accounts;
        this.entries = entries;
        this.branches = branches;
        this.accountNumberIssuer = accountNumberIssuer;
        this.encoder = encoder;
    }

    @Transactional(readOnly = true)
    public long customerCount() { return users.countByRole(UserRole.CUSTOMER); }

    @Transactional(readOnly = true)
    public long accountCount() { return accounts.count(); }

    @Transactional(readOnly = true)
    public long frozenAccountCount() { return accounts.countByStatus(AccountStatus.FROZEN); }

    @Transactional(readOnly = true)
    public List<BankBranch> branches() { return branches.findAllByOrderByCode(); }

    @Transactional(readOnly = true)
    public List<AppUser> customers(String query) {
        List<AppUser> all = users.findAllByRoleOrderById(UserRole.CUSTOMER);
        if (query == null || query.isBlank()) return all;
        String needle = query.trim().toLowerCase(Locale.ROOT);
        return all.stream().filter(user -> contains(user.getUsername(), needle)
            || contains(user.getDisplayName(), needle)
            || contains(user.getCustomerNumber(), needle)).toList();
    }

    @Transactional(readOnly = true)
    public AppUser customer(Long id) {
        AppUser user = users.findById(id).orElseThrow(() -> new BankingException("顧客が見つかりません。"));
        if (user.getRole() != UserRole.CUSTOMER) throw new BankingException("顧客が見つかりません。");
        return user;
    }

    @Transactional(readOnly = true)
    public List<BankAccount> accountsFor(Long customerId) {
        customer(customerId);
        return accounts.findAllByOwnerIdOrderById(customerId);
    }

    @Transactional
    public AppUser createCustomer(String customerNumber, String username, String password,
                                  String displayName, String email, String phone) {
        String number = required(customerNumber, "顧客番号").toUpperCase(Locale.ROOT);
        String login = required(username, "ユーザーID");
        String name = required(displayName, "氏名");
        if (!number.matches("C\\d{6}")) throw new BankingException("顧客番号はC＋6桁の数字で入力してください。");
        if (!login.matches("[A-Za-z0-9._-]{4,40}")) throw new BankingException("ユーザーIDは4〜40文字の英数字で入力してください。");
        if (password == null || password.length() < 8) throw new BankingException("初期パスワードは8文字以上で入力してください。");
        if (users.findByCustomerNumber(number).isPresent()) throw new BankingException("この顧客番号は使用済みです。");
        if (users.findByUsername(login).isPresent()) throw new BankingException("このユーザーIDは使用済みです。");
        return users.save(new AppUser(login, encoder.encode(password), name, number,
            blankToNull(email), blankToNull(phone), UserRole.CUSTOMER));
    }

    @Transactional
    public void updateCustomer(Long id, String displayName, String email, String phone, CustomerStatus status) {
        customer(id).updateProfile(required(displayName, "氏名"), blankToNull(email), blankToNull(phone), status);
    }

    @Transactional
    public BankAccount openAccount(Long customerId, AccountType type, String branchCode, BigDecimal initialBalance) {
        AppUser customer = customer(customerId);
        BigDecimal balance = initialBalance == null ? BigDecimal.ZERO : initialBalance;
        if (balance.signum() < 0 || balance.scale() > 2) throw new BankingException("初期残高を正しく入力してください。");
        balance = balance.setScale(2, RoundingMode.UNNECESSARY);
        BankBranch branch = branches.findById(required(branchCode, "支店"))
            .orElseThrow(() -> new BankingException("支店が見つかりません。"));
        String number = accountNumberIssuer.issue(branch.getCode(), type);
        BankAccount account = accounts.save(new BankAccount(number, balance, customer, type, branch));
        if (balance.signum() > 0) entries.save(new LedgerEntry(account, EntryType.DEPOSIT, balance, null));
        return account;
    }

    @Transactional
    public void changeAccountStatus(Long accountId, AccountStatus status) {
        BankAccount account = accounts.findById(accountId)
            .orElseThrow(() -> new BankingException("口座が見つかりません。"));
        if (status == AccountStatus.CLOSED && account.getBalance().signum() != 0) {
            throw new BankingException("残高がある口座は解約できません。");
        }
        account.changeStatus(status);
    }

    private boolean contains(String value, String needle) {
        return value != null && value.toLowerCase(Locale.ROOT).contains(needle);
    }

    private String required(String value, String label) {
        if (value == null || value.isBlank()) throw new BankingException(label + "を入力してください。");
        return value.trim();
    }

    private String blankToNull(String value) { return value == null || value.isBlank() ? null : value.trim(); }
}
