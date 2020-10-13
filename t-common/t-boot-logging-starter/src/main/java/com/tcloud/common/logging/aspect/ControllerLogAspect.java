package com.tcloud.common.logging.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Description: 日志切面
 * <br/>
 * ControllerLogAspect
 * Created by laiql on 2020/10/13 15:16.
 */
@Aspect
public class ControllerLogAspect
{

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Around("(@within(org.springframework.stereotype.Controller)" + "|| @within(org.springframework.web.bind.annotation.RestController))" + "&& execution(public * com.tcloud..*.controller..*.*(..))")
    public Object around(ProceedingJoinPoint pjp) throws Throwable
    {

        String className = pjp.getTarget().getClass().getName();
        String methodName = pjp.getSignature().getName();
        long beginTime = System.currentTimeMillis();
        Object returnValue = null;
        Exception ex = null;
        try
        {
            returnValue = pjp.proceed();
            return returnValue;
        }
        catch (Exception e)
        {
            ex = e;
            throw e;
        }
        finally
        {
            long cost = System.currentTimeMillis() - beginTime;
            if (ex != null)
            {
                log.error("[class: {}][method: {}][cost: {}ms][args: {}][发生异常]", className, methodName, pjp.getArgs(), ex);
            }
            else
            {
                log.info("[class: {}][method: {}][cost: {}ms][args: {}][return: {}]", className, methodName, cost, pjp.getArgs(), returnValue);
            }
        }

    }
}
