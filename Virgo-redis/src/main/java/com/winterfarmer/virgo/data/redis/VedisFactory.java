package com.winterfarmer.virgo.data.redis;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Created by yangtianhang on 15-4-7.
 */
public class VedisFactory implements ApplicationContextAware {
    private static ApplicationContext context;

    public static Vedis getVedis(RedisBiz redisBiz) {
        return context.getBean(redisBiz.name().toLowerCase() + "Vedis", Vedis.class);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }
}
