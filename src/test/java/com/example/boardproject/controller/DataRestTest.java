package com.example.boardproject.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.head;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@Disabled("Spring Data REST 통합 테스트에는 불필요함으로 제외시킴")
@DisplayName("DATA REST API 테스트")
@Transactional  // default: RollBack
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
 class DataRestTest {

    private final MockMvc mvc;

     DataRestTest(@Autowired MockMvc mvc) {
        this.mvc = mvc;
    }

    @DisplayName("[api] 게시글 리스트 조회")
    @Test
    void givenNothing_whenRequestingArticle_thenReturnsArticlesJsonResponse() throws Exception {
        // given
        // when & then
        mvc.perform(get("/api/articles"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.valueOf("application/hal+json")))
                .andDo(print());
        // import 안 나올 때 ctrl + space, static 으로 가져올 때는 option + enter
    }

    @DisplayName("[api] 게시글 단건 조회")
    @Test
    void givenNothing_whenRequestingArticle_thenReturnsArticleJsonResponse() throws Exception {
        // given
        // when & then
        mvc.perform(get("/api/articles/111"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.valueOf("application/hal+json")));
        // import 안 나올 때 ctrl + space, static 으로 가져올 때는 option + enter
    }

    @DisplayName("[api] 게시글을 통해 댓글 리스트 조회")
    @Test
    void givenNothing_whenRequestingArticleCommentsFromArticle_thenReturnsArticleCommentsJsonResponse()
            throws Exception {
        // given
        // when & then
        mvc.perform(get("/api/articles/111"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.valueOf("application/hal+json")));
        // import 안 나올 때 ctrl + space, static 으로 가져올 때는 option + enter
    }

    @DisplayName("[api] 댓글 리스트 조회")
    @Test
    void givenNothing_whenRequestingArticleComments_thenReturnsArticleCommentsJsonResponse() throws Exception {
        // given
        // when & then
        mvc.perform(get("/api/articleComments"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.valueOf("application/hal+json")));
        // import 안 나올 때 ctrl + space, static 으로 가져올 때는 option + enter
    }

    @DisplayName("[api] 댓글 단건 조회")
    @Test
    void givenNothing_whenRequestingArticleComment_thenReturnsArticleCommentJsonResponse() throws Exception {
        // given
        // when & then
        mvc.perform(get("/api/articleComments/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.valueOf("application/hal+json")));
        // import 안 나올 때 ctrl + space, static 으로 가져올 때는 option + enter
    }

    @DisplayName("[api] 회원 관련 API 는 일체 제공하지 않는다.")
    @Test
    void givenNothing_whenRequestingUserAccounts_thenThrowsException() throws Exception {
        // Given

        // When & Then
        mvc.perform(get("/api/users")).andExpect(status().isNotFound());
        mvc.perform(post("/api/users")).andExpect(status().isNotFound());
        mvc.perform(put("/api/users")).andExpect(status().isNotFound());
        mvc.perform(patch("/api/users")).andExpect(status().isNotFound());
        mvc.perform(delete("/api/users")).andExpect(status().isNotFound());
        mvc.perform(head("/api/users")).andExpect(status().isNotFound());
    }
}
