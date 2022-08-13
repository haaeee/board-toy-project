package com.example.boardproject.dto;

import com.example.boardproject.domain.User;

import java.time.LocalDateTime;

public record UserDto(
        Long id,
        String email,
        String userPassword,
        String nickname,
        String memo,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime modifiedAt,
        String modifiedBy
) {

    public static UserDto of(String email, String userPassword, String nickname, String memo) {
        return new UserDto(null, email, userPassword, nickname, memo, null, null, null, null);
    }

    public static UserDto of(Long id,
                             String email,
                             String userPassword,
                             String nickname,
                             String memo,
                             LocalDateTime createdAt,
                             String createdBy,
                             LocalDateTime modifiedAt,
                             String modifiedBy) {
        return new UserDto(id, email, userPassword, nickname, memo, createdAt, createdBy, modifiedAt, modifiedBy);
    }

    public static UserDto from(User entity) {
        return new UserDto(
                entity.getId(),
                entity.getEmail(),
                entity.getUserPassword(),
                entity.getNickname(),
                entity.getMemo(),
                entity.getCreatedAt(),
                entity.getCreatedBy(),
                entity.getModifiedAt(),
                entity.getModifiedBy()
        );
    }

    public User toEntity() {
        return User.of(
                email,
                userPassword,
                nickname,
                memo
        );
    }

}
