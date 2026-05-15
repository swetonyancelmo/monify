package com.swetonyancelmo.monify.repository;

import com.swetonyancelmo.monify.domain.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    Page<Transaction> findByAccount_User_Id(UUID userId, Pageable pageable);
}
