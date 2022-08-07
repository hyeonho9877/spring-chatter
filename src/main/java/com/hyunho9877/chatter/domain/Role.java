package com.hyunho9877.chatter.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public enum Role {
    USER, MANAGER, ADMIN
}
