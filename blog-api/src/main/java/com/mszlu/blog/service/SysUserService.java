package com.mszlu.blog.service;

import com.mszlu.blog.dao.pojo.SysUser;
import com.mszlu.blog.vo.Result;

public interface SysUserService {
    /**
     * 通过文章id查询用户
     * @param authorId
     * @return SysUser
     */
    SysUser querySysUserByUserId(Long authorId);

    /**
     * 通过登录帐号和密码查询用户
     * @param account
     * @param password
     * @return SysUser
     */
    SysUser querySysUserByAccountAndPassword(String account, String password);

    /**
     * 通过token查询用户
     * @param token
     * @return Result
     */
    Result querySysUserByToken(String token);

    /**
     * 通过帐号查询用户
     * @param account
     * @return SysUser
     */
    SysUser querySysUserByAccount(String account);

    /**
     * 添加用户
     * @param sysUser
     * @return int
     */
    int addSysUser(SysUser sysUser);
}
