package com.mszlu.blog.common.cache;


import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Cache {

    // 默认过期时间 5min
    long expire() default 5 * 60 * 1000;

    // 缓存名
    String name() default "";

}
