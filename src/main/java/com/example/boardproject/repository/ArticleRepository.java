package com.example.boardproject.repository;

import com.example.boardproject.domain.Article;
import com.example.boardproject.domain.QArticle;
import com.example.boardproject.repository.querydsl.ArticleRepositoryCustom;
import com.querydsl.core.types.dsl.DateTimeExpression;
import com.querydsl.core.types.dsl.StringExpression;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

/**
 * QuerydslPredicateExecutor<Article>: 엔티티 내부의 모든 필드 조회를 제공한다.
 * QuerydslBinderCustomizer<QArticle>: 따라서 이를 바인딩 해줘야한다.
 */
@RepositoryRestResource
public interface ArticleRepository extends
        JpaRepository<Article, Long>,
        ArticleRepositoryCustom,
        QuerydslPredicateExecutor<Article>,
        QuerydslBinderCustomizer<QArticle> {

    Page<Article> findByTitleContaining(String title, Pageable pageable);

    Page<Article> findByContentContaining(String content, Pageable pageable);

    Page<Article> findByUser_EmailContaining(String email, Pageable pageable);

    Page<Article> findByUser_NicknameContaining(String nickname, Pageable pageable);

    void deleteByIdAndUser_Id(Long articleId, Long userId);

    @Override
    default void customize(QuerydslBindings bindings, QArticle root) {
        bindings.excludeUnlistedProperties(true);
        bindings.including(root.title, root.content, root.hashtags, root.createdAt, root.createdBy);
//        bindings.bind(root.title).first(StringExpression::likeIgnoreCase);  // like '${v}'
        bindings.bind(root.title).first(StringExpression::containsIgnoreCase);  // like '%${v}%'
        bindings.bind(root.content).first(StringExpression::containsIgnoreCase);
        bindings.bind(root.hashtags.any().hashtagName).first(StringExpression::containsIgnoreCase);
        bindings.bind(root.createdAt).first(DateTimeExpression::eq);
        bindings.bind(root.createdBy).first(StringExpression::containsIgnoreCase);
    }

    @Query("select a from Article a join fetch a.user where a.id = :articleId")
    Optional<Article> findWithUserById(@Param("articleId") Long articleId);
}
