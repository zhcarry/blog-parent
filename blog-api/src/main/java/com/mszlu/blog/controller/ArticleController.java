package com.mszlu.blog.controller;

import com.mszlu.blog.common.aop.LogAnnotation;
import com.mszlu.blog.common.cache.Cache;
import com.mszlu.blog.service.ArticleService;
import com.mszlu.blog.vo.ArticleVo;
import com.mszlu.blog.vo.Result;
import com.mszlu.blog.vo.params.ArticleParam;
import com.mszlu.blog.vo.params.PageParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

// 使用json进行交互
@RestController
@RequestMapping("articles")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    /**
     * 首页 文章列表
     * @param params
     * @return
     */
    @PostMapping
    @LogAnnotation(module = "文章", operator = "文章列表")
    //@Cache(name = "listArticle")
    public Result listArticle(@RequestBody PageParams params) {
        return articleService.queryListArticle(params);
    }

    /**
     * 首页 最热文章
     * @return Result
     */
    @PostMapping("hot")
    public Result hotArticle() {
        int limit = 4;
        return articleService.queryHotArticle(limit);
    }

    /**
     * 首页 最新文章
     * @return Result
     */
    @PostMapping("new")
    public Result newArticle() {
        int limit = 4;
        return articleService.queryNewArticle(limit);
    }

    /**
     * 首页 文章归档
     * @return Result
     */
    @PostMapping("listArchives")
    public Result listArchives() {
        return articleService.queryArticleArchivesByCreateTime();
    }

    /**
     * 文章详情
     * @params id
     * @return Result
     */
    @PostMapping("view/{id}")
    public Result viewArticleId(@PathVariable("id") Long articleId) {
        return articleService.queryArticleByArticleId(articleId);
    }

    /**
     * 发布文章
     * @param articleParam
     * @return
     */
    @PostMapping("publish")
    public Result publish(@RequestBody ArticleParam articleParam) {
        return articleService.addArticle(articleParam);
    }
}
