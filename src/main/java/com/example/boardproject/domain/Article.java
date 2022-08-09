package com.example.boardproject.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

@Getter
@ToString(callSuper = true)
@Table(indexes = {
        @Index(columnList = "title"),
        @Index(columnList = "hashtag"),
        @Index(columnList = "createdAt"),
        @Index(columnList = "createdBy")
})
@Entity
public class Article extends AuditingFields {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id")
    private User user;  // 유저 정보(ID)

    @Setter
    @Column(nullable = false)
    private String title;  // 제목

    @Setter
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;  // 본문

    @Setter
    private String hashtag;  // 해시태그

    // mappedBy 를 걸지 않으면 두 entity 이름을 합쳐서 테이블을 하나 만듬
    @ToString.Exclude
    @OrderBy("createdAt DESC")
    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL)
    private final Set<ArticleComment> articleComments = new LinkedHashSet<>();

    // public, protected no-arg constructor -> entity
    protected Article() {
    }

    private Article(User user, String title, String content, String hashtag) {
        this.user = user;
        this.title = title;
        this.content = content;
        this.hashtag = hashtag;
    }

    // factory method
    public static Article of(User user, String title, String content, String hashtag) {
        return new Article(user, title, content, hashtag);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        // Pattern Matching for instanceof in Java 14
        if (!(o instanceof Article article)) return false;
        return id != null && id.equals(article.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
