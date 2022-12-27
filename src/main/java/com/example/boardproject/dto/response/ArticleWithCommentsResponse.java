package com.example.boardproject.dto.response;

import static java.util.stream.Collectors.*;

import com.example.boardproject.dto.ArticleCommentDto;
import com.example.boardproject.dto.ArticleWithCommentsDto;

import com.example.boardproject.dto.HashtagDto;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

public record ArticleWithCommentsResponse(
        Long id,
        String title,
        String content,
        Set<String> hashtags,
        LocalDateTime createdAt,
        String email,
        String nickname,
        Set<ArticleCommentResponse> articleCommentsResponse
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
                organizeChildComments(dto.articleCommentDtos())
        );
    }

    private static Set<ArticleCommentResponse> organizeChildComments(Set<ArticleCommentDto> articleCommentDtos) {
        Map<Long, ArticleCommentResponse> map = articleCommentDtos.stream()
                .map(ArticleCommentResponse::from)
                .collect(Collectors.toMap(ArticleCommentResponse::id, Function.identity()));

        map.values().stream()
                .filter(ArticleCommentResponse::hasParentComment)
                .forEach(childComment -> {
                    ArticleCommentResponse parentComment = map.get(childComment.parentCommentId());

                    // articleCommentResponse의 Comparator
                    parentComment.childComments().add(childComment);
                });

        return map.values().stream()
                .filter(ArticleCommentResponse::hasChildComment)
                .collect(Collectors.toCollection(() ->
                        new TreeSet<>(Comparator.comparing(ArticleCommentResponse::createdAt)
                                .reversed()
                                .thenComparing(ArticleCommentResponse::id)
                        )
                ));
    }

}
