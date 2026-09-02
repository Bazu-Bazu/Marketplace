package com.burkina.marketplace.aop;

import com.burkina.common.aop.BaseLoggingAspect;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

@Component
@Aspect
public class LoggingAspect extends BaseLoggingAspect {

    @Before("applicationPackage()")
    public void before(JoinPoint joinPoint) {
        logBefore(joinPoint);
    }

    @AfterReturning(pointcut = "applicationPackage()", returning = "result")
    public void afterReturning(JoinPoint joinPoint, Object result) {
        logAfterReturning(joinPoint, result);
    }

    @AfterThrowing(pointcut = "applicationPackage()", throwing = "exception")
    public void afterTrowing(JoinPoint joinPoint, Exception exception) {
        logAfterTrowing(joinPoint, exception);
    }

    @Pointcut(
            "execution(public * com.burkina.marketplace.controller..*.*(..)) || " +
            "execution(public * com.burkina.marketplace.service..*.*(..))"
    )
    public void applicationPackage() {}
}