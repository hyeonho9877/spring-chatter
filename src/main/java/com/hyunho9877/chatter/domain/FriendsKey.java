package com.hyunho9877.chatter.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FriendsKey implements Serializable {
    private String user;
    private String friend;
}
