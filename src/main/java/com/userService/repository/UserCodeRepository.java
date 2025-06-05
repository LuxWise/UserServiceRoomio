package com.userService.repository;

import com.userService.model.UserCodes;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserCodeRepository extends JpaRepository<UserCodes, UUID> {
    Optional<UserCodes> findByEmail(String email);

}
