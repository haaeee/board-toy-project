package com.example.boardproject.domain;

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

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
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
    @Column(nullable = false, length = 500)
    private String content;  // 본문

    protected ArticleComment() {
    }

    private ArticleComment(Article article, User user, String content) {
        this.article = article;
        this.user = user;
        this.content = content;
    }

    public static ArticleComment of(Article article, User user, String content) {
        return new ArticleComment(article, user, content);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ArticleComment that)) return false;
        return this.getId() != null && this.getId().equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getId());
    }
}
