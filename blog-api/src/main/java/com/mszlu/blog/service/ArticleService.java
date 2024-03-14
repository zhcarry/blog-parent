package com.mszlu.blog.service;

import com.mszlu.blog.vo.ArticleVo;
import com.mszlu.blog.vo.Result;
import com.mszlu.blog.vo.params.ArticleParam;
import com.mszlu.blog.vo.params.PageParams;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface ArticleService {
    /**
     *  分页查询文章列表
      * @param params
     * @return
     */
    Result queryListArticle(PageParams params);

    /**
     * 查询最热文章
     * @param limit
     * @return Result
     */
    Result queryHotArticle(int limit);

    /**
     * 查询最新文章
     * @param limit
     * @return Result
     */
    Result queryNewArticle(int limit);

    /**
     * 查询文章归档
     * @return Result
     */
    Result queryArticleArchivesByCreateTime();

    /**
     * 文章详情
     * @param ArticleId => 文章id
     * @return Result
     */
    Result queryArticleByArticleId(Long ArticleId);

    /**
     * 发布文章
     * @param articleParam
     * @return
     */
    Result addArticle(ArticleParam articleParam);

    /**
     * 文章评论数+1
     */
    void updateArticleCommentCounts(Long ArticleId);
}
