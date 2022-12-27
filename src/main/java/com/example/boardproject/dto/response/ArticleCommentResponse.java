package com.example.boardproject.dto.response;

import com.example.boardproject.dto.ArticleCommentDto;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

public record ArticleCommentResponse(
        Long id,
        String content,
        LocalDateTime createdAt,
        String email,
        String nickname,
        Long parentCommentId,
        Set<ArticleCommentResponse> childComments
) {

    public static ArticleCommentResponse of(Long id, String content, LocalDateTime createdAt, String email,
                                            String nickname) {
        return ArticleCommentResponse.of(id, content, createdAt, email, nickname, null);
    }

    public static ArticleCommentResponse of(Long id, String content, LocalDateTime createdAt, String email,
                                            String nickname, Long parentCommentId) {
        Comparator<ArticleCommentResponse> childCommentComparator = Comparator
                .comparing(ArticleCommentResponse::createdAt)
                .thenComparingLong(ArticleCommentResponse::id);

        return new ArticleCommentResponse(id, content, createdAt, email, nickname, parentCommentId,
                new TreeSet<>(childCommentComparator));
    }

    public static ArticleCommentResponse from(ArticleCommentDto dto) {
        String nickname = dto.userDto().nickname();
        if (nickname == null || nickname.isBlank()) {
            nickname = dto.userDto().email();
        }

        return ArticleCommentResponse.of(
                dto.id(),
                dto.content(),
                dto.createdAt(),
                dto.userDto().email(),
                nickname,
                dto.parentCommentId()
        );
    }

    public boolean hasParentComment() {
        return !Objects.isNull(parentCommentId);
    }

    public boolean hasChildComment() {
        return Objects.isNull(parentCommentId);
    }

}
