package com.mszlu.blog.vo;

public enum ErrorCode {
    PARAMS_ERROR(10001,"参数有误"),
    ACCOUNT_PWD_NOT_EXIST(10002,"用户名或密码不存在"),
    ACCOUNT_ERROR(1003,"用户已存在"),
    TOKEN_ERROR(10004,"token不合法"),

    NO_PERMISSION(70001,"无访问权限"),

    SYSUSER_ADD_ERROR(80001,"用户添加失败"),
    COMMENT_ADD_ERROR(80002,"评论添加失败"),
    ARTICLE_ADD_ERROR(80003,"文章添加失败"),
    PHOTO_ADD_ERROR(80004,"图片添加失败"),

    SESSION_TIME_OUT(90001,"会话超时"),
    NO_LOGIN(90002,"未登录"),

    SYSTEM_ERROR(-999,"系统错误！");

    private int code;
    private String msg;

    ErrorCode(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
