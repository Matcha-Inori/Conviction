package com.conviction.cache.factory;

import com.alibaba.dubbo.common.extension.ExtensionLoader;
import com.conviction.cache.ICache;
import org.junit.Assert;
import org.junit.Test;

public class SpiCacheFactoryTest
{
    @Test
    public void getCache() throws Exception
    {
        ExtensionLoader<ICacheFactory> extensionLoader = ExtensionLoader.getExtensionLoader(ICacheFactory.class);
        ICacheFactory iCacheFactory = extensionLoader.getExtension("spi");
        ICache iCache = iCacheFactory.getCache("redis");
        Assert.assertNotNull(iCache);
    }
}