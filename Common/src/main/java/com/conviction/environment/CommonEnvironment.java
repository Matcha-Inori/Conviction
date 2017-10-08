package com.conviction.environment;

import com.conviction.common.util.ClassLoaderUtil;
import com.conviction.environment.info.EnvironmentInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class CommonEnvironment implements IEnvironment
{
    private static final Logger logger;

    private static final String PROPERTIES_FILE_POSITION;

    static
    {
        logger = LoggerFactory.getLogger(CommonEnvironment.class);
        PROPERTIES_FILE_POSITION = "META-INF/config/";
    }

    private Map<String, Properties> propertiesMap;

    public CommonEnvironment()
    {
        propertiesMap = new ConcurrentHashMap<>(8);
        load();
    }

    private void load()
    {
        try
        {
            ClassLoader classLoader = ClassLoaderUtil.findClassLoader();
            Enumeration<URL> enumeration = classLoader.getResources(PROPERTIES_FILE_POSITION);
            if(null == enumeration) return;
            URL resourceURL;
            URI resourceURI;
            Path resourcePath;
            while(enumeration.hasMoreElements())
            {
                resourceURL = enumeration.nextElement();
                resourceURI = resourceURL.toURI();
                resourcePath = Paths.get(resourceURI);
                if(!Files.isDirectory(resourcePath)) continue;
                Files.walkFileTree(resourcePath, new CommonFileVisitor(resourcePath));
            }
        }
        catch (IOException | URISyntaxException e)
        {
            logger.error(EnvironmentInfo.LOAD_CONFIG_FAIL, e);
        }
    }

    @Override
    public String getProperty(String modelName, String propertyName)
    {
        return getProperty(modelName, propertyName, null);
    }

    @Override
    public String getProperty(String modelName, String propertyName, String defaultValue)
    {
        Properties properties = propertiesMap.get(modelName);
        if(null == properties) return defaultValue;
        return properties.getProperty(propertyName, defaultValue);
    }

    @Override
    public String setProperty(String modelName, String propertyName, String newValue)
    {
        Properties properties = propertiesMap.get(modelName);
        if(null == properties)
        {
            propertiesMap.putIfAbsent(modelName, new Properties());
            properties = propertiesMap.get(modelName);
        }
        return (String) properties.setProperty(propertyName, newValue);
    }

    @Override
    public String getSystemProperty(String propertyName)
    {
        return System.getProperty(propertyName);
    }

    @Override
    public String getSystemProperty(String propertyName, String defaultValue)
    {
        return System.getProperty(propertyName, defaultValue);
    }

    @Override
    public String setSystemProperty(String propertyName, String newValue)
    {
        return System.setProperty(propertyName, newValue);
    }

    private class CommonFileVisitor extends SimpleFileVisitor<Path>
    {
        private Path basePath;

        public CommonFileVisitor(Path basePath)
        {
            this.basePath = basePath;
        }

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attributes) throws IOException
        {
            if(this.basePath.equals(dir))
                return FileVisitResult.CONTINUE;

            Path relativePath = basePath.relativize(dir);
            int relativePathNameCount = relativePath.getNameCount();
            if(relativePathNameCount > 1)
                return FileVisitResult.SKIP_SUBTREE;

            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) throws IOException
        {
            Path relativePath = basePath.relativize(file);
            int relativePathNameCount = relativePath.getNameCount();
            if(relativePathNameCount > 2)
                return FileVisitResult.CONTINUE;
            String modelName = relativePath.getName(0).toString();
            Properties properties = propertiesMap.get(modelName);
            if(null == properties)
            {
                propertiesMap.putIfAbsent(modelName, new Properties());
                properties = propertiesMap.get(modelName);
            }

            try(
                    InputStream inputStream = Files.newInputStream(file, StandardOpenOption.READ);
                    BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream)
            )
            {
                properties.load(bufferedInputStream);
            }
            catch (IOException exception)
            {
                logger.error(EnvironmentInfo.LOAD_CONFIG_FAIL, exception);
            }

            return FileVisitResult.CONTINUE;
        }
    }
}
