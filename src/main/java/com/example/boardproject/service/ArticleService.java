package com.example.boardproject.service;

import static java.util.stream.Collectors.*;

import com.example.boardproject.domain.Article;
import com.example.boardproject.domain.Hashtag;
import com.example.boardproject.domain.User;
import com.example.boardproject.domain.constant.SearchType;
import com.example.boardproject.dto.ArticleDto;
import com.example.boardproject.dto.ArticleWithCommentsDto;
import com.example.boardproject.repository.ArticleRepository;
import com.example.boardproject.repository.HashtagRepository;
import com.example.boardproject.repository.UserRepository;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Objects;

@Slf4j
@Transactional
@Service
public class ArticleService {

    private final HashtagService hashtagService;
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final HashtagRepository hashtagRepository;

    public ArticleService(final HashtagService hashtagService, final ArticleRepository articleRepository,
                          final UserRepository userRepository,
                          final HashtagRepository hashtagRepository) {
        this.hashtagService = hashtagService;
        this.articleRepository = articleRepository;
        this.userRepository = userRepository;
        this.hashtagRepository = hashtagRepository;
    }

    @Transactional(readOnly = true)
    public Page<ArticleDto> searchArticles(SearchType searchType, String searchKeyword, Pageable pageable) {
        if (searchKeyword == null || searchKeyword.isBlank()) {
            return articleRepository.findAll(pageable).map(ArticleDto::from);
        }

        return switch (searchType) {
            case TITLE -> articleRepository.findByTitleContaining(searchKeyword, pageable).map(ArticleDto::from);
            case CONTENT -> articleRepository.findByContentContaining(searchKeyword, pageable).map(ArticleDto::from);
            case EMAIL -> articleRepository.findByUser_EmailContaining(searchKeyword, pageable).map(ArticleDto::from);
            case NICKNAME ->
                    articleRepository.findByUser_NicknameContaining(searchKeyword, pageable).map(ArticleDto::from);
            case HASHTAG ->
                    articleRepository.findByHashtagNames(Arrays.stream(searchKeyword.split(" ")).toList(), pageable)
                            .map(ArticleDto::from);
        };
    }

    @Transactional(readOnly = true)
    public ArticleWithCommentsDto getArticleWithComments(Long articleId) {
        return articleRepository.findWithUserById(articleId)
                .map(ArticleWithCommentsDto::from)
                .orElseThrow(() -> new EntityNotFoundException("게시글이 존재하지 않습니다. - articleId: " + articleId));
    }

    @Transactional(readOnly = true)
    public ArticleDto getArticle(Long articleId) {
        return articleRepository.findById(articleId)
                .map(ArticleDto::from)
                .orElseThrow(() -> new EntityNotFoundException("게시글이 존재하지 않습니다. - articleId: " + articleId));
    }

    public void saveArticle(ArticleDto articleDto) {
        User user = userRepository.getReferenceById(articleDto.userDto().id());
        Set<Hashtag> hashtags = renewHashtagsFromContent(articleDto.content());

        Article article = articleDto.toEntity(user);

        // entity cascade
        article.addHashtags(hashtags);
        articleRepository.save(articleDto.toEntity(user));
    }


    public void updateArticle(Long articleId, ArticleDto articleDto) {
        try {
            Article article = articleRepository.getReferenceById(articleId);
            User user = userRepository.getReferenceById(articleDto.userDto().id());

            if (Objects.equals(user.getEmail(), article.getUser().getEmail())) {
                if (articleDto.title() != null) {
                    article.setTitle(articleDto.title());
                }
                if (articleDto.content() != null) {
                    article.setContent(articleDto.content());
                }

                Set<Long> hashtagIds = article.getHashtags().stream()
                        .map(Hashtag::getId)
                        .collect(toUnmodifiableSet());

                article.clearHashtags();
                // TODO: 직접 delete query 작성 필요
                articleRepository.flush();

                hashtagIds.forEach(hashtagService::deleteHashtagWithoutArticles);

                Set<Hashtag> hashtags = renewHashtagsFromContent(article.getContent());
                article.addHashtags(hashtags);
            }
        } catch (EntityNotFoundException e) {
            log.warn("게시글 업데이트 실패. 게시글을 수정하는데 필요한 정보를 찾을 수 없습니다. - {}", e.getLocalizedMessage());
        }
    }

    public void deleteArticle(Long articleId, String userEmail) {
        Article article = articleRepository.getReferenceById(articleId);

        Set<Long> hashtagIds = article.getHashtags().stream()
                .map(Hashtag::getId)
                .collect(toUnmodifiableSet());

        articleRepository.deleteByIdAndUser_Email(articleId, userEmail);
        articleRepository.flush();

        hashtagIds.forEach(hashtagService::deleteHashtagWithoutArticles);
    }

    @Transactional(readOnly = true)
    public long getArticleCount() {
        return articleRepository.count();
    }

    @Transactional(readOnly = true)
    public Page<ArticleDto> searchArticlesViaHashtag(String hashtagName, Pageable pageable) {
        if (Objects.isNull(hashtagName) || hashtagName.isBlank()) {
            return Page.empty(pageable);
        }

        return articleRepository.findByHashtagNames(List.of(hashtagName), pageable)
                .map(ArticleDto::from);
    }

    @Transactional(readOnly = true)
    public List<String> getHashtags() {
        return hashtagService.findAllHashtagNames();
    }

    private Set<Hashtag> renewHashtagsFromContent(final String content) {
        Set<String> hashtagNamesInContent = hashtagService.parseHashtagNames(content);
        Set<Hashtag> hashtags = hashtagService.findHashtagsByNames(hashtagNamesInContent);

        Set<String> existingHashtagNames = hashtags.stream()
                .map(Hashtag::getHashtagName)
                .collect(toUnmodifiableSet());

        hashtagNamesInContent.forEach(newHashtagName -> {
            if (!existingHashtagNames.contains(newHashtagName)) {
                hashtags.add(Hashtag.of(newHashtagName));
            }
        });

        return hashtags;
    }
}
