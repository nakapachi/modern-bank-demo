package dev.learningbank.repository;

import dev.learningbank.domain.*;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AccountNumberSequenceRepository extends JpaRepository<AccountNumberSequence, AccountNumberSequenceId> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from AccountNumberSequence s where s.branchCode = :branchCode and s.accountType = :accountType")
    Optional<AccountNumberSequence> findForUpdate(@Param("branchCode") String branchCode,
                                                   @Param("accountType") AccountType accountType);
}
