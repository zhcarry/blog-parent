package com.mszlu.blog.dao.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mszlu.blog.dao.dto.Archive;
import com.mszlu.blog.dao.pojo.Article;

import java.util.List;

public interface ArticleMapper extends BaseMapper<Article> {

    // 通过文章创造时间查询文章归档
    List<Archive> selectArticleArchivesByCreateTime();

    /**
     * 查询文章列表
     * @param page
     * @param categoryId
     * @param tagId
     * @param year
     * @param month
     * @return
     */
    IPage<Article> selectArticleList(Page<Article> page, Long categoryId, Long tagId, String year, String month);
}
