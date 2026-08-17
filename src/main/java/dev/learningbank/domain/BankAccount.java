package dev.learningbank.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "bank_account")
public class BankAccount {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "account_number", nullable = false, unique = true, length = 7)
    private String accountNumber;
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private AppUser owner;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "branch_code", nullable = false)
    private BankBranch branch;
    @Enumerated(EnumType.STRING)
    @Column(name = "account_type", nullable = false, length = 20)
    private AccountType accountType = AccountType.ORDINARY;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AccountStatus status = AccountStatus.ACTIVE;
    @Column(name = "opened_at", nullable = false)
    private Instant openedAt = Instant.now();
    @Version
    private long version;

    protected BankAccount() {}
    public BankAccount(String accountNumber, BigDecimal balance, AppUser owner) {
        this(accountNumber, balance, owner, AccountType.ORDINARY, null);
    }
    public BankAccount(String accountNumber, BigDecimal balance, AppUser owner, AccountType accountType) {
        this(accountNumber, balance, owner, accountType, null);
    }
    public BankAccount(String accountNumber, BigDecimal balance, AppUser owner, AccountType accountType, BankBranch branch) {
        if (!AccountNumber.isValid(accountNumber)) {
            throw new IllegalArgumentException("口座番号のチェックデジットが正しくありません。");
        }
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.owner = owner;
        this.accountType = accountType;
        this.branch = branch;
    }
    public Long getId() { return id; }
    public String getAccountNumber() { return accountNumber; }
    public BigDecimal getBalance() { return balance; }
    public AppUser getOwner() { return owner; }
    public BankBranch getBranch() { return branch; }
    public String getBranchDisplayName() {
        return branch == null ? "本店営業部（001）" : branch.getName() + "（" + branch.getCode() + "）";
    }
    public AccountType getAccountType() { return accountType; }
    public AccountStatus getStatus() { return status; }
    public Instant getOpenedAt() { return openedAt; }
    public void changeStatus(AccountStatus status) { this.status = status; }
    public void credit(BigDecimal amount) { balance = balance.add(amount); }
    public void debit(BigDecimal amount) { balance = balance.subtract(amount); }
}
