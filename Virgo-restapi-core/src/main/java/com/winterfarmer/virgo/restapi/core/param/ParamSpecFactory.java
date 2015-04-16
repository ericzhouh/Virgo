package com.winterfarmer.virgo.restapi.core.param;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;
import com.winterfarmer.virgo.log.VirgoLogger;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * Created by yangtianhang on 15-1-11.
 * ParamSpecFactory可以有两种方法实现
 * 1. 动态注册或者注入注册所有的ParamSpec
 * 2. 写死创建过程
 * 1和2分别反应了灵活性和易用性
 * 在灵活性和易用性之间，我选择了易用性。
 * reason: 毕竟新的ParamSpec很少，让别人看懂和可以明确的使用更为重要
 * 还是用反射。。。
 */
public class ParamSpecFactory {
    private final static Package PARAM_PACKAGE = ParamSpecFactory.class.getPackage();
    private final static ParamSpecFactory paramSpecFactory = new ParamSpecFactory();

    private final ImmutableMap<String, Constructor> paramSpecMap;

    private ParamSpecFactory() {
        ImmutableMap.Builder<String, Constructor> paramSpecMapBuilder = ImmutableMap.builder();

        try {
            for (final ClassPath.ClassInfo classInfo : getParamSpecClassInfos()) {
                VirgoLogger.info("load param spec: {}", classInfo.getSimpleName());
                Class<?> clazz = Class.forName(classInfo.getName());
                putIfParamSpec(clazz, paramSpecMapBuilder);
            }
        } catch (Exception e) {
            VirgoLogger.fatal("Init param spec factory failed.", e);
        }

        paramSpecMap = paramSpecMapBuilder.build();
    }

    private ImmutableSet<ClassPath.ClassInfo> getParamSpecClassInfos() throws IOException {
        final ClassLoader loader = Thread.currentThread().getContextClassLoader();
        // Param Spec Classes is in package 'PARAM_PACKAGE', so we scan it
        return ClassPath.from(loader).getTopLevelClasses(PARAM_PACKAGE.getName());
    }

    private void putIfParamSpec(Class<?> clazz, ImmutableMap.Builder<String, Constructor> paramSpecMapBuilder) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if (isParamSpecClass(clazz)) {
            Method specNameMethod = checkAndGetSpecNameMethod(clazz);
            String specName = (String) specNameMethod.invoke(null);

            Constructor constructor = checkAndGetConstructor(clazz);
            paramSpecMapBuilder.put(StringUtils.lowerCase(specName), constructor);
        }
    }

    private boolean isParamSpecClass(Class<?> clazz) {
        if (clazz == null) {
            return false;
        }

        boolean isSubclassOfAbstractParamSpec = isSubclassOfAbstractParamSpec(clazz);
        boolean isConcreteClass = !Modifier.isAbstract(clazz.getModifiers());
        return isSubclassOfAbstractParamSpec && isConcreteClass;
    }

    private boolean isSubclassOfAbstractParamSpec(Class<?> clazz) {
        if (clazz == null) {
            return false;
        }

        return AbstractParamSpec.class.isAssignableFrom(clazz) || isSubclassOfAbstractParamSpec(clazz.getSuperclass());
    }

    private Constructor<?> checkAndGetConstructor(Class<?> clazz) throws NoSuchMethodException {
        return clazz.getConstructor(String.class);
    }

    private Method checkAndGetSpecNameMethod(Class<?> clazz) throws NoSuchMethodException {
        return checkAndGetMethod(clazz, String.class, "specName");
    }

    private Method checkAndGetMethod(Class<?> clazz, Class<?> returnType, String methodName, Class<?>... parameterTypes) throws NoSuchMethodException {
        final Method specNameMethod = clazz.getDeclaredMethod(methodName, parameterTypes);

        if (!Modifier.isStatic(specNameMethod.getModifiers()) && specNameMethod.getReturnType().isAssignableFrom(returnType)) {
            throw new NoSuchMethodException("class: " + clazz.getName() + " does not has static method [" + methodName + "]");
        }

        return specNameMethod;
    }

    public static AbstractParamSpec<?> getParamSpec(String strSpec) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        String[] specNameAndValue = AbstractParamSpec.splitSpecNameAndValue(strSpec);

        Constructor paramSpecConstructor = paramSpecFactory.paramSpecMap.get(specNameAndValue[0]);
        if (paramSpecConstructor == null) {
            throw new NoSuchMethodException("No param spec for [" + strSpec + "].");
        }

        try {
            return (AbstractParamSpec<?>) paramSpecConstructor.newInstance(specNameAndValue[1]);
        } catch (Exception e) {
            VirgoLogger.warn("getParamSpec for " + strSpec + " failed.", e);
            throw new RuntimeException("getParamSpec for " + strSpec + " failed.");
        }
    }
}
