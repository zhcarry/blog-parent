package com.mszlu.blog.controller;

import com.mszlu.blog.service.TagService;
import com.mszlu.blog.vo.Result;
import com.mszlu.blog.vo.TagVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("tags")
public class TagsController {
    @Autowired
    private TagService tagService;

    /**
     * 根据limit决定最热标签的数量
     * @return Result
     * @params int limit
     */
    @GetMapping("hot")
    public Result queryHotTagsByLimit() {
        int limit = 6;
        return tagService.queryHotTagsByLimit(limit);
    }


    /**
     * 所有标签(返回部分字段=>id,tagName)
     * @return
     */
    @GetMapping
    public Result queryAllTags() {
        return tagService.queryAllTag();
    }

    /**
     * 所有标签(返回全部字段)
     * @return
     */
    @GetMapping("detail")
    public Result queryAll() {
        return tagService.queryAll();
    }

    /**
     * 文章分类之具体标签下的所有所有文章
     * @param tagId
     * @return
     */
    @GetMapping("detail/{id}")
    public Result queryDetailById(@PathVariable("id") Long tagId) {
        return tagService.queryDetailById(tagId);
    }
}
