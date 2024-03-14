package com.mszlu.blog.controller;

import com.mszlu.blog.service.CommentService;
import com.mszlu.blog.vo.Result;
import com.mszlu.blog.vo.params.CommentParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("comments")
public class CommentController {
    @Autowired
    private CommentService commentService;

    /**
     * 文章详情中的评论
     * @param Id => 文章id
     * @return
     */
    @GetMapping("article/{id}")
    public Result comments(@PathVariable("id") Long Id) {
        return commentService.queryCommentsById(Id);
    }

    /**
     * 新增评论
     * @param commentParam
     * @return Result
     */
    @PostMapping("create/change")
    public Result createComment(@RequestBody CommentParam commentParam){
        return commentService.addComment(commentParam);
    }
}
