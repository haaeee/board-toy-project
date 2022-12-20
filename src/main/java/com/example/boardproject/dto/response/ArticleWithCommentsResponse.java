package com.example.boardproject.dto.response;

import static java.util.stream.Collectors.*;

import com.example.boardproject.dto.ArticleWithCommentsDto;

import com.example.boardproject.dto.HashtagDto;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

public record ArticleWithCommentsResponse(
        Long id,
        String title,
        String content,
        Set<String> hashtags,
        LocalDateTime createdAt,
        String email,
        String nickname,
        Set<ArticleCommentResponse> articleCommentResponse
) {

    public static ArticleWithCommentsResponse of(Long id, String title, String content, Set<String> hashtags,
                                                 LocalDateTime createdAt, String email, String nickname,
                                                 Set<ArticleCommentResponse> articleCommentResponses) {
        return new ArticleWithCommentsResponse(id, title, content, hashtags,
                createdAt, email, nickname, articleCommentResponses);
    }

    public static ArticleWithCommentsResponse from(ArticleWithCommentsDto dto) {
        String nickname = dto.userDto().nickname();
        if (nickname == null || nickname.isBlank()) {
            nickname = dto.userDto().email();
        }

        return new ArticleWithCommentsResponse(
                dto.id(),
                dto.title(),
                dto.content(),
                dto.hashtagDtos().stream()
                        .map(HashtagDto::hashtagName)
                        .collect(toUnmodifiableSet()),
                dto.createdAt(),
                dto.userDto().email(),
                nickname,
                dto.articleCommentDtos().stream()
                        .map(ArticleCommentResponse::from)
                        .collect(toCollection(LinkedHashSet::new))
        );
    }

}
