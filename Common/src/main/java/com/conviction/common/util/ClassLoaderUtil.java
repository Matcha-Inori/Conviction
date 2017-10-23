package com.conviction.common.util;

import java.util.Optional;

public class ClassLoaderUtil
{
    public static ClassLoader findClassLoader()
    {
        Thread currentThread = Thread.currentThread();
        ClassLoader classLoader;
        if(null != (classLoader = currentThread.getContextClassLoader()) ||
                null != (classLoader = ClassLoaderUtil.class.getClassLoader()))
            return classLoader;
        else
            return ClassLoader.getSystemClassLoader();
    }

    public static Optional<ClassLoader> findClassLoaderOptional()
    {
        return Optional.ofNullable(findClassLoader());
    }
}
