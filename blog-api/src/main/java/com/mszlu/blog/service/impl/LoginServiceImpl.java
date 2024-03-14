package com.mszlu.blog.service.impl;

import com.alibaba.fastjson.JSON;
import com.mszlu.blog.dao.pojo.SysUser;
import com.mszlu.blog.service.LoginService;
import com.mszlu.blog.service.SysUserService;
import com.mszlu.blog.utils.JWTUtils;
import com.mszlu.blog.vo.ErrorCode;
import com.mszlu.blog.vo.Result;
import com.mszlu.blog.vo.params.LoginParams;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private RedisTemplate<String, String> template;
    // md5加密盐
    private static final String slat = "zh123456!@#";

    /**
     * 处理登录请求 => 1. 验证参数是否合法 2. 密码加密，验证数据库是否存在该帐号密码
     *               3. 若不存在，登陆失败 4. 若存在，使用jwt，生成token，相应给前端
     *               5. 将token存入redis中(token:user信息),设置过期时间
     *               6. 下次进行登录验证时，需要先验证token是否合法，再验证redis中是否存在
     * @param loginParams
     * @return Result
     */
    @Override
    public Result login(LoginParams loginParams) {
        // 获取用户账号密码
        String account = loginParams.getAccount();
        String password = loginParams.getPassword();
        // 验证用户账号密码是否为空
        if (StringUtils.isBlank(account) || StringUtils.isBlank(password)) {
            return Result.fail(ErrorCode.PARAMS_ERROR.getCode(),ErrorCode.PARAMS_ERROR.getMsg());
        }
        // password进行加密
        password = DigestUtils.md5Hex(password + slat);
        // 查询数据库
        SysUser sysUser = sysUserService.querySysUserByAccountAndPassword(account,password);
        // 查询结果为空则返回错误信息
        if (sysUser == null) {
            return Result.fail(ErrorCode.ACCOUNT_PWD_NOT_EXIST.getCode(), ErrorCode.ACCOUNT_PWD_NOT_EXIST.getMsg());
        }
        // 不为空则生成token
        String token = JWTUtils.createToken(sysUser.getId());
        // 将token:user信息存入redis
        template.opsForValue().set("TOKEN_"+token, JSON.toJSONString(sysUser),1, TimeUnit.DAYS);
        return Result.success(token);
    }

    /**
     *  校验token,返回用户信息 => 1. 校验是否为空 2. 校验是否合法 3. redis中是否存在
     * @param token
     * @return Result
     */
    @Override
    public SysUser checkToken(String token) {
        // 校验是否为空
        if (StringUtils.isBlank(token)) {
            return null;
        }
        Map<String, Object> stringObjectMap = JWTUtils.checkToken(token);
        if (stringObjectMap == null) {
            return null;
        }
        String userJson = template.opsForValue().get("TOKEN_" + token);
        if (StringUtils.isBlank(userJson)) {
            return null;
        }
        // 到此处说明token校验成功
        SysUser sysUser = JSON.parseObject(userJson, SysUser.class);
        return sysUser;
    }

    /**
     * 退出登录 => 从redis中删除token
     * @param token
     * @return
     */
    @Override
    public Result logout(String token) {
        template.delete("TOKEN_" + token);
        return Result.success(null);
    }

    /**
     * 注册用户 => 1. 校验参数是否合法 2. 判断数据库中是否已存在
     *            3. 存在返回错误提示 4. 不存在则创建用户
     *            5. 创造token       6. 将token：user信息 存入redis
     *            7. 返回token
     * 注意： 事务需要开启
     * @param loginParams
     * @return
     */
    @Override
    public Result register(LoginParams loginParams) {
        // 校验参数是否合法
        String account = loginParams.getAccount();
        String password = loginParams.getPassword();
        String nickname = loginParams.getNickname();
        if (StringUtils.isBlank(account)
                || StringUtils.isBlank(password)
                || StringUtils.isBlank(nickname)
        ){
            return Result.fail(ErrorCode.PARAMS_ERROR.getCode(),ErrorCode.PARAMS_ERROR.getMsg());
        }

        SysUser sysUser = sysUserService.querySysUserByAccount(account);

        // 判断数据库中是否已存在
        if (sysUser != null) { //该用户已存在
            return Result.fail(ErrorCode.ACCOUNT_ERROR.getCode(), ErrorCode.ACCOUNT_ERROR.getMsg());
        }

        // 用户不存在，创建用户
        sysUser = new SysUser();
        sysUser.setNickname(nickname);
        sysUser.setAccount(account);
        sysUser.setPassword(DigestUtils.md5Hex(password+slat));
        sysUser.setCreateDate(System.currentTimeMillis());
        sysUser.setLastLogin(System.currentTimeMillis());
        sysUser.setAvatar("/static/img/logo.b3a48c0.png");
        sysUser.setAdmin(1); //1 为true
        sysUser.setDeleted(0); // 0 为false
        sysUser.setSalt("");
        sysUser.setStatus("");
        sysUser.setEmail("");
        // 将用户存入数据库
        int result = sysUserService.addSysUser(sysUser);
        if (result == 1) { // 添加成功
            // 生成token
            //String token = JWTUtils.createToken(sysUser.getId());
            // 将token：user信息 存入redis
            //template.opsForValue().set("TOKEN_" + token,JSON.toJSONString(sysUser),1,TimeUnit.DAYS);
//            return Result.success(token);
            return Result.success(true);
        }
        // 添加失败，返回错误信息
        return Result.fail(ErrorCode.SYSUSER_ADD_ERROR.getCode(), ErrorCode.SYSUSER_ADD_ERROR.getMsg());
    }
}
