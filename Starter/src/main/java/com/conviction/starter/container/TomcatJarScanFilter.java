package com.conviction.starter.container;

import com.conviction.common.util.StringUtil;
import org.apache.tomcat.JarScanFilter;
import org.apache.tomcat.JarScanType;

public class TomcatJarScanFilter implements JarScanFilter
{
    @Override
    public boolean check(JarScanType jarScanType, String jarName)
    {
        if(StringUtil.isEmpty(jarName))
            return false;
        return jarName.startsWith("tomcat");
    }
}
