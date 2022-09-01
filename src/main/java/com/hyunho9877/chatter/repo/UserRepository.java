package com.hyunho9877.chatter.repo;

import com.hyunho9877.chatter.domain.ApplicationUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<ApplicationUser, String> {
    List<ApplicationUser> findByNameContainsIgnoreCase(String name);

}