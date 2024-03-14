package com.mszlu.blog.common.aop;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogAnnotation {

    // 模块名称
    String module() default "";
    // 操作名称
    String operator() default "";
}
