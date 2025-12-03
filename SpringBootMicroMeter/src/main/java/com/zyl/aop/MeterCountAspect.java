package com.zyl.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Aspect
@Component
public class MeterCountAspect implements InitializingBean {

    private final MeterRegistry meterRegistry;
    private static final String COUNTER_KEY = "api.requests";
    private Counter counter;

    @Pointcut("@annotation(com.zyl.annotations.MeterCountAnnotation)")
    public void meterCountAnnotation() {
    }

    @Around("meterCountAnnotation()")
    public Object beforeMeterCountAnnotation(ProceedingJoinPoint joinPoint) throws Throwable {

        Object result = null;
        try {
            result = joinPoint.proceed();
            counter.increment();
        } catch (Throwable throwable) {
            System.out.println("请求异常");
            throw throwable;
        }
        return result;
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        this.counter = meterRegistry.counter(COUNTER_KEY);
    }
}
