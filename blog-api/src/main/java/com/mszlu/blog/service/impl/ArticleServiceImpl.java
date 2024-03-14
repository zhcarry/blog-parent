package com.mszlu.blog.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mszlu.blog.dao.dto.Archive;
import com.mszlu.blog.dao.mapper.ArticleBodyMapper;
import com.mszlu.blog.dao.mapper.ArticleMapper;
import com.mszlu.blog.dao.mapper.ArticleTagMapper;
import com.mszlu.blog.dao.pojo.*;
import com.mszlu.blog.service.*;
import com.mszlu.blog.service.threadimpl.ArticleCommentCountsThreadServiceImpl;
import com.mszlu.blog.service.threadimpl.HotBlogThreadServiceImpl;
import com.mszlu.blog.utils.SysUserThreadLocal;
import com.mszlu.blog.vo.*;
import com.mszlu.blog.vo.params.ArticleParam;
import com.mszlu.blog.vo.params.PageParams;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class ArticleServiceImpl implements ArticleService {
    @Resource
    private ArticleMapper articleMapper;
    @Resource
    private ArticleBodyMapper articleBodyMapper;
    @Resource
    private ArticleTagMapper articleTagMapper;

    @Resource
    private TagService tagService;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private CategoryService categoryService;
    @Resource
    private ArticleCommentCountsThreadServiceImpl articleCommentCountsThreadServiceImpl;
    @Resource
    private HotBlogThreadServiceImpl hotBlogThreadService;


    /**
     *  要求：分页查询article数据库表
     */
    @Override
    public Result queryListArticle(PageParams params) {
        /*Page<Article> page = new Page<>(params.getPage(), params.getPageSize());
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        // 判断是否需要按分类查询
        if (params.getCategoryId() != null) {
            wrapper.eq(Article::getCategoryId,params.getCategoryId());
        }
        // 判断是否需要按标签查询
        if (params.getTagId() != null) {
            // 通过tagId查询articleTag表得到tagId底下所有的文章id
            List<Long> articleIdList = new ArrayList<>();
            LambdaQueryWrapper<ArticleTag> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.select(ArticleTag::getArticleId).eq(ArticleTag::getTagId,params.getTagId());
            List<ArticleTag> articleTagList = articleTagMapper.selectList(queryWrapper);
            for (ArticleTag articleTag : articleTagList) {
                articleIdList.add(articleTag.getArticleId());
            }
            // 通过多个文章id查询文章
            if (articleIdList.size() > 0) {
                wrapper.in(Article::getId,articleIdList);
            }
        }

        // 先置顶排序
        // 再按创造时间降序
        wrapper.orderByDesc(Article::getWeight,Article::getCreateDate);
        Page<Article> articlePage = articleMapper.selectPage(page, wrapper);
        // 获取返回的articleList
        List<Article> records = articlePage.getRecords();
        // 获取了article对象后不能直接返回，需要转化成ArticleVo对象
        List<ArticleVo> articleVoList = copyList(records,true,true);
        return Result.success(articleVoList);*/

        Page<Article> page = new Page<>(params.getPage(),params.getPageSize());
        IPage<Article> articleIPage = this.articleMapper.selectArticleList(page,params.getCategoryId(),params.getTagId(),params.getYear(),params.getMonth());
        return Result.success(copyList(articleIPage.getRecords(),true,true));
    }

    /**
     * 查询最热文章
     * @param limit
     * @return Result
     */
    @Override
    public Result queryHotArticle(int limit) {
        //select id, title, view_counts from ms_article order by view_counts desc limit 4
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(Article::getId,Article::getTitle)
                    .orderByDesc(Article::getViewCounts)
                    .last("limit "+limit);
        List<Article> articleList = articleMapper.selectList(queryWrapper);
        // 将List<Article>转化为List<ArticleVo>
        List<ArticleVo> articleVoList = copyList(articleList, false, false);
        // 异步将最热博客存入redis
        hotBlogThreadService.hotBlogCache(articleVoList, articleBodyMapper);
        return Result.success(articleVoList);
    }

    /**
     * 查询最新文章
     * @param limit
     * @return Result
     */
    @Override
    public Result queryNewArticle(int limit) {
        //select id, title, create_date from ms_article order by create_date desc limit 4
        LambdaQueryWrapper<Article> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.select(Article::getId,Article::getTitle).orderByDesc(Article::getCreateDate).last("limit "+limit);
        List<Article> articleList = articleMapper.selectList(queryWrapper);
        // 将List<Article>转化为List<ArticleVo>
        return Result.success(copyList(articleList,false,false));
    }

    /**
     * 查询文章归档
     * @return Result
     */
    @Override
    public Result queryArticleArchivesByCreateTime() {
        List<Archive> archiveList = articleMapper.selectArticleArchivesByCreateTime();
        return Result.success(archiveList);
    }

    /**
     * 文章详情
     * @params ArticleId => 文章id
     * @return
     */
    @Override
    public Result queryArticleByArticleId(Long ArticleId) {
        /**
         *  1. 通过文章id查询文章，获取文章详情的id和文章分类的id
         *  2. 将文章详情和文章分类分别通过其id查询
         */
        Article article = articleMapper.selectById(ArticleId);
        ArticleVo articleVo = copy(article,true,true,true,true);
        /**
         *  此时文章详情获取完毕，需要将文章观看数+1
         *  该操作不能影响用户查询文章详情的体验，采用线程池解决
         */
        articleCommentCountsThreadServiceImpl.updateArticleViewCounts(articleMapper,article);
        return Result.success(articleVo);
    }

    /**
     * 发布文章
     * @param articleParam
     * @return
     */
    @Override
    public Result addArticle(ArticleParam articleParam) {
        Article article = new Article();
        // 评论数量
        article.setCommentCounts(0);
        // 创造时间
        article.setCreateDate(System.currentTimeMillis());
        // 文章概括
        article.setSummary(articleParam.getSummary());
        // 文章标题
        article.setTitle(articleParam.getTitle());
        // 观看数量
        article.setViewCounts(0);
        // 权重
        article.setWeight(Article.Article_Common);
        // 作者id
        article.setAuthorId(SysUserThreadLocal.get().getId());
        // 文章分类id
        article.setCategoryId(articleParam.getCategory().getId());

        // 添加文章以获取文章id
        int result = articleMapper.insert(article);
        if (result == 0) {
            return Result.fail(ErrorCode.ARTICLE_ADD_ERROR.getCode(), ErrorCode.ACCOUNT_PWD_NOT_EXIST.getMsg());
        }

        /**
         * 文章详情
         */
        ArticleBody articleBody = new ArticleBody();
        // 文章详情内容
        articleBody.setContent(articleParam.getBody().getContent());
        // 文章详情结构
        articleBody.setContentHtml(articleParam.getBody().getContentHtml());
        articleBody.setArticleId(article.getId());
        articleBodyMapper.insert(articleBody);
        article.setBodyId(articleBody.getId());

        // 更新文章
        articleMapper.updateById(article);

        /**
         * 添加文章-标签关联数据
         */
        List<TagVo> tagVoList = articleParam.getTags();
        if (tagVoList != null) {
            for (TagVo tagVo : tagVoList) {
                ArticleTag articleTag = new ArticleTag();
                articleTag.setTagId(tagVo.getId());
                articleTag.setArticleId(article.getId());
                articleTagMapper.insert(articleTag);
            }
        }


        Map<String,String> map = new HashMap<>();
        map.put("id",article.getId().toString());
        return Result.success(map);
    }

    /**
     * 文章评论数+1
     */
    @Override
    public void updateArticleCommentCounts(Long articleId) {
        ArticleCommentCountsThreadServiceImpl.updateArticleCommentCounts(articleMapper,articleId);
    }

    /**
     * 将List<Article> 转为 List<ArticleVo>
     */
    public List<ArticleVo> copyList(List<Article> records, boolean isTag, boolean isAuthor) {
        List<ArticleVo> articleVoList = new ArrayList<>();
        for (Article article : records) {
            articleVoList.add(copy(article,isTag,isAuthor,false,false));
        }
        return articleVoList;
    }

    /**
     * 方法重载 => 将List<Article> 转为 List<ArticleVo>
     */
    public List<ArticleVo> copyList(List<Article> records, boolean isTag, boolean isAuthor, boolean isBody, boolean isCategory) {
        List<ArticleVo> articleVoList = new ArrayList<>();
        for (Article article : records) {
            articleVoList.add(copy(article,isTag,isAuthor,isBody,isCategory));
        }
        return articleVoList;
    }

    /**
     * 将Article 转为 ArticleVo
     */
    public ArticleVo copy(Article article, boolean isTag, boolean isAuthor, boolean isBody, boolean isCategory) {
        ArticleVo articleVo = new ArticleVo();
        BeanUtils.copyProperties(article,articleVo);
        // 将ArticleVo中String createDate转为Date createDate
        articleVo.setCreateDate(new DateTime(article.getCreateDate()).toString("yyyy-MM-dd HH:mm"));

        if (isTag) { // 获取tag
            Long articleId = article.getId();
            articleVo.setTags(tagService.queryTagByArticleId(articleId));
        }
        if (isAuthor) { //获取author
            // 获取作者id
            Long authorId = article.getAuthorId();
            articleVo.setAuthor(sysUserService.querySysUserByUserId(authorId).getNickname());
        }
        if (isBody) { // 获取ArticleBody
            // 获取文章详情id
            Long bodyId = article.getBodyId();
            // 通过文章详情id查询文章详情表
            ArticleBody articleBody = articleBodyMapper.selectById(bodyId);

            ArticleBodyVo articleBodyVo = new ArticleBodyVo();
            articleBodyVo.setContent(articleBody.getContent());

            articleVo.setBody(articleBodyVo);
        }
        if (isCategory) { //获取Category
            Long categoryId = article.getCategoryId();
            // 通过分类id查询分类表
            Category category = categoryService.queryCategoryById(categoryId);
            CategoryVo categoryVo = new CategoryVo();
            // 将category复制到categoryVo
            BeanUtils.copyProperties(category,categoryVo);
            articleVo.setCategory(categoryVo);
        }
        return articleVo;
    }
}
