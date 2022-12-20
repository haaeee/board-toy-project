package com.example.boardproject.service;

import com.example.boardproject.domain.Article;
import com.example.boardproject.domain.Hashtag;
import com.example.boardproject.domain.User;
import com.example.boardproject.domain.constant.SearchType;
import com.example.boardproject.dto.ArticleDto;
import com.example.boardproject.dto.ArticleWithCommentsDto;
import com.example.boardproject.dto.HashtagDto;
import com.example.boardproject.dto.UserDto;
import com.example.boardproject.repository.ArticleRepository;
import com.example.boardproject.repository.HashtagRepository;
import com.example.boardproject.repository.UserRepository;
import java.util.HashSet;
import java.util.Set;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toUnmodifiableSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.BDDAssertions.as;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.*;

@DisplayName("비즈니스 로직 - 게시글")
@DisplayNameGeneration(ReplaceUnderscores.class)
@ExtendWith(MockitoExtension.class)
class ArticleServiceTest {

    // sut: system under test
    // @InjectMocks 은 온전히 생성자 주입은 어려움 Target: Field
    @InjectMocks
    private ArticleService sut;
    @Mock
    private HashtagService hashtagService;
    @Mock
    private HashtagRepository hashtagRepository;
    @Mock
    private ArticleRepository articleRepository;
    @Mock
    private UserRepository userRepository;

    @Test
    void 검색어_없이_게시글을_검색하면_게시글_페이지를_반환한다() {
        // Given
        Pageable pageable = Pageable.ofSize(20);
        given(articleRepository.findAll(pageable)).willReturn(Page.empty());

        // When
        Page<ArticleDto> articles = sut.searchArticles(null, null, pageable);

        // Then
        assertThat(articles).isEmpty();
        then(articleRepository).should().findAll(pageable);
    }

    @Test
    void 검색어와_함께_게시글을_검색하면_게시글_페이지를_반환한다() {
        // Given
        SearchType searchType = SearchType.TITLE;
        String searchKeyword = "title";
        Pageable pageable = Pageable.ofSize(20);
        given(articleRepository.findByTitleContaining(searchKeyword, pageable)).willReturn(Page.empty());

        // When
        Page<ArticleDto> articles = sut.searchArticles(searchType, searchKeyword, pageable);

        // Then
        assertThat(articles).isEmpty();
        then(articleRepository).should().findByTitleContaining(searchKeyword, pageable);
    }

    @Test
    void 검색어_없이_게시글을_해시태그_검색하면_빈_페이지를_반환한다() {
        // Given
        Pageable pageable = Pageable.ofSize(20);

        // When
        Page<ArticleDto> articles = sut.searchArticlesViaHashtag(null, pageable);

        // Then
        assertThat(articles).isEqualTo(Page.empty(pageable));
        then(hashtagRepository).shouldHaveNoInteractions();
        then(articleRepository).shouldHaveNoInteractions();
    }

    @Test
    void 없는_해시태그를_검색하면_빈_페이지를_반환한다() {
        // Given
        String hashtagName = "없는 해시태그";
        Pageable pageable = Pageable.ofSize(20);
        given(articleRepository.findByHashtagNames(List.of(hashtagName), pageable)).willReturn(
                new PageImpl<>(List.of(), pageable, 0));

        // When
        Page<ArticleDto> articles = sut.searchArticlesViaHashtag(hashtagName, pageable);

        // Then
        assertThat(articles).isEqualTo(Page.empty(pageable));
        then(articleRepository).should().findByHashtagNames(List.of(hashtagName), pageable);
    }

    @Test
    void 게시글을_해시태그_검색하면_게시글_페이지를_반환한다() {
        // Given
        String hashtagName = "spring";
        Pageable pageable = Pageable.ofSize(20);
        Article expectedArticle = createArticle();
        given(articleRepository.findByHashtagNames(List.of(hashtagName), pageable))
                .willReturn(new PageImpl<>(List.of(expectedArticle), pageable, 1));

        // When
        Page<ArticleDto> articles = sut.searchArticlesViaHashtag(hashtagName, pageable);

        // Then
        assertThat(articles).isEqualTo(new PageImpl<>(List.of(ArticleDto.from(expectedArticle)), pageable, 1));
        then(articleRepository).should().findByHashtagNames(List.of(hashtagName), pageable);
    }

