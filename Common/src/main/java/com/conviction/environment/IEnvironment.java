package com.conviction.environment;

import com.alibaba.dubbo.common.extension.SPI;

@SPI(value = "default")
public interface IEnvironment
{
    String getProperty(String modelName, String propertyName);
    String getProperty(String modelName, String propertyName, String defaultValue);
    String setProperty(String modelName, String propertyName, String newValue);

    String getSystemProperty(String propertyName);
    String getSystemProperty(String propertyName, String defaultValue);
    String setSystemProperty(String propertyName, String newValue);
}
