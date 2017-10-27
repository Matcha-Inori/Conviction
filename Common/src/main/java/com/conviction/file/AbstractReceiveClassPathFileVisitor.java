package com.conviction.file;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

public abstract class AbstractReceiveClassPathFileVisitor<RESULT_TYPE>
        extends AbstractClassPathFileVisitor
        implements IReceiveClassPathFileVisitor<Path, RESULT_TYPE>
{
    protected AtomicBoolean start;
    protected Collection<RESULT_TYPE> resultCollection;

    public AbstractReceiveClassPathFileVisitor(String basePathStr)
    {
        this(basePathStr, false);
    }

    public AbstractReceiveClassPathFileVisitor(String basePathStr, boolean visitDirectory)
    {
        this(basePathStr, visitDirectory, ArrayList::new);
    }

    public AbstractReceiveClassPathFileVisitor(String basePathStr,
                                               boolean visitDirectory,
                                               Supplier<? extends Collection> collectionSupplier)
    {
        super(basePathStr, visitDirectory);
        this.resultCollection = collectionSupplier.get();
        this.start = new AtomicBoolean(false);
    }

    @Override
    public void startVisit()
    {
        if(!start.compareAndSet(false, true)) return;
        super.startVisit();
    }

    protected void addResult(RESULT_TYPE result)
    {
        this.resultCollection.add(result);
    }

    @Override
    public Collection<RESULT_TYPE> getResultCollection()
    {
        return start.get() ? this.resultCollection : null;
    }
}
