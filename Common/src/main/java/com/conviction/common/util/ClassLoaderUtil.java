package com.conviction.common.util;

public class ClassLoaderUtil
{
    public static ClassLoader findClassLoader()
    {
        Thread currentThread = Thread.currentThread();
        ClassLoader classLoader = currentThread.getContextClassLoader();
        if(null != classLoader)
            return classLoader;
        classLoader = ClassLoader.class.getClassLoader();
        if(null != classLoader)
            return classLoader;
        return ClassLoader.getSystemClassLoader();
    }
}
