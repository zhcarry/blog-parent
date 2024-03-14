package com.mszlu.blog.controller;

import com.mszlu.blog.service.CategoryService;
import com.mszlu.blog.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("categorys")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    /**
     * 发布文章之文章类别
     * @return
     */
    @GetMapping
    public Result categorys() {
        return categoryService.queryAll();
    }

    /**
     * 文章分类
     * @return
     */
    @GetMapping("detail")
    public Result detail() {
        return categoryService.queryAllCategory();
    }

    /**
     * 具体文章分类对应文章
     */
    @GetMapping("detail/{id}")
    public Result categoryDetailById(@PathVariable("id") Long CategoryId) {
        return categoryService.queryCategoryDetailById(CategoryId);
    }
}
