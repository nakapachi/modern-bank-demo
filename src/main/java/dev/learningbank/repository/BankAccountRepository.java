package dev.learningbank.repository;

import dev.learningbank.domain.BankAccount;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.*;

public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {
    @EntityGraph(attributePaths = "branch")
    List<BankAccount> findAllByOwnerUsernameOrderById(String username);
    @EntityGraph(attributePaths = "branch")
    Optional<BankAccount> findByIdAndOwnerUsername(Long id, String username);
    Optional<BankAccount> findByAccountNumber(String accountNumber);
    @EntityGraph(attributePaths = "branch")
    List<BankAccount> findAllByOwnerIdOrderById(Long ownerId);
    long countByStatus(dev.learningbank.domain.AccountStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from BankAccount a where a.id = :id")
    Optional<BankAccount> findByIdForUpdate(@Param("id") Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from BankAccount a where a.accountNumber = :number")
    Optional<BankAccount> findByNumberForUpdate(@Param("number") String number);
}
