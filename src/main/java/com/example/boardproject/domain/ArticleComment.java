package com.example.boardproject.domain;

import java.util.LinkedHashSet;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.Objects;

@Getter
@ToString
@Table(indexes = {
        @Index(columnList = "content"),
        @Index(columnList = "createdAt"),
        @Index(columnList = "createdBy")
})
@Entity
public class ArticleComment extends AuditingFields {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne(optional = false)
    @JoinColumn(name = "article_id")
    private Article article;  // 게시글 (id)

    @Setter
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user; // 유저 정보 (ID)

    @Setter
    @Column(updatable = false)
    private Long parentCommentId;

    @Setter
    @Column(nullable = false, length = 500)
    private String content;  // 본문

    @ToString.Exclude
    @OrderBy("createdAt ASC")
    @OneToMany(mappedBy = "parentCommentId", cascade = CascadeType.ALL)
    private Set<ArticleComment> childComments = new LinkedHashSet<>();

    protected ArticleComment() {
    }

    private ArticleComment(Article article, User user, Long parentCommentId, String content) {
        this.article = article;
        this.user = user;
        this.parentCommentId = parentCommentId;
        this.content = content;
    }

    public static ArticleComment of(Article article, User user, String content) {
        return new ArticleComment(article, user, null, content);
    }

    public void addChildComment(ArticleComment child) {
        child.setParentCommentId(this.getId());
        this.getChildComments().add(child);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ArticleComment that)) {
            return false;
        }
        return this.getId() != null && this.getId().equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId());
    }
}
