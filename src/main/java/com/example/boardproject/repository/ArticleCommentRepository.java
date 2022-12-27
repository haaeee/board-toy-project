package com.example.boardproject.repository;

import com.example.boardproject.domain.ArticleComment;
import com.example.boardproject.domain.QArticleComment;
import com.querydsl.core.types.dsl.DateTimeExpression;
import com.querydsl.core.types.dsl.StringExpression;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.transaction.annotation.Transactional;

@RepositoryRestResource
public interface ArticleCommentRepository extends
        JpaRepository<ArticleComment, Long>,
        QuerydslPredicateExecutor<ArticleComment>,
        QuerydslBinderCustomizer<QArticleComment> {

    // Article Repo 의(_) Id
    @Query("select ac from ArticleComment ac join fetch ac.user join fetch ac.childComments where ac.article.id = :articleId")
    List<ArticleComment> findWithUserByArticle_Id(@Param("articleId") Long articleId);

    @Query("select ac from ArticleComment ac join fetch ac.user join fetch ac.childComments where ac.id = :id")
    Optional<ArticleComment> findWithUserAndChildCommentsById(@Param("id") Long id);

    @Modifying(clearAutomatically = true)
    @Query("delete from ArticleComment ac where ac.id = :id and ac.user.id = :userId")
    void deleteByIdAndUser_Id(Long id, Long userId);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("delete from ArticleComment ac where ac.id in :childIds")
    void deleteByChildCommentId(@Param("childIds") Collection<Long> childIds);

    @Override
    default void customize(QuerydslBindings bindings, QArticleComment root) {
        bindings.excludeUnlistedProperties(true);
        bindings.including(root.content, root.createdAt, root.createdBy);
        bindings.bind(root.content).first(StringExpression::containsIgnoreCase);
        bindings.bind(root.createdAt).first(DateTimeExpression::eq);
        bindings.bind(root.createdBy).first(StringExpression::containsIgnoreCase);
    }
}
