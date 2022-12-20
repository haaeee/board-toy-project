package com.example.boardproject.dto.request;

import com.example.boardproject.dto.ArticleDto;
import com.example.boardproject.dto.HashtagDto;
import com.example.boardproject.dto.UserDto;
import java.util.Set;

public record ArticleRequest(
        String title,
        String content
) {

    public static ArticleRequest of(String title, String content) {
        return new ArticleRequest(title, content);
    }

    public ArticleDto toDto(UserDto userDto) {
        return toDto(userDto, null);
    }

    private ArticleDto toDto(UserDto userDto, Set<HashtagDto> hashtagDtos) {
        return ArticleDto.of(userDto, title, content, hashtagDtos);
    }
}
