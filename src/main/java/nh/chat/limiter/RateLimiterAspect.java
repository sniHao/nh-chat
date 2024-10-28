package nh.chat.limiter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nh.chat.utils.IpUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Collections;
import java.util.List;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RateLimiterAspect {
    private static final Logger logger = LoggerFactory.getLogger(RateLimiterAspect.class);

    private final RedisTemplate redisTemplate;
    private final RedisScript<Long> limitScript;

    @Before("@annotation(rateLimiter)")
    public void doBefore(JoinPoint point, RateLimiter rateLimiter) throws RateLimitException {
        int time = rateLimiter.time();
        int count = rateLimiter.count();
        String combineKey = getCombineKey(rateLimiter, point);
        List<Object> keys = Collections.singletonList(combineKey);
        Long number = (Long) redisTemplate.execute(limitScript, keys, count, time);
        if (number == null || number.intValue() > count) {
            throw new RateLimitException("访问过于频繁，请稍候再试");
        }
    }

    /**
     * @Description:获取IP或者其他限流方式
     * @author: xph
     * @param[1] rateLimiter
     * @param[2] point
     * @return String
     * @Date: 2024/10/20 15:45
     */

    public String getCombineKey(RateLimiter rateLimiter, JoinPoint point) {
        StringBuilder stringBuilder = new StringBuilder(rateLimiter.key());
        if (rateLimiter.limitType() == LimiterType.IP) {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            assert attributes != null;
            String ip = IpUtil.getIp(attributes.getRequest());
            stringBuilder.append(ip);
        }
        return stringBuilder.toString();
    }
}
