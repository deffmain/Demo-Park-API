package com.micael.demo_park_api.repository;

import com.micael.demo_park_api.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);


    @Query("SELECT u.role FROM User u WHERE u.username = :username")
    User.Role getRoleByUsername(@Param("username") String username);

}
