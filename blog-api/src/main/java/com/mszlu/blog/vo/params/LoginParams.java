package com.mszlu.blog.vo.params;

import lombok.Data;

@Data
public class LoginParams {
    // 帐号
    private String account;
    // 密码
    private String password;
    // 昵称
    private String nickname;
}
