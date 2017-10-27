package com.conviction.cache;

import com.conviction.container.AbstractContainer;

public abstract class AbstractCache extends AbstractContainer implements ICache
{
    protected AbstractCache(String description)
    {
        super(description);
    }
}
