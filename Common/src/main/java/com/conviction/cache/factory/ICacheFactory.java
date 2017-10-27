package com.conviction.cache.factory;

import com.alibaba.dubbo.common.extension.SPI;
import com.conviction.cache.ICache;

@SPI("spi")
public interface ICacheFactory
{
    ICache getCache(String cacheName);
}
