package com.mszlu.blog.common.aop;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Component
@Aspect // 切面
@Slf4j
public class LogAspect {

    /**
     * @annotation: 指定注解
     * 该切点用于 记录日志
     */
    @Pointcut("@annotation(com.mszlu.blog.common.aop.LogAnnotation)")
    public void logPointCut() {}



    @Around("logPointCut()")
    public Object log(ProceedingJoinPoint joinPoint) throws Throwable {
        // 方法开始时间
        long beginTime = System.currentTimeMillis();

        // 执行代理方法
        Object result = joinPoint.proceed();

        // 方法执行结束所消耗的时间
        long time = System.currentTimeMillis() - beginTime;

        // 保存日志
        recordLog(joinPoint, time);
        return result;
    }

    // 保存日志方法
    private void recordLog(ProceedingJoinPoint joinPoint, long time) {
        /**
         * joinPoint.getSignature()=>获取IOC容器中目标对象中的方法
         * MethodSignature 方法签名 => 方法名和形参列表共同组成方法签名。
         */
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        // 获取目标方法
        Method method = signature.getMethod();
        // 目标方法上存在该注解，则返回该注解，否则返回null
        LogAnnotation logAnnotation = method.getAnnotation(LogAnnotation.class);

        log.info("=====================log start================================");
        log.info("module:{}",logAnnotation.module());
        log.info("operation:{}",logAnnotation.operator());

        // 获取目标对象的类的全限定类名
        String className = joinPoint.getTarget().getClass().getName();
        // 获取被执行方法的方法名
        String methodName = signature.getName();
        log.info("request method:{}",className + "." + methodName + "()");

        // 获取被执行方法的参数数组
        Object[] args = joinPoint.getArgs();
        // 将obj ==> json
        String params = JSON.toJSONString(args[0]);
        log.info("params:{}",params);

        log.info("execute time : {} ms",time);
        log.info("=====================log end================================");
    }
}
