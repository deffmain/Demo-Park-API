package com.micael.demo_park_api.repository;

import com.micael.demo_park_api.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

}
