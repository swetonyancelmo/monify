package com.swetonyancelmo.monify.repository;

import com.swetonyancelmo.monify.domain.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    List<Transaction> findByAccount_User_Id(UUID userId);
}