    @Test
    void 게시글_Id로_조회하면_댓글_담긴_게시글을_반환한다() {
        // Given
        Long articleId = 1L;
        Article article = createArticle();
        given(articleRepository.findById(articleId)).willReturn(Optional.of(article));

        // When
        ArticleWithCommentsDto dto = sut.getArticleWithComments(articleId);

        // Then
        assertThat(dto)
                .hasFieldOrPropertyWithValue("title", article.getTitle())
                .hasFieldOrPropertyWithValue("content", article.getContent())
                .hasFieldOrPropertyWithValue("hashtagDtos", article.getHashtags().stream()
                        .map(HashtagDto::from)
                        .collect(toUnmodifiableSet()));
        then(articleRepository).should().findById(articleId);
    }

    @Test
    void 댓글_달린_게시글이_없으면_예외를_던진다() {
        // Given
        Long articleId = 0L;
        given(articleRepository.findById(articleId)).willReturn(Optional.empty());

        // When
        Throwable t = catchThrowable(() -> sut.getArticleWithComments(articleId));

        // Then
        assertThat(t)
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("게시글이 존재하지 않습니다. - articleId: " + articleId);
        then(articleRepository).should().findById(articleId);
    }

    @Test
    void 게시글을_조회하면_게시글을_반환한다() {
        // Given
        Long articleId = 1L;
        Article article = createArticle();
        given(articleRepository.findById(articleId)).willReturn(Optional.of(article));

        // When
        ArticleDto dto = sut.getArticle(articleId);

        // Then
        assertThat(dto)
                .hasFieldOrPropertyWithValue("title", article.getTitle())
                .hasFieldOrPropertyWithValue("content", article.getContent())
                .hasFieldOrPropertyWithValue("hashtagDtos", article.getHashtags().stream()
                        .map(HashtagDto::from)
                        .collect(toUnmodifiableSet()));
        then(articleRepository).should().findById(articleId);
    }

    @Test
    void 게시글이_존재하지_않으면_예외를_던진다() {
        // Given
        Long articleId = 0L;
        given(articleRepository.findById(articleId)).willReturn(Optional.empty());

        // When
        Throwable t = catchThrowable(() -> sut.getArticleWithComments(articleId));

        // Then
        assertThat(t)
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("게시글이 존재하지 않습니다. - articleId: " + articleId);
        then(articleRepository).should().findById(articleId);
    }

    @Test
    void 게시글_정보를_입력하면_본문에서_해시태그_정보를_추출하여_해시태그_정보가_포함된_게시글을_생성한다() {
        // Given
        ArticleDto dto = createArticleDto();
        Set<String> expectedHashtagNames = Set.of("java", "spring");
        Set<Hashtag> expectedHashtags = new HashSet<>();
        expectedHashtags.add(createHashtag("java"));
        given(userRepository.getReferenceById(dto.userDto().id())).willReturn(createUser());
        given(hashtagService.parseHashtagNames(dto.content())).willReturn(expectedHashtagNames);
        given(hashtagService.findHashtagsByNames(expectedHashtagNames)).willReturn(expectedHashtags);
        given(articleRepository.save(any(Article.class))).willReturn(createArticle());

        // When
        sut.saveArticle(dto);

        // Then
        then(userRepository).should().getReferenceById(dto.userDto().id());
        then(hashtagService).should().parseHashtagNames(dto.content());
        then(hashtagService).should().findHashtagsByNames(expectedHashtagNames);
        then(articleRepository).should().save(any(Article.class));
    }

    @Test
    void 게시글의_수정_정보를_입력하면_게시글을_수정한다() {
        // Given
        Article article = createArticle();
        ArticleDto dto = createArticleDto("새 타이틀", "새 내용 #springboot");
        Set<String> expectedHashtagNames = Set.of("springboot");
        Set<Hashtag> expectedHashtags = new HashSet<>();

        given(articleRepository.getReferenceById(dto.id())).willReturn(article);
        given(userRepository.getReferenceById(dto.userDto().id())).willReturn(dto.userDto().toEntity());
        willDoNothing().given(articleRepository).flush();
        willDoNothing().given(hashtagService).deleteHashtagWithoutArticles(any());
        given(hashtagService.parseHashtagNames(dto.content())).willReturn(expectedHashtagNames);
        given(hashtagService.findHashtagsByNames(expectedHashtagNames)).willReturn(expectedHashtags);

        // When
        sut.updateArticle(dto.id(), dto);

        // Then
        assertThat(article)
                .hasFieldOrPropertyWithValue("title", dto.title())
                .hasFieldOrPropertyWithValue("content", dto.content())
                .extracting("hashtags", as(InstanceOfAssertFactories.COLLECTION))
                .hasSize(1)
                .extracting("hashtagName")
                .containsExactly("springboot");

        then(articleRepository).should().getReferenceById(dto.id());
        then(userRepository).should().getReferenceById(dto.userDto().id());
        then(articleRepository).should().flush();
        then(hashtagService).should(times(2)).deleteHashtagWithoutArticles(any());
        then(hashtagService).should().parseHashtagNames(dto.content());
        then(hashtagService).should().findHashtagsByNames(expectedHashtagNames);
    }

