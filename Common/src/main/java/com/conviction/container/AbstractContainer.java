package com.conviction.container;

import com.alibaba.dubbo.common.extension.ExtensionLoader;
import com.alibaba.dubbo.container.Container;
import com.conviction.environment.IEnvironment;
import com.conviction.hook.ContainerCloseThread;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public abstract class AbstractContainer implements Container
{
    private static final AtomicLong nextId;
    private static final String CLOSE_THREAD_NAME_FORMAT;

    static
    {
        nextId = new AtomicLong(0L);
        CLOSE_THREAD_NAME_FORMAT = "%s-%d";
    }

    private final long id;
    protected AtomicBoolean start;
    private String description;

    protected IEnvironment iEnvironment;

    public AbstractContainer(String description)
    {
        this.description = description;
        this.id = nextId.getAndIncrement();
        this.start = new AtomicBoolean(false);

        ExtensionLoader<IEnvironment> extensionLoader = ExtensionLoader.getExtensionLoader(IEnvironment.class);
        this.iEnvironment = extensionLoader.getDefaultExtension();
    }

    protected abstract void doStart();
    protected abstract void doStop();

    @Override
    public void start()
    {
        if(!this.start.compareAndSet(false, true)) return;
        Runtime runtime = Runtime.getRuntime();
        runtime.addShutdownHook(new ContainerCloseThread(
                String.format(CLOSE_THREAD_NAME_FORMAT, description, id),
                this)
        );
        this.doStart();
    }

    @Override
    public void stop()
    {
        if(!this.start.get()) return;
        this.doStop();
    }
}
