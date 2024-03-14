package com.mszlu.blog.common.cache;

import com.alibaba.fastjson.JSON;
import com.mszlu.blog.vo.ErrorCode;
import com.mszlu.blog.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.time.Duration;

@Component
@Aspect
@Slf4j
public class CacheAspect {

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    @Pointcut("@annotation(com.mszlu.blog.common.cache.Cache)")
    public void cachePointCut() {}

    @Around("cachePointCut()")
    public Object around(ProceedingJoinPoint joinPoint){
        try {
            // 获取被执行方法的方法签名
            Signature signature = joinPoint.getSignature();
            // 获取IOC容器中目标对象中的全限定类名
            String className = joinPoint.getTarget().getClass().getName();
            // 获取被执行方法的方法名
            String methodName = signature.getName();

            // 创建切入点参数数量的Class数组
            Class[] parameterTypes = new Class[joinPoint.getArgs().length];
            // 获取切入点的参数数组
            Object[] args = joinPoint.getArgs();
            String params = "";
            for(int i=0; i<args.length; i++) {
                if(args[i] != null) {
                    params += JSON.toJSONString(args[i]);
                    // 将参数的类存入Class[]数组中
                    parameterTypes[i] = args[i].getClass();
                }else {
                    parameterTypes[i] = null;
                }
            }

            if (StringUtils.isNotEmpty(params)) {//加密
                params = DigestUtils.md5Hex(params);
            }

            // 获取被执行的方法
            Method method = joinPoint.getSignature().getDeclaringType().getMethod(methodName, parameterTypes);
            // 通过被执行的方法获取Cache注解
            Cache annotation = method.getAnnotation(Cache.class);
            // 缓存过期时间
            long expire = annotation.expire();
            // 缓存名称
            String name = annotation.name();

            // 设置redis的key
            String redisKey = name + "::" + className+"::"+methodName+"::"+params;

            // 判断redis里是否存在该key
            String redisValue = redisTemplate.opsForValue().get(redisKey);
            if (StringUtils.isNotEmpty(redisValue)){
                log.info("走了缓存,{},{}",className,methodName);
                return JSON.parseObject(redisValue, Result.class);
            }

            Object proceed = joinPoint.proceed();

            // 缓存
            redisTemplate.opsForValue().set(redisKey,JSON.toJSONString(proceed), Duration.ofMillis(expire));
            log.info("存入缓存==> {},{}",className,methodName);

            return proceed;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return Result.fail(ErrorCode.SYSTEM_ERROR.getCode(), ErrorCode.SYSTEM_ERROR.getMsg());
    }
}
