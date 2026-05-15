package com.swetonyancelmo.monify.repository;

import com.swetonyancelmo.monify.domain.Account;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {

    boolean existsByNameAndUserId(String name, UUID userId);
    Optional<Account> findByIdAndUserId(UUID id, UUID userId);
    Page<Account> findByUserId(UUID userId, Pageable pageable);
}
