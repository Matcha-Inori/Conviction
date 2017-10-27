package com.conviction.web.container;

import com.conviction.common.constant.CommonConstant;
import com.conviction.common.constant.SystemPropertiesKey;
import com.conviction.common.util.StringUtil;
import com.conviction.container.AbstractContainer;
import com.conviction.exception.BaseRuntimeException;
import com.conviction.web.constant.WebConstant;
import com.conviction.web.container.info.TomcatContainerInfo;
import org.apache.catalina.Context;
import org.apache.catalina.Globals;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.Server;
import org.apache.catalina.startup.Tomcat;
import org.apache.tomcat.JarScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;

public class TomcatContainer extends AbstractContainer
{
    private static final Logger logger;

    private static final String TOMCAT_HOME_KEY;
    private static final String TOMCAT_BASE_DIR_KEY;
    private static final String TOMCAT_DOC_BASE_DIR_KEY;

    private static final String DEFAULT_TOMCAT_BASE_DIR;

    static
    {
        logger = LoggerFactory.getLogger(TomcatContainer.class);
        TOMCAT_HOME_KEY = "conviction.starter.tomcatHomeKey";
        TOMCAT_BASE_DIR_KEY = "conviction.starter.tomcatBaseDirKey";
        TOMCAT_DOC_BASE_DIR_KEY = "conviction.starter.tomcatDocBaseDirKey";
        DEFAULT_TOMCAT_BASE_DIR = "tomcatBase";
    }

    private Tomcat tomcat;

    public TomcatContainer()
    {
        super("TomcatContainer");
        tomcat = new Tomcat();
    }

    @Override
    protected void doStart()
    {
        try
        {
            String tomcatHomeDir = iEnvironment.getProperty(WebConstant.MODEL_NAME, TOMCAT_HOME_KEY);
            if(StringUtil.isBlank(tomcatHomeDir))
                throw new BaseRuntimeException("Must set tomcat home!!");

            iEnvironment.setSystemProperty(Globals.CATALINA_HOME_PROP, tomcatHomeDir);

            String tomcatDocBaseDir = iEnvironment.getProperty(WebConstant.MODEL_NAME, TOMCAT_DOC_BASE_DIR_KEY);
            if(StringUtil.isBlank(tomcatDocBaseDir))
                throw new BaseRuntimeException("Must set tomcatDocBaseDir");

            String tomcatBaseDir = iEnvironment.getProperty(WebConstant.MODEL_NAME, TOMCAT_BASE_DIR_KEY);
            if(StringUtil.isBlank(tomcatBaseDir))
            {
                tomcatBaseDir = iEnvironment.getSystemProperty(SystemPropertiesKey.USER_DIR);
                tomcatBaseDir += DEFAULT_TOMCAT_BASE_DIR;
            }
            String contextPath = "/" + CommonConstant.PROJECT_NAME;
            tomcat.setBaseDir(tomcatBaseDir);
            tomcat.setHostname("localhost"/*CommonConstant.PROJECT_NAME*/);
            tomcat.setPort(8668);
            Context context = tomcat.addWebapp(contextPath, tomcatDocBaseDir);
            //由于fragment功能的存在，tomcat会扫描jar包中的web-fragment.xml配置文件，我们这里对其进行过滤，并不每个包都扫描
            //其实通过ClassLoader也可以作到，但是感觉这样的方式更简单一些
            JarScanner jarScanner = context.getJarScanner();
            jarScanner.setJarScanFilter(new TomcatJarScanFilter());
            Server server = tomcat.getServer();
            server.setPort(8669);
            tomcat.init();
            tomcat.start();
            server.await();
        }
        catch (ServletException | LifecycleException e)
        {
            throw new BaseRuntimeException(e);
        }
    }

    @Override
    protected void doStop()
    {
        try
        {
            tomcat.stop();
        }
        catch (LifecycleException e)
        {
            logger.error(TomcatContainerInfo.STOP_CONTAINER_FAIL, e);
        }
    }
}
