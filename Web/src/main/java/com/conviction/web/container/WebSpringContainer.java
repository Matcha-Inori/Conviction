package com.conviction.web.container;

import com.conviction.container.AbstractContainer;
import com.conviction.web.constant.WebConstant;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.XmlWebApplicationContext;

import javax.servlet.ServletContext;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class WebSpringContainer extends AbstractContainer
{
    private static final String SPRING_CONFIG_POSITION_PREFIX;
    private static final String SPRING_CONFIG_POSITION_KEY;
    private static final String SPRING_CONFIG_POSITION_SPLIT;

    static
    {
        SPRING_CONFIG_POSITION_PREFIX = "classpath*:";
        SPRING_CONFIG_POSITION_KEY = "spring.config.position";
        SPRING_CONFIG_POSITION_SPLIT = ",";
    }

    private ServletContext servletContext;
    private ConfigurableWebApplicationContext webApplicationContext;

    public WebSpringContainer()
    {
        super("WebSpringContainer");
        this.webApplicationContext = null;
    }

    @Override
    public void start()
    {
        if(!this.start.compareAndSet(false, true)) return;
        this.doStart();
    }

    @Override
    protected void doStart()
    {
        String springConfigPosition = this.iEnvironment.getProperty(WebConstant.MODEL_NAME, SPRING_CONFIG_POSITION_KEY);
        Optional<String> springConfigPositionOptional = Optional.ofNullable(springConfigPosition);
        List<String> springConfigPositions = springConfigPositionOptional
                .map(position -> position.split(SPRING_CONFIG_POSITION_SPLIT))
                .map(
                        positionArray -> Arrays.stream(positionArray)
                                .map(position -> SPRING_CONFIG_POSITION_PREFIX + position)
                                .collect(Collectors.toList())
                )
                .orElseGet(Collections::emptyList);

        this.webApplicationContext = new XmlWebApplicationContext();
        this.webApplicationContext.setServletContext(servletContext);
        this.webApplicationContext.setConfigLocations(springConfigPositions.toArray(new String[0]));
        this.webApplicationContext.registerShutdownHook();
        this.webApplicationContext.refresh();
    }

    @Override
    protected void doStop()
    {
        this.webApplicationContext.close();
    }

    public ServletContext getServletContext()
    {
        return servletContext;
    }

    public void setServletContext(ServletContext servletContext)
    {
        this.servletContext = servletContext;
    }

    public WebApplicationContext getWebApplicationContext()
    {
        return this.start.get() ? webApplicationContext : null;
    }
}
