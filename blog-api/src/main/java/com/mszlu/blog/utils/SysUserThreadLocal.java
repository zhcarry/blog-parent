package com.mszlu.blog.utils;

import com.mszlu.blog.dao.pojo.SysUser;

public class SysUserThreadLocal {
    private SysUserThreadLocal() {};

    private static final ThreadLocal<SysUser> LOCAL = new ThreadLocal<>();

    public static SysUser get() {
        return LOCAL.get();
    }

    public static void set(SysUser sysUser) {
        LOCAL.set(sysUser);
    }

    public static void remove() {
        LOCAL.remove();
    }

}
