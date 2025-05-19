package com.userService.repository;

import com.userService.dto.User.SearchRole;
import com.userService.model.Users.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface UsersRepository extends JpaRepository<Users, UUID> {
    Optional<Users> findByEmail(String email);
}
