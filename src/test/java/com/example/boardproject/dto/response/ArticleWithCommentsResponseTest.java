package com.example.boardproject.dto.response;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.boardproject.dto.ArticleCommentDto;
import com.example.boardproject.dto.ArticleWithCommentsDto;
import com.example.boardproject.dto.HashtagDto;
import com.example.boardproject.dto.UserDto;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@DisplayName("DTO - 댓글을 포함한 게시글 응답 테스트")
@DisplayNameGeneration(ReplaceUnderscores.class)
class ArticleWithCommentsResponseTest {

    @Test
    void 자식_댓글이_없는_게시글__댓글_dto_를_api_응답으로_변환할_때__댓글을_시간_내림차순_ID_오름차순으로_정리한다() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        Set<ArticleCommentDto> articleCommentDtos = Set.of(
                createArticleCommentDto(1L, null, now),
                createArticleCommentDto(2L, null, now.plusDays(1L)),
                createArticleCommentDto(3L, null, now.plusDays(3L)),
                createArticleCommentDto(4L, null, now),
                createArticleCommentDto(5L, null, now.plusDays(5L)),
                createArticleCommentDto(6L, null, now.plusDays(4L)),
                createArticleCommentDto(7L, null, now.plusDays(2L)),
                createArticleCommentDto(8L, null, now.plusDays(7L))
        );
        ArticleWithCommentsDto articleWithCommentsDto = createArticleWithCommentsDto(articleCommentDtos);

        // When
        ArticleWithCommentsResponse actual = ArticleWithCommentsResponse.from(articleWithCommentsDto);

        // Then
        assertThat(actual.articleCommentsResponse())
                .containsExactly(
                        createArticleCommentResponse(8L, null, now.plusDays(7L)),
                        createArticleCommentResponse(5L, null, now.plusDays(5L)),
                        createArticleCommentResponse(6L, null, now.plusDays(4L)),
                        createArticleCommentResponse(3L, null, now.plusDays(3L)),
                        createArticleCommentResponse(7L, null, now.plusDays(2L)),
                        createArticleCommentResponse(2L, null, now.plusDays(1L)),
                        createArticleCommentResponse(1L, null, now),
                        createArticleCommentResponse(4L, null, now)
                );
    }

    @Test
    void 게시글_댓글_dto를_api_응답으로_변환할_때_댓글_부모_자식_관계를_각각의_규칙으로_정렬하여_정리한다() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        Set<ArticleCommentDto> articleCommentDtos = Set.of(
                createArticleCommentDto(1L, null, now),
                createArticleCommentDto(2L, 1L, now.plusDays(1L)),
                createArticleCommentDto(3L, 1L, now.plusDays(3L)),
                createArticleCommentDto(4L, 1L, now),
                createArticleCommentDto(5L, null, now.plusDays(5L)),
                createArticleCommentDto(6L, null, now.plusDays(4L)),
                createArticleCommentDto(7L, 6L, now.plusDays(2L)),
                createArticleCommentDto(8L, 6L, now.plusDays(7L))
        );
        ArticleWithCommentsDto input = createArticleWithCommentsDto(articleCommentDtos);

        // When
        ArticleWithCommentsResponse actual = ArticleWithCommentsResponse.from(input);

        // Then
        assertThat(actual.articleCommentsResponse())
                .containsExactly(
                        createArticleCommentResponse(5L, null, now.plusDays(5L)),
                        createArticleCommentResponse(6L, null, now.plusDays(4L)),
                        createArticleCommentResponse(1L, null, now)
                )
                .flatExtracting(ArticleCommentResponse::childComments)
                .containsExactly(
                        createArticleCommentResponse(7L, 6L, now.plusDays(2L)),
                        createArticleCommentResponse(8L, 6L, now.plusDays(7L)),
                        createArticleCommentResponse(4L, 1L, now),
                        createArticleCommentResponse(2L, 1L, now.plusDays(1L)),
                        createArticleCommentResponse(3L, 1L, now.plusDays(3L))
                );
    }

    @Test
    void 게시글__댓글_dto를_api_응답으로_변환할_때_부모_자식_관계_깊이_depth는_제한이_없다() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        Set<ArticleCommentDto> articleCommentDtos = Set.of(
                createArticleCommentDto(1L, null, now),
                createArticleCommentDto(2L, 1L, now.plusDays(1L)),
                createArticleCommentDto(3L, 2L, now.plusDays(2L)),
                createArticleCommentDto(4L, 3L, now.plusDays(3L)),
                createArticleCommentDto(5L, 4L, now.plusDays(4L)),
                createArticleCommentDto(6L, 5L, now.plusDays(5L)),
                createArticleCommentDto(7L, 6L, now.plusDays(6L)),
                createArticleCommentDto(8L, 7L, now.plusDays(7L))
        );
        ArticleWithCommentsDto input = createArticleWithCommentsDto(articleCommentDtos);

        // When
        ArticleWithCommentsResponse actual = ArticleWithCommentsResponse.from(input);

        // Then
        Iterator<ArticleCommentResponse> iterator = actual.articleCommentsResponse().iterator();
        long i = 1L;
        while (iterator.hasNext()) {
            ArticleCommentResponse articleCommentResponse = iterator.next();
            assertThat(articleCommentResponse)
                    .hasFieldOrPropertyWithValue("id", i)
                    .hasFieldOrPropertyWithValue("parentCommentId", i == 1L ? null : i - 1L)
                    .hasFieldOrPropertyWithValue("createdAt", now.plusDays(i - 1L));

            iterator = articleCommentResponse.childComments().iterator();
            i++;
        }
    }

    private ArticleWithCommentsDto createArticleWithCommentsDto(Set<ArticleCommentDto> articleCommentDtos) {
        return ArticleWithCommentsDto.of(
                1L,
                createUserDto(),
                articleCommentDtos,
                "title",
                "content",
                Set.of(HashtagDto.of("java")),
                LocalDateTime.now(),
                "jm",
                LocalDateTime.now(),
                "jm"
        );
    }

    private ArticleCommentDto createArticleCommentDto(final Long id, final Long parentCommentId,
                                                      final LocalDateTime createdAt) {
        return ArticleCommentDto.of(id, 1L, createUserDto(), parentCommentId, "test content" + id, createdAt, "jm",
                createdAt, "jm");
    }

    private ArticleCommentResponse createArticleCommentResponse(final Long id, final Long parentCommentId,
                                                                final LocalDateTime createdAt) {
        return ArticleCommentResponse.of(id, "test content" + id, createdAt, "jm@email.com", "jm", parentCommentId);
    }

    private UserDto createUserDto() {
        return UserDto.of(
                1L,
                "jm@email.com",
                "password",
                "jm",
                "test Memo",
                LocalDateTime.now(),
                "jm",
                LocalDateTime.now(),
                "jm"
        );
    }
}