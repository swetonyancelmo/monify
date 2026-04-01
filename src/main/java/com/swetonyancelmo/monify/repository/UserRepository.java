package com.swetonyancelmo.monify.repository;

import com.swetonyancelmo.monify.domain.users.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
}
