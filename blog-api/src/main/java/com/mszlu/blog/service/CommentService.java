package com.mszlu.blog.service;

import com.mszlu.blog.vo.Result;
import com.mszlu.blog.vo.params.CommentParam;

public interface CommentService {
    /**
     * 通过文章id查询评论
     * @param Id => 文章id，评论id
     * @return Result
     */
    Result queryCommentsById(Long Id);

    /**
     *  新增评论
     * @param commentParam
     * @return
     */
    Result addComment(CommentParam commentParam);
}
