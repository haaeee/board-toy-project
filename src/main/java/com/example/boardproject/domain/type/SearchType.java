package com.example.boardproject.domain.type;

import lombok.Getter;

public enum SearchType {
    TITLE("제목"),
    CONTENT("게시글 내용"),
    EMAIL("이메일"),
    NICKNAME("닉네임"),
    HASHTAG("해시태그");

    @Getter
    private final String description;

    SearchType(String description) {
        this.description = description;
    }

}