    @Test
    void 없는_게시글의_수정_정보를_입력하면_경고_로그를_찍고_아무_것도_하지_않는다() {
        // Given
        ArticleDto dto = createArticleDto("새 타이틀", "새 내용");
        given(articleRepository.getReferenceById(dto.id())).willThrow(EntityNotFoundException.class);

        // When
        sut.updateArticle(dto.id(), dto);

        // Then
        then(articleRepository).should().getReferenceById(dto.id());
    }

    @Test
    void 게시글_작성자가_아닌_사람이_수정_정보를_입력하면_아무_것도_하지_않는다() {
        // Given
        Long differentArticleId = 22L;
        Article differentArticle = createArticle(differentArticleId);
        differentArticle.setUser(createUser("newUser"));

        ArticleDto articleDto = createArticleDto("새 타이틀", "새 내용");
        given(articleRepository.getReferenceById(differentArticleId)).willReturn(differentArticle);
        given(userRepository.getReferenceById(articleDto.userDto().id())).willReturn(articleDto.userDto().toEntity());

        // When
        sut.updateArticle(differentArticleId, articleDto);

        // Then
        then(articleRepository).should().getReferenceById(differentArticleId);
        then(userRepository).should().getReferenceById(articleDto.userDto().id());
        then(hashtagService).shouldHaveNoInteractions();
    }

    @Test
    void 게시글의_ID_를_입력하면_게시글을_삭제한다() {
        // Given
        Long articleId = 1L;
        String userEmail = "jm@email.com";
        given(articleRepository.getReferenceById(articleId)).willReturn(createArticle());
        willDoNothing().given(articleRepository).deleteByIdAndUser_Email(articleId, userEmail);
        willDoNothing().given(articleRepository).flush();
        willDoNothing().given(hashtagService).deleteHashtagWithoutArticles(any());

        // When
        sut.deleteArticle(1L, userEmail);

        // Then
        then(articleRepository).should().getReferenceById(articleId);
        then(articleRepository).should().deleteByIdAndUser_Email(articleId, userEmail);
        then(articleRepository).should().flush();
        then(hashtagService).should(times(2)).deleteHashtagWithoutArticles(any());
    }

    @Test
    void 게시글_수를_조회하면_게시글_수를_반환한다() {
        // Given
        long expected = 0L;
        given(articleRepository.count()).willReturn(expected);

        // When
        long actual = sut.getArticleCount();

        // Then
        assertThat(actual).isEqualTo(expected);
        then(articleRepository).should().count();
    }

    @Test
    void 해시태그를_조회하면_unique_해시태그_리스트를_반환한다() {
        // Given
        List<String> expectedHashtags = List.of("java", "spring", "boot");
        given(hashtagService.findAllHashtagNames()).willReturn(expectedHashtags);

        // When
        List<String> actualHashtags = sut.getHashtags();

        // Then
        assertThat(actualHashtags).isEqualTo(expectedHashtags);
        then(hashtagService).should().findAllHashtagNames();
    }


    private ArticleDto createArticleDto() {
        return createArticleDto("title", "content");
    }

    private ArticleDto createArticleDto(String title, String content) {
        return ArticleDto.of(
                1L,
                createUserDto(),
                title,
                content,
                null,
                LocalDateTime.now(),
                "Jm",
                LocalDateTime.now(),
                "Jm");
    }

    private UserDto createUserDto() {
        return UserDto.of(
                1L,
                "jm@email.com",
                "password",
                "jm",
                "memo",
                LocalDateTime.now(),
                "jm",
                LocalDateTime.now(),
                "jm"
        );
    }

    private HashtagDto createHashtagDto() {
        return HashtagDto.of("java");
    }

    private Hashtag createHashtag(String hashtagName) {
        return createHashtag(1L, hashtagName);
    }

    private Hashtag createHashtag(Long id, String hashtagName) {
        Hashtag hashtag = Hashtag.of(hashtagName);
        ReflectionTestUtils.setField(hashtag, "id", id);
        return hashtag;
    }

    private Article createArticle() {
        return createArticle(1L);
    }

    private Article createArticle(Long id) {
        Article article = Article.of(
                createUser(),
                "title",
                "content"
        );

        article.addHashtags(Set.of(
                createHashtag(1L, "java"),
                createHashtag(2L, "spring")
        ));

        ReflectionTestUtils.setField(article, "id", id);

        return article;
    }

    private User createUser() {
        return createUser("jm@email.com");
    }

    private User createUser(String userEmail) {
        return User.of(userEmail, "password", "jm", "memo");
    }

}
