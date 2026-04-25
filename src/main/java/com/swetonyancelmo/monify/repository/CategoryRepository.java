package com.swetonyancelmo.monify.repository;

import com.swetonyancelmo.monify.domain.categories.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {

    boolean existsByNameAndUserId(String name, UUID userId);

    Optional<Category> findByIdAndUserId(UUID id, UUID userId);

    @Modifying
    @Query("DELETE FROM Category c WHERE c.id = :id AND c.user.id = :userId")
    int deleteByIdAndUserId(UUID id, UUID userId);
}
