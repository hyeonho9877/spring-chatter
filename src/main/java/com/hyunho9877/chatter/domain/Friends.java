package com.hyunho9877.chatter.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
public class Friends {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private ApplicationUser user1;

    @ManyToOne
    private ApplicationUser user2;

    public Friends() {
    }

    private Friends(ApplicationUser user1, ApplicationUser user2) {
        this.user1 = user1;
        this.user2 = user2;
    }

    public static Friends of(ApplicationUser user1, ApplicationUser user2) {
        return new Friends(user1, user2);
    }

}
