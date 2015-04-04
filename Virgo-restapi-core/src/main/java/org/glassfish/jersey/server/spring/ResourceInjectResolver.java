package org.glassfish.jersey.server.spring;

import org.apache.commons.lang3.StringUtils;
import org.glassfish.hk2.api.Injectee;
import org.glassfish.hk2.api.InjectionResolver;
import org.glassfish.hk2.api.ServiceHandle;
import org.springframework.context.ApplicationContext;

import javax.annotation.Resource;
import java.lang.reflect.*;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by yangtianhang on 15-1-28.
 */
public class ResourceInjectResolver implements InjectionResolver<Resource> {
    private static final Logger LOGGER = Logger.getLogger(ResourceInjectResolver.class.getName());

    private volatile ApplicationContext ctx;

    public ResourceInjectResolver(ApplicationContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public Object resolve(Injectee injectee, ServiceHandle<?> serviceHandle) {
        AnnotatedElement parent = injectee.getParent();
        String beanName = null;

        if (parent != null) {
            Resource res = parent.getAnnotation(Resource.class);
            if (res != null) {
                beanName = res.name();
                if (StringUtils.isBlank(beanName)) {
                    if (parent instanceof Field) {
                        beanName = ((Field) parent).getName();
                        LOGGER.info("parent instanceof Field beanName: " + beanName);
                    } else if (parent instanceof Method) {
                        beanName = ((Method) parent).getName().substring(3);
                        LOGGER.info("parent instanceof Method beanName: " + beanName);
                    } else {
                        throw new UnsupportedOperationException("unsupported annotated object");
                    }
                }
            }
        }

        return getBeanFromSpringContext(beanName, injectee.getRequiredType());
    }

    private Object getBeanFromSpringContext(String beanName, Type beanType) {
        Class<?> bt = getClassFromType(beanType);
        if (beanName != null) {
            return ctx.getBean(beanName, bt);
        }

        Map<String, ?> beans = ctx.getBeansOfType(bt);
        if (beans == null || beans.size() != 1) {
            LOGGER.warning(LocalizationMessages.NO_BEANS_FOUND_FOR_TYPE(beanType));
            return null;
        }

        return beans.values().iterator().next();
    }

    private Class<?> getClassFromType(Type type) {
        if (type instanceof Class) {
            return (Class<?>) type;
        }
        if (type instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) type;

            return (Class<?>) pt.getRawType();
        }
        return null;
    }


    @Override
    public boolean isConstructorParameterIndicator() {
        return false;
    }

    @Override
    public boolean isMethodParameterIndicator() {
        return false;
    }
}
