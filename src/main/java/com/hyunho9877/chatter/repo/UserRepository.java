package com.hyunho9877.chatter.repo;

import com.hyunho9877.chatter.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByName(String name);
}