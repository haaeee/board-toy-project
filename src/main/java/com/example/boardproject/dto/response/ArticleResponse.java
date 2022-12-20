package com.example.boardproject.dto.response;

import static java.util.stream.Collectors.*;

import com.example.boardproject.dto.ArticleDto;

import com.example.boardproject.dto.HashtagDto;
import java.time.LocalDateTime;
import java.util.Set;

public record ArticleResponse(
        Long id,
        String title,
        String content,
        Set<String> hashtags,
        LocalDateTime createdAt,
        String email,
        String nickname
) {

    public static ArticleResponse of(Long id, String title, String content, Set<String> hashtags,
                                     LocalDateTime createdAt,
                                     String email, String nickname) {
        return new ArticleResponse(id, title, content, hashtags, createdAt, email, nickname);
    }

    public static ArticleResponse from(ArticleDto dto) {
        String nickname = dto.userDto().nickname();
        if (nickname == null || nickname.isBlank()) {
            nickname = dto.userDto().email();
        }

        return new ArticleResponse(
                dto.id(),
                dto.title(),
                dto.content(),
                dto.hashtagDtos().stream()
                        .map(HashtagDto::hashtagName)
                        .collect(toUnmodifiableSet()),
                dto.createdAt(),
                dto.userDto().email(),
                nickname
        );
    }

}
