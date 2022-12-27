package com.example.boardproject.dto;


import com.example.boardproject.domain.Article;
import com.example.boardproject.domain.ArticleComment;
import com.example.boardproject.domain.User;
import java.time.LocalDateTime;
import java.util.Objects;

public record ArticleCommentDto(
        Long id,
        Long articleId,
        UserDto userDto,
        Long parentCommentId,
        String content,
        LocalDateTime createdAt,
        String createdBy,
        LocalDateTime modifiedAt,
        String modifiedBy
) {

    public static ArticleCommentDto of(Long articleId, UserDto userDto, String content) {
        return ArticleCommentDto.of(articleId, userDto, null, content);
    }

    public static ArticleCommentDto of(Long articleId, UserDto userDto, Long parentCommentId, String content) {
        return ArticleCommentDto.of(null, articleId, userDto, parentCommentId, content, null, null, null, null);
    }

    public static ArticleCommentDto of(Long id, Long articleId, UserDto userDto, Long parentCommentId, String content,
                                       LocalDateTime createdAt, String createdBy, LocalDateTime modifiedAt,
                                       String modifiedBy) {

        return new ArticleCommentDto(id, articleId, userDto, parentCommentId, content, createdAt, createdBy, modifiedAt,
                modifiedBy);
    }

    public static ArticleCommentDto from(ArticleComment entity) {
        return new ArticleCommentDto(
                entity.getId(),
                entity.getArticle().getId(),
                UserDto.from(entity.getUser()),
                entity.getParentCommentId(),
                entity.getContent(),
                entity.getCreatedAt(),
                entity.getCreatedBy(),
                entity.getModifiedAt(),
                entity.getModifiedBy()
        );
    }

    /**
     * 부모 댓글 entity로 변경하고 entity에서 add method를 통해 자식 댓글 추가
     */
    public ArticleComment toEntity(Article article, User user) {
        return ArticleComment.of(
                article,
                user,
                content
        );
    }

    public boolean hasParentComment() {
        return !Objects.isNull(parentCommentId);
    }
}
