package com.conviction.file;

import com.conviction.common.util.ClassLoaderUtil;
import com.conviction.file.info.ClassPathFileVisitorInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Enumeration;

public abstract class AbstractClassPathFileVisitor implements IClassPathFileVisitor<Path>
{
    private Logger logger = LoggerFactory.getLogger(AbstractClassPathFileVisitor.class);

    private String basePathStr;
    private boolean visitDirectory;

    protected ClassLoader classLoader;
    protected Path basePath;

    public AbstractClassPathFileVisitor(String basePathStr, boolean visitDirectory)
    {
        this.basePathStr = basePathStr;
        this.visitDirectory = visitDirectory;

        this.classLoader = ClassLoaderUtil.findClassLoader();
        this.basePath = Paths.get(basePathStr);
    }

    @Override
    public void startVisit()
    {
        try
        {
            Enumeration<URL> childURLs = this.classLoader.getResources(basePathStr);
            URL childURL;
            Path childPath;
            while(childURLs.hasMoreElements())
            {
                childURL = childURLs.nextElement();
                childPath = Paths.get(childURL.toURI());
                Files.walkFileTree(childPath, this);
            }
        }
        catch (IOException | URISyntaxException e)
        {
            logger.error(ClassPathFileVisitorInfo.WALK_FILE_TREE_ERROR, e);
        }
    }

    protected abstract void doVisitFile(Path file, BasicFileAttributes attributes);

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attributes) throws IOException
    {
        if(visitDirectory)
            return FileVisitResult.CONTINUE;
        return dir.endsWith(basePath) ? FileVisitResult.CONTINUE : FileVisitResult.SKIP_SUBTREE;
    }

    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) throws IOException
    {
        doVisitFile(file, attributes);
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException
    {
        logger.error(ClassPathFileVisitorInfo.VISIT_FILE_ERROR, exc);
        return FileVisitResult.SKIP_SUBTREE;
    }
}
