package com.mszlu.blog.service;

import com.mszlu.blog.vo.Result;
import com.mszlu.blog.vo.TagVo;

import java.util.List;

public interface TagService {

    /**
     * 通过articleId查询Tag
     * @param articleId
     * @return List<TagVo>,
     */
    List<TagVo> queryTagByArticleId(Long articleId);

    /**
     * 查询limit个数的最热标签
     * @param limit
     * @return
     */
    Result queryHotTagsByLimit(int limit);

    /**
     * 所有标签(返回部分字段=>id,tagName)
     * @return
     */
    Result queryAllTag();

    /**
     * 所有标签(返回全部字段)
     * @return
     */
    Result queryAll();

    /**
     * 根据tagId查询tag
     * @param tagId
     * @return
     */
    Result queryDetailById(Long tagId);
}
