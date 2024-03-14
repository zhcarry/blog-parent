package com.mszlu.blog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mszlu.blog.dao.mapper.SysUserMapper;
import com.mszlu.blog.dao.pojo.SysUser;
import com.mszlu.blog.dao.pojo.Tag;
import com.mszlu.blog.service.LoginService;
import com.mszlu.blog.service.SysUserService;
import com.mszlu.blog.vo.*;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class SysUserServiceImpl implements SysUserService {

    @Resource
    private SysUserMapper sysUserMapper;
    @Resource
    private LoginService loginService;

    /**
     * 根据authorId查询作者
     * @param authorId
     * @return SysUser
     */
    @Override
    public SysUser querySysUserByUserId(Long authorId) {
        SysUser sysUser = sysUserMapper.selectById(authorId);
        if (sysUser == null) {
            sysUser = new SysUser();
            sysUser.setNickname("码神之路");
            return sysUser;
        }
        return sysUser;
    }

    /**
     * 根据用户account和password查询用户信息
     * @param account
     * @param password
     * @return
     */
    @Override
    public SysUser querySysUserByAccountAndPassword(String account, String password) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(SysUser::getAccount,account)
                    .eq(SysUser::getPassword,password)
                    .select(SysUser::getAccount,SysUser::getId,SysUser::getAvatar,SysUser::getNickname)
                    .last("limit 1"); // limit 1 用于提高查找效率
        return sysUserMapper.selectOne(queryWrapper);
    }

    /**
     * 通过token查询用户
     * @param token
     * @return Result
     */
    @Override
    public Result querySysUserByToken(String token) {
        // 校验token
        SysUser sysUser = loginService.checkToken(token);
        if (sysUser == null) {
            return Result.fail(ErrorCode.TOKEN_ERROR.getCode(), ErrorCode.TOKEN_ERROR.getMsg());
        }
        // 将SysUser 转化为 LoginUserVo
        LoginUserVo loginUserVo = new LoginUserVo();
        loginUserVo.setId(sysUser.getId());
        loginUserVo.setNickname(sysUser.getNickname());
        loginUserVo.setAccount(sysUser.getAccount());
        loginUserVo.setAvatar(sysUser.getAvatar());
        return Result.success(loginUserVo);
    }

    /**
     * 通过帐号查询用户
     * @param account
     * @return SysUser
     */
    @Override
    public SysUser querySysUserByAccount(String account) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUser::getAccount,account).last("limit 1");
        return sysUserMapper.selectOne(queryWrapper);
    }

    /**
     * 添加用户
     * @param sysUser
     * @return
     */
    @Override
    public int addSysUser(SysUser sysUser) {
        return sysUserMapper.insert(sysUser);
    }


}
