package com.example.boardproject.dto;


import com.example.boardproject.domain.Article;
import com.example.boardproject.domain.ArticleComment;
import com.example.boardproject.domain.User;

import java.time.LocalDateTime;

public record ArticleCommentDto(
        Long id,
        Long articleId,
        UserDto userDto,
        String content,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime modifiedAt,
        String modifiedBy
) {

    public static ArticleCommentDto of(Long articleId, UserDto userDto, String content) {
        return new ArticleCommentDto(null, articleId, userDto, content, null, null, null, null);
    }

    public static ArticleCommentDto of(Long id,
                                       Long articleId,
                                       UserDto userDto,
                                       String content,
                                       LocalDateTime createdAt,
                                       String createdBy,
                                       LocalDateTime modifiedAt,
                                       String modifiedBy) {
        return new ArticleCommentDto(id, articleId, userDto, content, createdAt, createdBy, modifiedAt, modifiedBy);
    }

    public static ArticleCommentDto from(ArticleComment entity) {
        return new ArticleCommentDto(
                entity.getId(),
                entity.getArticle().getId(),
                UserDto.from(entity.getUser()),
                entity.getContent(),
                entity.getCreatedAt(),
                entity.getCreatedBy(),
                entity.getModifiedAt(),
                entity.getModifiedBy()
        );
    }

    public ArticleComment toEntity(Article article, User user) {
        return ArticleComment.of(
                article,
                user,
                content
        );
    }

}
