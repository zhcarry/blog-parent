package com.mszlu.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mszlu.blog.common.aop.LogAnnotation;
import com.mszlu.blog.dao.mapper.CommentMapper;
import com.mszlu.blog.dao.pojo.Comment;
import com.mszlu.blog.dao.pojo.SysUser;
import com.mszlu.blog.filter.SensitiveFilter;
import com.mszlu.blog.service.CommentService;
import com.mszlu.blog.service.SysUserService;
import com.mszlu.blog.utils.ObjectConversionUtils;
import com.mszlu.blog.utils.SysUserThreadLocal;
import com.mszlu.blog.vo.CommentVo;
import com.mszlu.blog.vo.ErrorCode;
import com.mszlu.blog.vo.Result;
import com.mszlu.blog.vo.params.CommentParam;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {
    @Resource
    private CommentMapper commentMapper;
    @Resource
    private SysUserService sysUserService;
    @Resource
    private ArticleServiceImpl articleService;
    @Resource
    private SensitiveFilter sensitiveFilter;

    /**
     * 通过文章id查询评论
     * @param Id => 文章id，评论id
     * @return Result
     */
    @Override
    @LogAnnotation(module = "评论", operator = "根据文章查询评论")
    public Result queryCommentsById(Long Id) {
        /**
         * 1. 通过文章id查询评论表，返回评论list集合
         * 2. 通过评论list集合，获取每一个评论的sysUserId、level、parentId、toUid
         * 3. 通过sysUserId获取评论的作者信息
         */
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getArticleId,Id).eq(Comment::getLevel,1);
        //通过文章id查询评论表，返回评论list集合
        List<Comment> commentList = commentMapper.selectList(queryWrapper);
        List<CommentVo> commentVoList = copyList(commentList);
        return Result.success(commentVoList);
    }

    /**
     * 新增评论
     * @param commentParam
     * @return
     */
    @Override
    public Result addComment(CommentParam commentParam) {
        // 创建comment对象
        Comment comment = new Comment();
        // 评论脱敏
        String content = commentParam.getContent();
        commentParam.setContent(sensitiveFilter.filter(content));

        BeanUtils.copyProperties(commentParam,comment);
        // toUid
        comment.setToUid(commentParam.getToUserId());
        // 创建时间
        comment.setCreateDate(System.currentTimeMillis());
        // 作者id
        comment.setAuthorId(SysUserThreadLocal.get().getId());
        /**
         *  level => 查询父评论的level，在其基础上+1,parent
         */
        if (null == commentParam.getParent() || 0L == commentParam.getParent()) {
            comment.setLevel(1);
            comment.setParentId(0L);
        } else {
            comment.setLevel(commentMapper.selectById(commentParam.getParent()).getLevel() + 1);
            comment.setParentId(commentParam.getParent());
        }
        // toUid
        comment.setToUid(comment.getToUid() == null ? 0 : comment.getToUid());
        // 新增评论完毕，文章评论数需要+1
        articleService.updateArticleCommentCounts(commentParam.getArticleId());

        int result = commentMapper.insert(comment);
        if (result != 1) {
            return Result.fail(ErrorCode.COMMENT_ADD_ERROR.getCode(), ErrorCode.COMMENT_ADD_ERROR.getMsg());
        }
        return Result.success(copy(comment));
    }

    // 将List<Comment>转为List<CommentVo>
    public List<CommentVo> copyList(List<Comment> commentList) {
        List<CommentVo> commentVoList = new ArrayList<>();
        for (Comment comment : commentList) {
            commentVoList.add(copy(comment));
        }
        return commentVoList;
    }

    // 将Comment转为CommentVo
    public CommentVo copy(Comment comment) {
        CommentVo commentVo = new CommentVo();
        BeanUtils.copyProperties(comment,commentVo);
        commentVo.setAuthor(ObjectConversionUtils.copy(sysUserService.querySysUserByUserId(comment.getAuthorId())));
        commentVo.setToUser(ObjectConversionUtils.copy(sysUserService.querySysUserByUserId(comment.getToUid())));
        // 获取该评论的孩子id
        List<Long> ChildrenIdList = commentMapper.selectCommentId(comment.getId());
        // 通过孩子id获取其子评论
        List<Comment> commentList = new ArrayList<>();
        for (Long id : ChildrenIdList) {
            commentList.add(commentMapper.selectById(id));
        }
        List<CommentVo> commentVoList = copyList(commentList);
        commentVo.setChildrens(commentVoList);
        return commentVo;
    }
}
