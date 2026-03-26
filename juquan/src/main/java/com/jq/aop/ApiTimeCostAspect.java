package com.jq.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j  // 日志注解（若未用lombok，可替换为手动声明log）
@Aspect  // 标记为切面类
@Component  // 交给Spring容器管理，必须加！
public class ApiTimeCostAspect {

    /**
     * 环绕通知：统计Controller层所有接口的耗时
     * 切点表达式：execution(* com.xxx.controller.*.*(..))
     * 解释：com.xxx.controller包下所有类的所有方法，任意参数、任意返回值
     */
    @Around("execution(* com.jq.controller.*.*(..))")
    public Object calculateApiCost(ProceedingJoinPoint pjp) throws Throwable {
        // 1. 记录接口开始执行时间
        long start = System.currentTimeMillis();

        // 2. 执行原接口方法（核心：不影响原业务逻辑）
        Object result = pjp.proceed();

        // 3. 计算耗时
        long cost = System.currentTimeMillis() - start;

        // 4. 打印耗时日志（包含接口名+耗时，便于分析）
        log.info("【接口耗时统计】{} 耗时：{} ms",
                pjp.getSignature().getDeclaringTypeName() + "." + pjp.getSignature().getName(),
                cost);

        // 5. 返回接口原结果
        return result;
    }
}
