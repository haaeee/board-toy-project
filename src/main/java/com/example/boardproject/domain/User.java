package com.example.boardproject.domain;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Objects;

@Getter
@ToString
@Table(name = "users", indexes = {
        @Index(columnList = "user_id"),
        @Index(columnList = "email", unique = true),
        @Index(columnList = "createdAt"),
        @Index(columnList = "createdBy")
})
@Entity
public class User extends AuditingFields {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Setter
    @Column(nullable = false, length = 100)
    private String email;

    @Setter
    @Column(nullable = false)
    private String password;

    @Setter
    @Column(length = 100)
    private String nickname;

    @Setter
    private String memo;

    protected User() {
    }

    private User(String email, String password, String nickname, String memo) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.memo = memo;
    }

    public static User of(String email, String password, String nickname, String memo) {
        return new User(email, password, nickname, memo);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return id != null && id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
