package com.winterfarmer.virgo.restapi.core.param;

import com.google.common.collect.ImmutableMap;
import com.google.common.reflect.ClassPath;
import com.winterfarmer.virgo.log.VirgoLogger;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
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

    private final ImmutableMap<String, Method> instanceOfMethodMap;

    private ParamSpecFactory() {
        final ClassLoader loader = Thread.currentThread().getContextClassLoader();
        ImmutableMap.Builder<String, Method> instanceOfMapBuilder = ImmutableMap.builder();

        try {
            for (final ClassPath.ClassInfo info : ClassPath.from(loader).getTopLevelClasses(PARAM_PACKAGE.getName())) {
                Class<?> clazz = Class.forName(info.getName());
                putIfParamSpec(clazz, instanceOfMapBuilder);
            }
        } catch (IOException e) {
            VirgoLogger.fatal(e, e.getMessage());
        } catch (NoSuchMethodException e) {
            VirgoLogger.fatal(e, e.getMessage());
        } catch (InvocationTargetException e) {
            VirgoLogger.fatal(e, e.getMessage());
        } catch (IllegalAccessException e) {
            VirgoLogger.fatal(e, e.getMessage());
        } catch (ClassNotFoundException e) {
            VirgoLogger.fatal(e, e.getMessage());
        }

        instanceOfMethodMap = instanceOfMapBuilder.build();
    }

    private void putIfParamSpec(Class<?> clazz, ImmutableMap.Builder<String, Method> instanceOfMapBuilder) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        if (isParamSpecClass(clazz)) {
            Method specNameMethod = checkAndGetSpecNameMethod(clazz);
            String specName = (String) specNameMethod.invoke(null);

            Method instanceOfMethod = checkAndGetInstanceOfMethod(clazz);
            instanceOfMapBuilder.put(StringUtils.lowerCase(specName), instanceOfMethod);
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

    private Method checkAndGetInstanceOfMethod(Class<?> clazz) throws NoSuchMethodException {
        return checkAndGetMethod(clazz, AbstractParamSpec.class, "instanceOf", String.class);
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

    public static AbstractParamSpec<?> getParamSpec(String strSpec) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String[] specNameAndValue = AbstractParamSpec.splitSpecNameAndValue(strSpec);

        Method instanceOfMethod = paramSpecFactory.instanceOfMethodMap.get(specNameAndValue[0]);
        if (instanceOfMethod == null) {
            throw new NoSuchMethodException("No param spec for [" + strSpec + "].");
        }

        return (AbstractParamSpec<?>) instanceOfMethod.invoke(null, specNameAndValue[1]);
    }
}
