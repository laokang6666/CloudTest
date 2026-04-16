package com.car.userservice.repository;

import com.car.userservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByLoginName(String loginName);

    Optional<User> findByLoginName(String loginName);
}
