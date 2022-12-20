package com.example.boardproject.dto;

import static java.util.stream.Collectors.*;

import com.example.boardproject.domain.Hashtag;
import java.time.LocalDateTime;
import java.util.Set;

public record HashtagWithArticlesDto(
        Long id,
        Set<ArticleDto> articleDtos,
        String hashtagName,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime modifiedAt,
        String modifiedBy
) {

    public static HashtagWithArticlesDto of(Set<ArticleDto> articleDtos, String hashtagName) {
        return new HashtagWithArticlesDto(null, articleDtos, hashtagName, null, null, null, null);
    }

    public static HashtagWithArticlesDto of(Long id, Set<ArticleDto> articleDtos, String hashtagName,
                                            LocalDateTime createdAt, String createdBy, LocalDateTime modifiedAt,
                                            String modifiedBy) {
        return new HashtagWithArticlesDto(id, articleDtos, hashtagName, createdAt, createdBy, modifiedAt, modifiedBy);
    }

    public static HashtagWithArticlesDto from(Hashtag entity) {
        return new HashtagWithArticlesDto(
                entity.getId(),
                entity.getArticles().stream()
                        .map(ArticleDto::from)
                        .collect(toUnmodifiableSet()),
                entity.getHashtagName(),
                entity.getCreatedAt(),
                entity.getCreatedBy(),
                entity.getModifiedAt(),
                entity.getModifiedBy()
        );
    }

    public Hashtag toEntity() {
        return Hashtag.of(hashtagName);
    }
}
