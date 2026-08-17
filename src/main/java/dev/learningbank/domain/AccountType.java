package dev.learningbank.domain;

public enum AccountType {
    ORDINARY("1"),
    SAVINGS("2");

    private final String numberBand;

    AccountType(String numberBand) { this.numberBand = numberBand; }
    public String numberBand() { return numberBand; }
}
