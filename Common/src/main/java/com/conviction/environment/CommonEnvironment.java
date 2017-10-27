package com.conviction.environment;

import com.conviction.environment.info.EnvironmentInfo;
import com.conviction.file.AbstractClassPathFileVisitor;
import com.conviction.file.IClassPathFileVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collector;
import java.util.stream.Collectors;

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
        IClassPathFileVisitor<Path> fileVisitor = new EnvironmentFileVisitor();
        fileVisitor.startVisit();
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
    public Map<String, String> getProperties(String modelName, String keyPrefix)
    {
        Properties properties = propertiesMap.get(modelName);
        if(null == properties) return Collections.emptyMap();
        Set<Map.Entry<Object, Object>> entries = properties.entrySet();
        Map<String, String> theProperties = entries.stream()
                .filter(entry -> ((String) entry.getKey()).startsWith(keyPrefix))
                .collect(Collectors.toMap(
                        (entry) -> (String) entry.getKey(),
                        (entry) -> (String) entry.getValue()
                ));
        return theProperties;
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

    private class EnvironmentFileVisitor extends AbstractClassPathFileVisitor
    {
        public EnvironmentFileVisitor()
        {
            super(PROPERTIES_FILE_POSITION, true);
        }

        @Override
        protected void doVisitFile(Path file, BasicFileAttributes attributes)
        {
            Optional<Path> filePositionOptional = Optional.ofNullable(file.getParent());
            Optional<Path> filePositionParentOptional = filePositionOptional.map(Path::getParent)
                    .filter(filePositionParent -> filePositionParent.endsWith(this.basePath));

            if(!filePositionParentOptional.isPresent()) return;

            Path filePosition = filePositionOptional.get();

            String modelName = filePosition.getFileName().toString();
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
        }

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attributes) throws IOException
        {
            //只遍历META-INF/config/和META-INF/config/ABC/这样的目录
            if(dir.endsWith(this.basePath)) return FileVisitResult.CONTINUE;

            Path parentDirectory = dir.getParent();
            return null == parentDirectory || !parentDirectory.endsWith(this.basePath) ?
                    FileVisitResult.SKIP_SUBTREE :
                    FileVisitResult.CONTINUE;
        }
    }
}
