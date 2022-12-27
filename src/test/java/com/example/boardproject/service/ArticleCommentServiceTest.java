package com.example.boardproject.service;

import com.example.boardproject.domain.Article;
import com.example.boardproject.domain.ArticleComment;
import com.example.boardproject.domain.Hashtag;
import com.example.boardproject.domain.User;
import com.example.boardproject.dto.ArticleCommentDto;
import com.example.boardproject.dto.UserDto;
import com.example.boardproject.repository.ArticleCommentRepository;
import com.example.boardproject.repository.ArticleRepository;
import com.example.boardproject.repository.UserRepository;
import java.util.Set;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;


@DisplayName("비즈니스 로직 - 댓글")
@DisplayNameGeneration(ReplaceUnderscores.class)
@ExtendWith(MockitoExtension.class)
class ArticleCommentServiceTest {

    @InjectMocks
    private ArticleCommentService sut;

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private ArticleCommentRepository articleCommentRepository;

    @Mock
    private UserRepository userRepository;

    @Test
    void 게시글을_ID를_조회하면__게시글을_반환한다() {
        // Given
        Long articleId = 1L;
        ArticleComment expectedParentComment = createArticleComment(1L, "parent content");
        ArticleComment expectedChildComment = createArticleComment(2L, "child content");

        expectedChildComment.setParentCommentId(expectedParentComment.getId());
        given(articleCommentRepository.findWithUserByArticle_Id(articleId)).willReturn(List.of(
                expectedParentComment,
                expectedChildComment
        ));

        // When
        List<ArticleCommentDto> actual = sut.searchArticleComments(articleId);

        // Then
        assertThat(actual).hasSize(2);
        assertThat(actual)
                .extracting("id", "articleId", "parentCommentId", "content")
                .containsExactlyInAnyOrder(
                        Assertions.tuple(1L, 1L, null, "parent content"),
                        Assertions.tuple(2L, 1L, 1L, "child content")
                );
        then(articleCommentRepository).should().findWithUserByArticle_Id(articleId);
    }

    @Test
    void 댓글_정보를_입력하면__댓글을_저장한다() {
        // Given
        ArticleCommentDto dto = createArticleCommentDto("댓글");

        given(articleRepository.getReferenceById(dto.articleId())).willReturn(createArticle());
        given(articleCommentRepository.save(any(ArticleComment.class))).willReturn(null);
        given(userRepository.getReferenceById(dto.userDto().id())).willReturn(createUser());

        // When
        sut.saveArticleComment(dto);

        // Then
        then(articleRepository).should().getReferenceById(dto.articleId());
        then(userRepository).should().getReferenceById(dto.userDto().id());

        then(articleCommentRepository).should(never()).getReferenceById(anyLong());
        then(articleCommentRepository).should().save(any(ArticleComment.class));
    }

    @Test
    void 댓글_저장을_시도했는데_맞는_게시글이_없으면_경고_로그를_찍고_아무것도_안_한다() {
        // Given
        ArticleCommentDto dto = createArticleCommentDto("댓글");
        given(articleRepository.getReferenceById(dto.articleId())).willThrow(EntityNotFoundException.class);

        // When
        sut.saveArticleComment(dto);

        // Then
        then(articleRepository).should().getReferenceById(dto.articleId());
        then(articleCommentRepository).shouldHaveNoInteractions();
        then(userRepository).shouldHaveNoInteractions();
    }

    @Test
    void 부모_댓글_ID와_댓글_정보를_입력하면_대댓글을_저장한다() {
        // Given
        Long parentCommentId = 1L;
        ArticleComment parent = createArticleComment(parentCommentId, "댓글");
        ArticleCommentDto child = createArticleCommentDto(parentCommentId, "대댓글");
        given(articleRepository.getReferenceById(child.articleId())).willReturn(createArticle());
        given(userRepository.getReferenceById(child.userDto().id())).willReturn(createUser());
        given(articleCommentRepository.getReferenceById(child.parentCommentId())).willReturn(parent);

        // When
        sut.saveArticleComment(child);

        // Then
        assertThat(child.parentCommentId()).isNotNull();
        then(articleRepository).should().getReferenceById(child.articleId());
        then(userRepository).should().getReferenceById(child.userDto().id());
        then(articleCommentRepository).should().getReferenceById(child.parentCommentId());
        then(articleCommentRepository).should(never()).save(any(ArticleComment.class));
    }

//    @DisplayName("댓글 정보를 입력하면, 댓글을 수정한다.")
//    @Test
//    void givenArticleCommentInfo_whenUpdatingArticleComment_thenUpdatesArticleComment() {
//        // Given
//        String oldContent = "content";
//        String updatedContent = "댓글";
//        ArticleComment articleComment = createArticleComment(oldContent);
//        ArticleCommentDto dto = createArticleCommentDto(updatedContent);
//        given(articleCommentRepository.getReferenceById(dto.id())).willReturn(articleComment);
//
//        // When
//        sut.updateArticleComment(dto);
//
//        // Then
//        assertThat(articleComment.getContent())
//                .isNotEqualTo(oldContent)
//                .isEqualTo(updatedContent);
//        then(articleCommentRepository).should().getReferenceById(dto.id());
//    }

    @Test
    void 댓글_ID를_입력하면_댓글을_삭제한다() {
        // Given
        Long articleCommentId = 1L;
        Long userId = 1L;
        willDoNothing().given(articleCommentRepository).deleteByIdAndUser_Id(articleCommentId, userId);

        // When
        sut.deleteArticleComment(articleCommentId, userId);

        // Then
        then(articleCommentRepository).should().deleteByIdAndUser_Id(articleCommentId, userId);
    }

    private ArticleCommentDto createArticleCommentDto(String content) {
        return createArticleCommentDto(null, content);
    }

    private ArticleCommentDto createArticleCommentDto(Long parentCommentId, String content) {
        return createArticleCommentDto(1L, parentCommentId, content);
    }
    private ArticleCommentDto createArticleCommentDto(Long id, Long parentCommentId, String content) {
        return ArticleCommentDto.of(
                id,
                1L,
                createUserDto(),
                parentCommentId,
                content,
                LocalDateTime.now(),
                "jm",
                LocalDateTime.now(),
                "jm"
        );
    }

    private UserDto createUserDto() {
        return UserDto.of(
                1L,
                "jm@test.com",
                "password",
                "Jm",
                "Memo",
                LocalDateTime.now(),
                "jm",
                LocalDateTime.now(),
                "jm"
        );
    }

    private ArticleComment createArticleComment(Long id, String content) {
        ArticleComment articleComment = ArticleComment.of(
                createArticle(),
                createUser(),
                content
        );
        ReflectionTestUtils.setField(articleComment, "id", id);

        return articleComment;
    }

    private User createUser() {
        return User.of(
                "jm@email.com",
                "password",
                "jaime",
                null
        );
    }

    private Article createArticle() {
        Article article = Article.of(
                createUser(),
                "title",
                "content"
        );
        ReflectionTestUtils.setField(article, "id", 1L);
        article.addHashtags(Set.of(createHashtag(article)));
        return article;
    }

    private Hashtag createHashtag(Article article) {
        return Hashtag.of("spring");
    }
}
