package dev.learningbank.service;

import dev.learningbank.domain.*;
import dev.learningbank.repository.AccountNumberSequenceRepository;
import org.springframework.stereotype.Service;

@Service
public class AccountNumberIssuer {
    private final AccountNumberSequenceRepository sequences;

    public AccountNumberIssuer(AccountNumberSequenceRepository sequences) {
        this.sequences = sequences;
    }

    public String issue(String branchCode, AccountType accountType) {
        AccountNumberSequence sequence = sequences.findForUpdate(branchCode, accountType)
            .orElseThrow(() -> new BankingException("この支店・科目の採番設定がありません。"));
        int serial = sequence.takeNext();
        return AccountNumber.issue(accountType.numberBand() + "%05d".formatted(serial));
    }
}
