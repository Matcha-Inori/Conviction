package com.conviction.file;

import java.nio.file.FileVisitor;

public interface IClassPathFileVisitor<PATH_TYPE> extends FileVisitor<PATH_TYPE>
{
    void startVisit();
}
