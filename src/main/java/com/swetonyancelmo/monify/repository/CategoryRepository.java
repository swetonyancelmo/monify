package com.swetonyancelmo.monify.repository;

import com.swetonyancelmo.monify.domain.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {

    boolean existsByNameAndUserId(String name, UUID userId);

    Optional<Category> findByIdAndUserId(UUID id, UUID userId);

    Page<Category> findByUserId(UUID userId, Pageable pageable);

    @Modifying
    @Query("DELETE FROM Category c WHERE c.id = :id AND c.user.id = :userId")
    int deleteByIdAndUserId(UUID id, UUID userId);
}
