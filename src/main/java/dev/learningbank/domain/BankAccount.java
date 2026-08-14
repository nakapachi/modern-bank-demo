package dev.learningbank.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "bank_account")
public class BankAccount {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "account_number", nullable = false, unique = true, length = 7)
    private String accountNumber;
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_id", nullable = false)
    private AppUser owner;
    @Version
    private long version;

    protected BankAccount() {}
    public BankAccount(String accountNumber, BigDecimal balance, AppUser owner) {
        if (accountNumber == null || !accountNumber.matches("\\d{7}")) {
            throw new IllegalArgumentException("口座番号は7桁の数字で指定してください。");
        }
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.owner = owner;
    }
    public Long getId() { return id; }
    public String getAccountNumber() { return accountNumber; }
    public BigDecimal getBalance() { return balance; }
    public AppUser getOwner() { return owner; }
    public void credit(BigDecimal amount) { balance = balance.add(amount); }
    public void debit(BigDecimal amount) { balance = balance.subtract(amount); }
}
