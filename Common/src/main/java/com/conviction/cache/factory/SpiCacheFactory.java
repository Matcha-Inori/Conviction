package com.conviction.cache.factory;

import com.alibaba.dubbo.common.extension.ExtensionLoader;
import com.conviction.cache.ICache;

public class SpiCacheFactory implements ICacheFactory
{
    private ExtensionLoader<ICache> cacheExtensionLoader;

    public SpiCacheFactory()
    {
        this.cacheExtensionLoader = ExtensionLoader.getExtensionLoader(ICache.class);
    }

    @Override
    public ICache getCache(String cacheName)
    {
        ICache iCache = this.cacheExtensionLoader.getExtension(cacheName);
        iCache.start();
        return iCache;
    }
}
