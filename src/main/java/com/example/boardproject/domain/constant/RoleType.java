package com.example.boardproject.domain.constant;

import lombok.Getter;

public enum RoleType {
    USER("ROLE_USER"), // ROLE_ : Spring Security 권한 표현을 하는 문자열 규칙
    ADMIN("ROLE_ADMIN");

    @Getter
    private final String name;

    RoleType(String name) {
        this.name = name;
    }

}
