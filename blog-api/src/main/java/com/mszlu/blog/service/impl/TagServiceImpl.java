package com.mszlu.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mszlu.blog.dao.mapper.TagMapper;
import com.mszlu.blog.dao.pojo.Tag;
import com.mszlu.blog.service.TagService;
import com.mszlu.blog.vo.Result;
import com.mszlu.blog.vo.TagVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class TagServiceImpl implements TagService {

    @Resource
    private TagMapper tagMapper;

    /**
     * 通过articleId查询Tag
     * @param articleId
     * @return List<TagVo>,
     */
    @Override
    public List<TagVo> queryTagByArticleId(Long articleId) {
        List<Tag> tagList = tagMapper.selectTagByArticleId(articleId);
        // 将tagList转化为tagVoList
        List<TagVo> tagVoList = copyTag(tagList);
        return tagVoList;
    }

    /**
     * 根据limit决定最热标签的数量
     * @param limit
     * @return List<TagVo>
     */
    @Override
    public Result queryHotTagsByLimit(int limit) {
        // 根据limit查询标签id
        List<Long> tagIdList = tagMapper.selectTagIdByLimit(limit);
        if (tagIdList.isEmpty()) {
            return Result.success(Collections.emptyList());
        }
        List<Tag> tagList = tagMapper.selectTagsByTagId(tagIdList);
        List<TagVo> tagVoList = copyTag(tagList);
        return Result.success(tagList);
    }

    /**
     * 所有标签(返回部分字段=>id,tagName)
     * @return
     */
    @Override
    public Result queryAllTag() {
        LambdaQueryWrapper<Tag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(Tag::getId,Tag::getTagName);
        List<Tag> tagList = tagMapper.selectList(queryWrapper);
        return Result.success(copyTag(tagList));
    }

    /**
     * 所有标签(返回全部字段)
     * @return
     */
    @Override
    public Result queryAll() {
        List<Tag> tagList = tagMapper.selectList(new LambdaQueryWrapper<>());
        return Result.success(copyTag(tagList));
    }

    /**
     * 根据tagId查询tag
     * @param tagId
     * @return
     */
    @Override
    public Result queryDetailById(Long tagId) {
        Tag tag = tagMapper.selectById(tagId);
        return Result.success(copy(tag));
    }

    // 将tagList转化为tagVoList
    public List<TagVo> copyTag(List<Tag> tagList) {
        List<TagVo> tagVoList = new ArrayList<>();
        for (Tag tag : tagList) {
            tagVoList.add(copy(tag));
        }
        return tagVoList;
    }

    // 将Tag转化为TagVo
    public TagVo copy(Tag tag) {
        TagVo tagVo = new TagVo();
        BeanUtils.copyProperties(tag,tagVo);
        return tagVo;
    }
}
