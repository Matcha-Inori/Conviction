package com.conviction.web.servlet;

import com.conviction.file.CommonFileVisitor;
import com.conviction.file.IReceiveClassPathFileVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletContext;
import java.nio.file.Path;
import java.util.Collection;

public class WebDispatcherServlet extends DispatcherServlet
{
    private static final Logger logger = LoggerFactory.getLogger(WebDispatcherServlet.class);

    private static final String SPRING_CONFIG_BASE_POSITION_POSTFIX;
    private static final String SPRING_CONFIG_POSITION_PREFIX;

    static
    {
        SPRING_CONFIG_BASE_POSITION_POSTFIX = "META-INF/spring/";
        SPRING_CONFIG_POSITION_PREFIX = "classpath*:";
    }

    @Override
    protected WebApplicationContext createWebApplicationContext(ApplicationContext parent)
    {
        IReceiveClassPathFileVisitor<Path, String> classPathFileVisitor = new CommonFileVisitor(
                SPRING_CONFIG_BASE_POSITION_POSTFIX,
                false,
                (path, attributes) -> SPRING_CONFIG_POSITION_PREFIX +
                        SPRING_CONFIG_BASE_POSITION_POSTFIX + path.getFileName().toString()
        );
        classPathFileVisitor.startVisit();
        Collection<String> springConfigPositions = classPathFileVisitor.getResultCollection();

        ServletContext servletContext = this.getServletContext();
        ConfigurableWebApplicationContext applicationContext = new XmlWebApplicationContext();
        applicationContext.setServletContext(servletContext);
        applicationContext.setConfigLocations(springConfigPositions.toArray(new String[0]));
        applicationContext.registerShutdownHook();
        applicationContext.refresh();
        return applicationContext;
    }
}