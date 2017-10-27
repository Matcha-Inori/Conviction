package com.conviction.cache;

import com.alibaba.dubbo.common.extension.SPI;
import com.alibaba.dubbo.container.Container;

@SPI("redis")
public interface ICache extends Container
{

}
