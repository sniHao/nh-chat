package nh.chat.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @Description:获取上下文bean
 * @Author: xph
 * @Date: 2024-12-25 9:55
 */
@Component
public class NhBeanUtil implements ApplicationContextAware {
    static ApplicationContext applicationContext = null;

    @Override
    public void setApplicationContext(ApplicationContext arg) throws BeansException {
        if (applicationContext == null) applicationContext = arg;
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
