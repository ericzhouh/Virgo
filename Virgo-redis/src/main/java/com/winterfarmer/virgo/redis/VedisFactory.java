package com.winterfarmer.virgo.redis;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Created by yangtianhang on 15-4-7.
 */
public class VedisFactory implements ApplicationContextAware {
    private static ApplicationContext context;

    public static Vedis getVedis(RedisBiz redisBiz) {
        System.out.println("=============" + redisBiz.name().toLowerCase() + "Vedis");
        for (String beanName : context.getBeanDefinitionNames()) {
            System.out.println("====== bean =======" + beanName);
        }

        return context.getBean(redisBiz.name().toLowerCase() + "Vedis", Vedis.class);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }
}
