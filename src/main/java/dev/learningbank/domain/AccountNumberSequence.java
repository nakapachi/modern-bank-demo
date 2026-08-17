package dev.learningbank.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "account_number_sequence")
@IdClass(AccountNumberSequenceId.class)
public class AccountNumberSequence {
    @Id @Column(name = "branch_code", length = 3)
    private String branchCode;
    @Id @Enumerated(EnumType.STRING) @Column(name = "account_type", length = 20)
    private AccountType accountType;
    @Column(name = "next_sequence", nullable = false)
    private int nextSequence;
    @Column(name = "last_issued_at")
    private Instant lastIssuedAt;
    @Version
    private long version;

    protected AccountNumberSequence() {}

    public int takeNext() {
        if (nextSequence < 1 || nextSequence > 99_999) {
            throw new IllegalStateException("この支店・科目の口座番号帯を使い切りました。");
        }
        int issued = nextSequence++;
        lastIssuedAt = Instant.now();
        return issued;
    }
}
