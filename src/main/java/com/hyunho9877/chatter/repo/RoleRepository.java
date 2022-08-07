package com.hyunho9877.chatter.repo;

import com.hyunho9877.chatter.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, String> {
}