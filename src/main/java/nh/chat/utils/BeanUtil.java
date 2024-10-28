package nh.chat.utils;

import jakarta.annotation.Nonnull;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @Description:获取对象实例
 * @Author: xph
 * @Date: 2024-09-25 17:17
 */
@Component
public class BeanUtil implements ApplicationContextAware {
    static ApplicationContext applicationContext = null;

    @Override
    public void setApplicationContext(@Nonnull ApplicationContext arg0) throws BeansException {
        if (applicationContext == null) applicationContext = arg0;
    }

    public static Object getBean(String name) {
        return applicationContext.getBean(name);
    }

    /**
     * 拿到ApplicationContext对象实例后就可以手动获取Bean的注入实例对象
     */
    public static <T> T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }
}
