package com.conviction.web.servlet;

import com.alibaba.dubbo.common.extension.ExtensionLoader;
import com.alibaba.dubbo.container.Container;
import com.conviction.web.container.WebSpringContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletContext;

public class WebDispatcherServlet extends DispatcherServlet
{
    private static final Logger logger;

    private static final String WEB_SPRING_CONTAINER_NAME;

    static
    {
        logger = LoggerFactory.getLogger(WebDispatcherServlet.class);

        WEB_SPRING_CONTAINER_NAME = "webSpring";
    }

    @Override
    protected WebApplicationContext createWebApplicationContext(ApplicationContext parent)
    {
        ServletContext servletContext = this.getServletContext();
        ExtensionLoader<Container> extensionLoader = ExtensionLoader.getExtensionLoader(Container.class);
        WebSpringContainer container = (WebSpringContainer) extensionLoader.getExtension(WEB_SPRING_CONTAINER_NAME);
        container.setServletContext(servletContext);
        container.start();
        return container.getWebApplicationContext();
    }
}