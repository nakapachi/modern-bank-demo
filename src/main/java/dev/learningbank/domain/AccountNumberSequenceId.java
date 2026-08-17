package dev.learningbank.domain;

import java.io.Serializable;
import java.util.Objects;

public class AccountNumberSequenceId implements Serializable {
    private String branchCode;
    private AccountType accountType;

    public AccountNumberSequenceId() {}
    public AccountNumberSequenceId(String branchCode, AccountType accountType) {
        this.branchCode = branchCode;
        this.accountType = accountType;
    }

    @Override public boolean equals(Object other) {
        if (this == other) return true;
        if (!(other instanceof AccountNumberSequenceId id)) return false;
        return Objects.equals(branchCode, id.branchCode) && accountType == id.accountType;
    }

    @Override public int hashCode() { return Objects.hash(branchCode, accountType); }
}
