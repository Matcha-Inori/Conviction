package com.conviction.starter;

import com.alibaba.dubbo.common.extension.ExtensionLoader;
import com.alibaba.dubbo.container.Container;
import com.conviction.common.util.StringUtil;
import com.conviction.environment.IEnvironment;
import com.conviction.starter.constant.StarterConstant;

public class Starter
{
    private static final String CONTAINER_PROPERTY_NAME;

    private static final String DEFAULT_CONTAINER_NAME;

    static
    {
        CONTAINER_PROPERTY_NAME = "conviction.starter.containerName";
        DEFAULT_CONTAINER_NAME = "tomcat";
    }

    public static void main(String[] args)
    {
        //设置log4j配置文件的位置
        System.setProperty("log4j.configuration", "META-INF/log/log4jConfig.properties");

        ExtensionLoader<IEnvironment> environmentLoader = ExtensionLoader.getExtensionLoader(IEnvironment.class);
        IEnvironment iEnvironment = environmentLoader.getDefaultExtension();

        String containerName = null;
        if(args.length != 0)
            containerName = args[0].trim();

        if(StringUtil.isEmpty(containerName))
            containerName = iEnvironment.getProperty(StarterConstant.MODEL_NAME, CONTAINER_PROPERTY_NAME);

        if(StringUtil.isEmpty(containerName))
            containerName = iEnvironment.getSystemProperty(CONTAINER_PROPERTY_NAME, DEFAULT_CONTAINER_NAME);


        ExtensionLoader<Container> containerExtensionLoader = ExtensionLoader.getExtensionLoader(Container.class);
        Container container = containerExtensionLoader.getExtension(containerName);
        container.start();
    }
}
