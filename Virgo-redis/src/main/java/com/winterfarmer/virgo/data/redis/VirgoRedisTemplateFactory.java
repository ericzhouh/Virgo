package com.winterfarmer.virgo.data.redis;

import com.winterfarmer.virgo.common.model.VirgoModule;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * Created by yangtianhang on 15-4-7.
 */
public class VirgoRedisTemplateFactory implements ApplicationContextAware {
    private static ApplicationContext context;

    private static <String, HV> RedisTemplate<String, HV> getRedisTemplate(VirgoModule virgoModule) {
        return context.getBean(virgoModule.name().toLowerCase() + "RedisTemplate", RedisTemplate.class);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }
}
