package com.conviction.environment;

import com.alibaba.dubbo.common.extension.SPI;

import java.util.Map;

@SPI(value = "default")
public interface IEnvironment
{
    String getProperty(String modelName, String propertyName);
    String getProperty(String modelName, String propertyName, String defaultValue);
    Map<String, String> getProperties(String modelName, String keyPrefix);
    String setProperty(String modelName, String propertyName, String newValue);

    String getSystemProperty(String propertyName);
    String getSystemProperty(String propertyName, String defaultValue);
    String setSystemProperty(String propertyName, String newValue);
}
