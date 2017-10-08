package com.conviction.web.context;

import com.alibaba.dubbo.common.extension.ExtensionLoader;
import com.conviction.common.util.ClassLoaderUtil;
import com.conviction.common.util.StringUtil;
import com.conviction.environment.IEnvironment;
import com.conviction.web.constant.WebConstant;
import com.conviction.web.info.WebContextInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.File;

public class WebContext implements ServletContextListener
{
    private static final Logger logger = LoggerFactory.getLogger(WebContext.class);

    private static final String SPRING_CONFIG_POSITION;
    private static final String SPRING_CONFIG_POSITION_SEPARATOR;

    static
    {
        SPRING_CONFIG_POSITION = "conviction.web.springConfigPosition";
        SPRING_CONFIG_POSITION_SEPARATOR = ";";
    }

    private IEnvironment iEnvironment;
    private ApplicationContext context;

    public WebContext()
    {
        ExtensionLoader<IEnvironment> extensionLoader = ExtensionLoader.getExtensionLoader(IEnvironment.class);
        this.iEnvironment = extensionLoader.getDefaultExtension();
        this.context = null;
    }

    @Override
    public void contextInitialized(ServletContextEvent sce)
    {
        String springConfigPosition = iEnvironment.getProperty(WebConstant.MODEL_NAME, SPRING_CONFIG_POSITION);
        if(StringUtil.isEmpty(springConfigPosition))
        {
            logger.warn(WebContextInfo.CAN_NOT_FOUND_ANY_SPRING_CONFIG_FILE);
            return;
        }
        ClassLoader classLoader = ClassLoaderUtil.findClassLoader();
        String[] springConfigPositions = springConfigPosition.split(SPRING_CONFIG_POSITION_SEPARATOR);

        for(String position : springConfigPositions)
        {
            classLoader.getResource(position);
        }

        ServletContext servletContext = sce.getServletContext();
        ConfigurableWebApplicationContext applicationContext = new XmlWebApplicationContext();
        applicationContext.setServletContext(servletContext);
        applicationContext.setConfigLocations(springConfigPosition.split(File.pathSeparator));
        applicationContext.registerShutdownHook();
        applicationContext.refresh();
        this.context = applicationContext;
        servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, context);
    }

    public ApplicationContext getContext()
    {
        return context;
    }
}
