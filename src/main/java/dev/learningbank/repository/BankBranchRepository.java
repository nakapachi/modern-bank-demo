package dev.learningbank.repository;

import dev.learningbank.domain.BankBranch;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BankBranchRepository extends JpaRepository<BankBranch, String> {
    List<BankBranch> findAllByOrderByCode();
}
