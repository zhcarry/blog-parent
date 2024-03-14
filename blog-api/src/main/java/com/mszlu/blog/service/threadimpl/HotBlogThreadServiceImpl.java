package com.mszlu.blog.service.threadimpl;

import com.alibaba.fastjson.JSON;
import com.mszlu.blog.dao.mapper.ArticleBodyMapper;
import com.mszlu.blog.dao.pojo.ArticleBody;
import com.mszlu.blog.vo.ArticleVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class HotBlogThreadServiceImpl {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Async("hotBlogServiceExecutor")
    public void hotBlogCache(
            List<ArticleVo> articleVoList,
            ArticleBodyMapper articleBodyMapper) {
        // 将热门帖子存入redis
        log.debug("开始将热门帖子存入redis");
        Iterator<ArticleVo> iterator = articleVoList.iterator();
        List<Long> articleIds = new ArrayList<>(articleVoList.size());
        while (iterator.hasNext()) {
            ArticleVo articleVo = iterator.next();
            Long articleId = articleVo.getId();
            if (stringRedisTemplate.opsForValue().get(articleId.toString()) == null) articleIds.add(articleId);
        }
        if (articleIds.isEmpty()) return;
        List<ArticleBody> articleBodies = articleBodyMapper.selectBatchIds(articleIds);
        Iterator<ArticleBody> bodyIterator = articleBodies.iterator();
        while (bodyIterator.hasNext()) {
            ArticleBody articleBody = bodyIterator.next();
            Long articleId = articleBody.getArticleId();
            String key = "hot:blog:" + articleId;
            String articleBodyJsonStr = JSON.toJSONString(articleBody);
            stringRedisTemplate.opsForValue().set(key ,  articleBodyJsonStr, 24, TimeUnit.HOURS);
        }
        log.debug("热门帖子存入redis结束！");
    }
}
