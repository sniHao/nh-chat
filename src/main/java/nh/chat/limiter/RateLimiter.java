package nh.chat.limiter;

import java.lang.annotation.*;

/**
 * @Description:限流注解类
 * @Author:xph
 * @Date: 2024/10/20 15:21
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimiter {
    /**
     * 限流key
     */
    String key() default "rate_limit:";

    /**
     * 限流时间,单位秒
     */
    int time() default 60;

    /**
     * 限流次数
     */
    int count() default 100;

    /**
     * 限流类型
     */
    LimiterType limitType() default LimiterType.DEFAULT;
}
