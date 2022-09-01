package com.hyunho9877.chatter.domain;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Entity @IdClass(FriendsKey.class)
@Getter @Setter @ToString
@AllArgsConstructor @NoArgsConstructor
public class Friends implements Serializable {

    @Id
    private String user;

    @Id
    private String friend;

    private Integer unconfirmed;
    private String lastMessage;
    private String lastChattedTime;

    @ManyToOne
    private ApplicationUser appUser;

    @ManyToOne
    private ApplicationUser appFriend;

    private Friends(String user, String friend, ApplicationUser appUser, ApplicationUser appFriend) {
        this.user = user;
        this.friend = friend;
        this.appUser = appUser;
        this.appFriend = appFriend;
    }

    public static Friends of(ApplicationUser user, ApplicationUser friend) {
        return new Friends(user.getEmail(), friend.getEmail(), user, friend);
    }

    @PrePersist
    public void prePersist() {
        this.unconfirmed = 0;
        this.lastChattedTime = "";
        this.lastMessage = "";
    }

}
