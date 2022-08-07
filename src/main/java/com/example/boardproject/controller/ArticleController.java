package com.example.boardproject.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping("/articles")
@Controller
public class ArticleController {

    @GetMapping
    public String articles(Model model) {
        model.addAttribute("articles", List.of());
        return "articles/index";
    }

    @GetMapping("/{articleId}")
    public String articles(@PathVariable Long articleId, Model model) {
        model.addAttribute("article", "article");  // TODO: 구현할 때 여기에 실제 데이터 넣어야함
        model.addAttribute("articleComments", List.of());
        return "articles/detail";
    }
}
