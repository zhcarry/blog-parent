package com.mszlu.blog.service.threadimpl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.mszlu.blog.dao.mapper.ArticleMapper;
import com.mszlu.blog.dao.pojo.Article;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class ArticleCommentCountsThreadServiceImpl {

    /**
     * 添加评论后，文章评论数+1
     */
    public static void updateArticleCommentCounts(ArticleMapper articleMapper,Long id) {
        Article article = articleMapper.selectById(id);
        int commentCounts = article.getCommentCounts();
        Article articleUpdate = new Article();
        // 只需要修改评论数
        articleUpdate.setCommentCounts(commentCounts + 1);

        LambdaUpdateWrapper<Article> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Article::getId,article.getId())
                     .eq(Article::getCommentCounts,commentCounts); // 添加乐观锁
        articleMapper.update(articleUpdate,updateWrapper);
    }

    /**
     * 查看文章详情后，文章观看数增加
     * @param articleMapper
     * @param article
     */
    public void updateArticleViewCounts(ArticleMapper articleMapper, Article article) {
        int viewCounts = article.getViewCounts();
        // 只需要修改viewCounts
        Article articleUpdate = new Article();
        articleUpdate.setViewCounts(viewCounts+1);

        // 修改语句
        LambdaUpdateWrapper<Article> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(Article::getId,article.getId())
                     .eq(Article::getViewCounts,viewCounts); // 添加乐观锁,确保线程安全
        articleMapper.update(articleUpdate,updateWrapper);
    }
}
