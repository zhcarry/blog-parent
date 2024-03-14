package com.mszlu.blog.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mszlu.blog.dao.pojo.Comment;

import java.util.List;

public interface CommentMapper extends BaseMapper<Comment> {
    /**
     * 获取该id对应评论的子id
     * @param id
     * @return
     */
    List<Long> selectCommentId(Long id);
}
