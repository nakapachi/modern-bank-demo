package dev.learningbank.repository;

import dev.learningbank.domain.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;
import dev.learningbank.domain.UserRole;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    Optional<AppUser> findByUsername(String username);
    Optional<AppUser> findByCustomerNumber(String customerNumber);
    List<AppUser> findAllByRoleOrderById(UserRole role);
    long countByRole(UserRole role);
}
