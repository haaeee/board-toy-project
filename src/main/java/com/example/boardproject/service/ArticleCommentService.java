package com.example.boardproject.service;

import com.example.boardproject.domain.Article;
import com.example.boardproject.domain.ArticleComment;
import com.example.boardproject.domain.User;
import com.example.boardproject.dto.ArticleCommentDto;
import com.example.boardproject.repository.ArticleCommentRepository;
import com.example.boardproject.repository.ArticleRepository;
import com.example.boardproject.repository.UserRepository;
import java.util.List;
import javax.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class ArticleCommentService {

    private final ArticleRepository articleRepository;
    private final ArticleCommentRepository articleCommentRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<ArticleCommentDto> searchArticleComments(Long articleId) {
        return articleCommentRepository.findWithUserByArticle_Id(articleId)
                .stream()
                .map(ArticleCommentDto::from)
                .toList();
    }

    public void saveArticleComment(ArticleCommentDto dto) {
        try {
            Article article = articleRepository.getReferenceById(dto.articleId());
            User user = userRepository.getReferenceById(dto.userDto().id());
            ArticleComment articleComment = dto.toEntity(article, user);

            if (dto.hasParentComment()) {
                ArticleComment parentComment = articleCommentRepository.getReferenceById(dto.parentCommentId());
                parentComment.addChildComment(articleComment);
            } else {
                articleCommentRepository.save(articleComment);
            }
        } catch (EntityNotFoundException e) {
            log.warn("댓글 저장 실패. 댓글 작성에 필요한 정보를 찾을 수 없습니다. - {}", e.getLocalizedMessage());
        }
    }

    public void updateArticleComment(ArticleCommentDto dto) {
        try {
            ArticleComment articleComment = articleCommentRepository.getReferenceById(dto.id());
            if (dto.content() != null) {
                articleComment.setContent(dto.content());
            }
        } catch (EntityNotFoundException e) {
            log.warn("댓글 업데이트 실패. 댓글을 찾을 수 없습니다. - dto: {}", dto);
        }

    }

    public void deleteArticleComment(Long articleCommentId, Long userId) {
        articleCommentRepository.deleteByIdAndUser_Id(articleCommentId, userId);
    }

}
