package nh.chat.limiter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;

/**
 * @Description:限流配置类
 * @Author:xph
 * @Date: 2024/10/20 15:20
 */
@Configuration("NhLimiterConfig")
public class LimiterConfig {
    @Bean
    public DefaultRedisScript<Long> limiterScript() {
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource("lua/Limiter.lua")));
        redisScript.setResultType(Long.class);
        return redisScript;
    }

}
