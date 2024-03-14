package com.mszlu.blog.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mszlu.blog.dao.pojo.Tag;

import java.util.List;

public interface TagMapper extends BaseMapper<Tag> {

    /**
     * 通过articleId查询Tag
     * @param articleId
     * @return List<Tag>
     */
    List<Tag> selectTagByArticleId(Long articleId);

    /**
     * 根据limit查询标签id
     * @param limit
     * @return tagIdList
     */
    List<Long> selectTagIdByLimit(int limit);

    /**
     * 根据tagIds查询Tag
     * @param tagIdList
     * @return List<Tag>
     */
    List<Tag> selectTagsByTagId(List<Long> tagIdList);
}
