package com.example.boardproject.dto;

import static java.util.stream.Collectors.toUnmodifiableSet;

import com.example.boardproject.domain.Article;
import com.example.boardproject.domain.User;
import java.time.LocalDateTime;
import java.util.Set;

public record ArticleDto(
        Long id,
        UserDto userDto,
        String title,
        String content,
        Set<HashtagDto> hashtagDtos,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime modifiedAt,
        String modifiedBy
) {

    public static ArticleDto of(UserDto userDto, String title, String content, Set<HashtagDto> hashtagDtos) {
        return new ArticleDto(null, userDto, title, content, hashtagDtos, null, null, null, null);
    }

    public static ArticleDto of(Long id, UserDto userDto, String title, String content, Set<HashtagDto> hashtagDtos,
                                LocalDateTime createdAt, String createdBy, LocalDateTime modifiedAt,
                                String modifiedBy) {
        return new ArticleDto(id, userDto, title, content, hashtagDtos, createdAt, createdBy, modifiedAt, modifiedBy);
    }

    // 위 아래의 2가지 method 를 통해 dto -> domain 영향을 없앰
    public static ArticleDto from(Article entity) {
        return new ArticleDto(
                entity.getId(),
                UserDto.from(entity.getUser()),
                entity.getTitle(),
                entity.getContent(),
                entity.getHashtags().stream()
                        .map(HashtagDto::from)
                        .collect(toUnmodifiableSet()),
                entity.getCreatedAt(),
                entity.getCreatedBy(),
                entity.getModifiedAt(),
                entity.getModifiedBy()
        );
    }

    public Article toEntity(User user) {
        return Article.of(
                user,
                title,
                content
        );
    }
}
