package com.example.boardproject.dto.request;

import com.example.boardproject.dto.ArticleCommentDto;
import com.example.boardproject.dto.UserDto;

public record ArticleCommentRequest(
        Long articleId,
        String content
) {
    public static ArticleCommentRequest of(Long articleId, String content) {
        return new ArticleCommentRequest(articleId, content);
    }

    public ArticleCommentDto toDto(UserDto userDto) {
        return ArticleCommentDto.of(
                articleId,
                userDto,
                content
        );
    }

}
