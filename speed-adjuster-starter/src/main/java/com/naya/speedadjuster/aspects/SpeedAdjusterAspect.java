package com.naya.speedadjuster.aspects;

import com.naya.speedadjuster.services.SpeedAdjusterService;
import lombok.SneakyThrows;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Evgeny Borisov
 */
//@Aspect
public class SpeedAdjusterAspect {
    @Autowired
    private SpeedAdjusterService speedAdjuster;

    @SneakyThrows
    @Around("@annotation(com.naya.speedadjuster.annotations.Balanced)")
    public Object beforeHandling(ProceedingJoinPoint pjp) {
        long start = System.currentTimeMillis();
        Object retVal = pjp.proceed();
        long end = System.currentTimeMillis();
//        speedAdjuster.newDelay(end-start);
        return retVal;

    }
}
