package dev.learningbank.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "bank_branch")
public class BankBranch {
    @Id
    @Column(length = 3)
    private String code;
    @Column(name = "bank_code", nullable = false, length = 4)
    private String bankCode;
    @Column(nullable = false, length = 80)
    private String name;
    @Column(nullable = false, length = 120)
    private String location;
    @Column(nullable = false, length = 120)
    private String connection;

    protected BankBranch() {}
    public BankBranch(String code, String bankCode, String name, String location, String connection) {
        this.code = code;
        this.bankCode = bankCode;
        this.name = name;
        this.location = location;
        this.connection = connection;
    }
    public String getCode() { return code; }
    public String getBankCode() { return bankCode; }
    public String getName() { return name; }
    public String getLocation() { return location; }
    public String getConnection() { return connection; }
}
