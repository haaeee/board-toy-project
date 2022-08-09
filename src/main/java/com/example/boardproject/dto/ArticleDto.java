package com.example.boardproject.dto;

import com.example.boardproject.domain.Article;

import java.time.LocalDateTime;

public record ArticleDto(
        Long id,
        UserDto userDto,
        String title,
        String content,
        String hashtag,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime modifiedAt,
        String modifiedBy
) {
    public static ArticleDto of(Long id,
                                UserDto userDto,
                                String title,
                                String content,
                                String hashtag,
                                LocalDateTime createdAt,
                                String createdBy,
                                LocalDateTime modifiedAt,
                                String modifiedBy) {
        return new ArticleDto(id, userDto, title, content, hashtag, createdAt, createdBy, modifiedAt, modifiedBy);
    }

    // 위 아래의 2가지 method 를 통해 dto -> domain 영향을 없앰
    public static ArticleDto from(Article entity) {
        return new ArticleDto(
                entity.getId(),
                UserDto.from(entity.getUser()),
                entity.getTitle(),
                entity.getContent(),
                entity.getHashtag(),
                entity.getCreatedAt(),
                entity.getCreatedBy(),
                entity.getModifiedAt(),
                entity.getModifiedBy()
        );
    }

    public Article toEntity() {
        return Article.of(
                userDto.toEntity(),
                title,
                content,
                hashtag
        );
    }

}
