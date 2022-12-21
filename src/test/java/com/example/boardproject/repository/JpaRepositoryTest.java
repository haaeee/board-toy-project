package com.example.boardproject.repository;


import com.example.boardproject.config.P6spyConfiguration;
import com.example.boardproject.domain.Article;
import com.example.boardproject.domain.ArticleComment;
import com.example.boardproject.domain.Hashtag;
import com.example.boardproject.domain.User;
import java.util.Set;
import java.util.stream.Collectors;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("testdb")
@DisplayName("JPA 연결 테스트")
@DisplayNameGeneration(ReplaceUnderscores.class)
@Import({JpaRepositoryTest.TestJpaConfig.class, P6spyConfiguration.class})
@DataJpaTest
class JpaRepositoryTest {

    private final ArticleRepository articleRepository;
    private final ArticleCommentRepository articleCommentRepository;
    private final UserRepository userRepository;
    private final HashtagRepository hashtagRepository;

    JpaRepositoryTest(@Autowired ArticleRepository articleRepository,
                      @Autowired ArticleCommentRepository articleCommentRepository,
                      @Autowired UserRepository userRepository,
                      @Autowired HashtagRepository hashtagRepository) {
        this.articleRepository = articleRepository;
        this.articleCommentRepository = articleCommentRepository;
        this.userRepository = userRepository;
        this.hashtagRepository = hashtagRepository;
    }

    @Test
    void select_테스트() {
        // given

        // when
        List<Article> articles = articleRepository.findAll();

        // then
        assertThat(articles).isNotNull()
                .hasSize(123);
    }

    @Test
    void insert_테스트() {
        // given
        long previousCount = articleRepository.count();

        User user = userRepository.save(User.of("test@email.com", "pwd", null, null));
        Article article = Article.of(user, "new article", "new content");
        article.addHashtags(Set.of(Hashtag.of("spring")));

        // when
        articleRepository.save(article);

        // then
        assertThat(articleRepository.count()).isEqualTo(previousCount + 1);
    }

    @Test
    void update_테스트() {
        // given
        Article article = articleRepository.findById(1L).orElseThrow();
        Hashtag updatedHashtag = Hashtag.of("springboot");
        article.clearHashtags();
        article.addHashtags(Set.of(updatedHashtag));
        // when
        Article savedArticle = articleRepository.saveAndFlush(article);
//        articleRepository.flush();

        // then
        assertThat(savedArticle.getHashtags())
                .hasSize(1)
                .extracting("hashtagName", String.class)
                .containsExactly(updatedHashtag.getHashtagName());
    }

    @Test
    void delete_테스트() {
        // given
        Article article = articleRepository.findById(1L).orElseThrow();
        long previousArticleCount = articleRepository.count();
        long previousArticleCommentCount = articleCommentRepository.count();
        int deletedCommentsSize = article.getArticleComments().size();

        // when
        articleRepository.delete(article);

        // then
        assertThat(articleRepository.count()).isEqualTo(previousArticleCount - 1);
        assertThat(articleCommentRepository.count()).isEqualTo(previousArticleCommentCount - deletedCommentsSize);
    }

    @Test
    void 댓글에_대댓글_insert_테스트() {
        // Given
        ArticleComment parentComment = articleCommentRepository.findWithUserAndChildCommentsById(1L).get();
        ArticleComment childComment = ArticleComment.of(parentComment.getArticle(), parentComment.getUser(), "대댓글");

        // When
        parentComment.addChildComment(childComment);
        articleCommentRepository.flush();

        // Then
        assertThat(articleCommentRepository.findById(1L).get())
                .hasFieldOrPropertyWithValue("parentCommentId", null)
                .extracting("childComments", InstanceOfAssertFactories.COLLECTION)
                .hasSize(5);
    }

    @Test
    void 대댓글_조회_테스트() {
        // Given

        // When
        Optional<ArticleComment> parentComment = articleCommentRepository.findWithUserAndChildCommentsById(1L);

        // Then
        assertThat(parentComment.get())
                .hasFieldOrPropertyWithValue("parentCommentId", null)
                .extracting("childComments", InstanceOfAssertFactories.COLLECTION)
                .hasSize(4);
    }

    @Test
    void 댓글_삭제와_대댓글_전체_연동_삭제_테스트____댓글_ID_유저_ID() {
        // Given
        long previousArticleComment = articleCommentRepository.count();
        Set<Long> childIds = articleCommentRepository.findById(1L).get().getChildComments().stream()
                .map(ArticleComment::getId).collect(
                        Collectors.toUnmodifiableSet());

        // When
        articleCommentRepository.deleteByChildCommentId(childIds);
        articleCommentRepository.deleteByIdAndUser_Email(1L, "jm@email.com");

        // Then
        assertThat(articleCommentRepository.count()).isEqualTo(previousArticleComment - 5);
    }




    @Test
    void Querydsl_전체_hashtag_리스트에서_이름만_조회하기() {
        // Given

        // When
        List<String> hashtagNames = hashtagRepository.findAllHashtagNames();

        // Then
        assertThat(hashtagNames).hasSize(19);
    }

    @Test
    void Querydsl_hashtag_로_페이징된_게시글_검색하기() {
        // Given
        List<String> hashtagNames = List.of("blue", "crimson", "fuscia");
        Pageable pageable = PageRequest.of(0, 5, Sort.by(
                Sort.Order.desc("hashtags.hashtagName"),  // article Field
                Sort.Order.asc("title")
        ));

        // When
        Page<Article> articlePage = articleRepository.findByHashtagNames(hashtagNames, pageable);

        // Then
        assertThat(articlePage.getContent()).hasSize(pageable.getPageSize());
        assertThat(articlePage.getContent().get(0).getTitle()).isEqualTo("Fusce posuere felis sed lacus.");
        assertThat(articlePage.getContent().get(0).getHashtags())
                .extracting("hashtagName", String.class)
                .containsExactly("fuscia");
        assertThat(articlePage.getTotalElements()).isEqualTo(17);
        assertThat(articlePage.getTotalPages()).isEqualTo(4);
    }


    @EnableJpaAuditing
    @TestConfiguration
    static class TestJpaConfig {
        @Bean
        AuditorAware<String> auditorAware() {
            return () -> Optional.of("jaime");
        }
    }

}
