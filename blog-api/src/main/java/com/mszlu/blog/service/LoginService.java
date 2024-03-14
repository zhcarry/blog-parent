package com.mszlu.blog.service;

import com.mszlu.blog.dao.pojo.SysUser;
import com.mszlu.blog.vo.Result;
import com.mszlu.blog.vo.params.LoginParams;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface LoginService {
    /**
     * 处理登录请求
     * @return Result
     */
    Result login(LoginParams loginParams);

    /**
     * 校验token是否合法
     * @param token
     * @return SysUser
     */
    SysUser checkToken(String token);

    /**
     * 退出登录
     * @param token
     * @return
     */
    Result logout(String token);

    /**
     * 注册用户
     * @param loginParams
     * @return Result
     */
    Result register(LoginParams loginParams);
}
