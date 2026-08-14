package dev.learningbank.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "ledger_entry")
public class LedgerEntry {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "account_id", nullable = false)
    private BankAccount account;
    @Enumerated(EnumType.STRING)
    @Column(name = "entry_type", nullable = false, length = 24)
    private EntryType type;
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;
    @Column(name = "counterparty_account", length = 7)
    private String counterpartyAccount;
    @Column(name = "occurred_at", nullable = false)
    private Instant occurredAt;

    protected LedgerEntry() {}
    public LedgerEntry(BankAccount account, EntryType type, BigDecimal amount, String counterpartyAccount) {
        this.account = account;
        this.type = type;
        this.amount = amount;
        this.counterpartyAccount = counterpartyAccount;
        this.occurredAt = Instant.now();
    }
    public Long getId() { return id; }
    public EntryType getType() { return type; }
    public BigDecimal getAmount() { return amount; }
    public String getCounterpartyAccount() { return counterpartyAccount; }
    public Instant getOccurredAt() { return occurredAt; }
}
