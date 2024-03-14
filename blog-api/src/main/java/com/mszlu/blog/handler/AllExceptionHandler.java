package com.mszlu.blog.handler;

import com.mszlu.blog.vo.Result;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 *  对所有异常进行处理
 */

@ControllerAdvice// 该注解作用：对加了@Controller的的方法进行拦截，是aop的实现
public class AllExceptionHandler {


    @ExceptionHandler(Exception.class) //进行Exception异常处理
    @ResponseBody // 返回的数据转换成json
    public Result doException(Exception ex) {
        ex.printStackTrace();
        return Result.fail(-999,"系统异常");
    }
}
