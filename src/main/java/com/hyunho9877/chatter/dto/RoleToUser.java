package com.hyunho9877.chatter.dto;

import com.hyunho9877.chatter.domain.Role;
import lombok.Data;

@Data
public class RoleToUser {
    private String email;
    private Role role;
}
