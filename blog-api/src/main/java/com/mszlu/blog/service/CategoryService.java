package com.mszlu.blog.service;

import com.mszlu.blog.dao.pojo.Category;
import com.mszlu.blog.vo.Result;


public interface CategoryService {

    /**
     * 通过类别id查询类别
     * @param categoryId
     * @return
     */
    Category queryCategoryById(Long categoryId);

    /**
     * 查询所有类别(查询部分字段)
     * @return
     */
    Result queryAll();

    /**
     * 查询所有类别(查询所有字段)
     * @return
     */
    Result queryAllCategory();

    /**
     * 查询该分类id下对应的文章
     * @param categoryId
     * @return
     */
    Result queryCategoryDetailById(Long categoryId);
}
