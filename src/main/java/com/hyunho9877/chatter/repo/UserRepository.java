package com.hyunho9877.chatter.repo;

import com.hyunho9877.chatter.domain.ApplicationUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<ApplicationUser, String> {

}