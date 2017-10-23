package com.conviction.file;

import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Optional;
import java.util.function.BiFunction;

public class CommonFileVisitor extends AbstractReceiveClassPathFileVisitor<String>
{
    private Optional<BiFunction> fileVisitFunctionOptional;

    public CommonFileVisitor(String basePathStr,
                             boolean visitDirectory,
                             BiFunction<Path, BasicFileAttributes, String> fileVisitFunction)
    {
        super(basePathStr, visitDirectory);
        this.fileVisitFunctionOptional = Optional.ofNullable(fileVisitFunction);
    }

    @Override
    protected void doVisitFile(Path file, BasicFileAttributes attributes)
    {
        BiFunction<Path, BasicFileAttributes, String> fileVisitFunction =
                fileVisitFunctionOptional.orElse((path, fileAttributes) -> path.toString());
        String result = fileVisitFunction.apply(file, attributes);
        this.addResult(result);
    }
}
