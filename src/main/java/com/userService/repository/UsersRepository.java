package com.userService.repository;

import com.userService.dto.User.SearchRole;
import com.userService.model.Users.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;


@Repository
public interface UsersRepository extends JpaRepository<Users, Integer> {
    Optional<Users> findByEmail(String email);

    @Query("SELECT SearchRole(u.role,u.user_id) " +
            "FROM Users u " +
            "JOIN u.role r " +
            "WHERE u.user_id = :user_id")
    SearchRole findUserRoleById(@Param("user_id") Long userId);
}
