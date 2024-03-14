package com.mszlu.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mszlu.blog.dao.mapper.CategoryMapper;
import com.mszlu.blog.dao.pojo.Category;
import com.mszlu.blog.service.CategoryService;
import com.mszlu.blog.vo.CategoryVo;
import com.mszlu.blog.vo.Result;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Resource
    private CategoryMapper categoryMapper;

    /**
     *  通过分类id查询分类
     * @param categoryId
     * @return
     */
    @Override
    public Category queryCategoryById(Long categoryId) {
        return categoryMapper.selectById(categoryId);
    }

    /**
     * 查询所有类别(查询部分字段=>id,categoryName)
     * @return
     */
    @Override
    public Result queryAll() {
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(Category::getId,Category::getCategoryName);
        List<Category> categoryList = categoryMapper.selectList(queryWrapper);
        return Result.success(copyList(categoryList));
    }

    /**
     * 查询所有类别(查询所有字段)
     * @return
     */
    @Override
    public Result queryAllCategory() {
        List<Category> categoryList = categoryMapper.selectList(new LambdaQueryWrapper<>());
        return Result.success(copyList(categoryList));
    }

    /**
     * 查询该分类id下对应的文章
     * @param categoryId
     * @return
     */
    @Override
    public Result queryCategoryDetailById(Long categoryId) {
        Category category = categoryMapper.selectById(categoryId);
        return Result.success(copy(category));
    }

    // 将Category转化为CategoryVo
    public CategoryVo copy(Category category){
        CategoryVo categoryVo = new CategoryVo();
        BeanUtils.copyProperties(category,categoryVo);
        return categoryVo;
    }

    // 将List<Category>转化为List<CategoryVo>
    public List<CategoryVo> copyList(List<Category> categoryList){
        List<CategoryVo> categoryVoList = new ArrayList<>();
        for (Category category : categoryList) {
            categoryVoList.add(copy(category));
        }
        return categoryVoList;
    }
}
