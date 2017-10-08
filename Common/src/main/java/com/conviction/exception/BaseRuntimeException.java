package com.conviction.exception;

public class BaseRuntimeException extends RuntimeException
{
    public BaseRuntimeException()
    {
    }

    public BaseRuntimeException(String message)
    {
        super(message);
    }

    public BaseRuntimeException(String message, Throwable cause)
    {
        super(message, cause);
    }

    public BaseRuntimeException(Throwable cause)
    {
        super(cause);
    }

    public BaseRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace)
    {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
