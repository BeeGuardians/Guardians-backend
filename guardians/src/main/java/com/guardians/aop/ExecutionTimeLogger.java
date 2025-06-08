package com.guardians.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Aspect
@Component
public class ExecutionTimeLogger {

    private static final Logger log = LoggerFactory.getLogger(ExecutionTimeLogger.class);

    @Around("execution(* com.guardians..*Controller.*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object proceed = joinPoint.proceed();
        long duration = System.currentTimeMillis() - start;

        log.info("[{}] 실행 시간: {}ms", joinPoint.getSignature(), duration);

        return proceed;
    }
}
