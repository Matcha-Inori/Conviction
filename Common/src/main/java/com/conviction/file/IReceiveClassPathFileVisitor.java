package com.conviction.file;

import java.util.Collection;

public interface IReceiveClassPathFileVisitor<PATH_TYPE, RESULT_TYPE> extends IClassPathFileVisitor<PATH_TYPE>
{
    Collection<RESULT_TYPE> getResultCollection();
}
