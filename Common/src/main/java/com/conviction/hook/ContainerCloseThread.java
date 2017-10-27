package com.conviction.hook;

import com.alibaba.dubbo.container.Container;

import java.util.Optional;

public class ContainerCloseThread extends Thread
{
    private static final String CONTAINER_CLOSE_THREAD_NAME_FORMAT;

    static
    {
        CONTAINER_CLOSE_THREAD_NAME_FORMAT = "ContainerThread-%s";
    }

    private Optional<Container> containerOptional;

    public ContainerCloseThread(String name, Container container)
    {
        super(String.format(CONTAINER_CLOSE_THREAD_NAME_FORMAT, name));
        this.containerOptional = Optional.ofNullable(container);
    }

    @Override
    public void run()
    {
        this.containerOptional.ifPresent(Container::stop);
    }
}
