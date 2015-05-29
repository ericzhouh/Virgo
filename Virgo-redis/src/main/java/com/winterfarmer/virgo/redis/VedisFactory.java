package com.winterfarmer.virgo.redis;

import org.apache.commons.lang3.text.WordUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Created by yangtianhang on 15-4-7.
 */
public class VedisFactory implements ApplicationContextAware {
    private static ApplicationContext context;

    public static Vedis getVedis(RedisBiz redisBiz) {
        return context.getBean(uncapitalize(redisBiz) + "Vedis", Vedis.class);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    private static final String uncapitalize(RedisBiz redisBiz) {
        return WordUtils.uncapitalize(redisBiz.name());
    }
}
