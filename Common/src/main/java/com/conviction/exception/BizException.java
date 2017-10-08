package com.conviction.exception;

import com.conviction.exception.util.BizExceptionUtil;

public class BizException extends BaseException
{
    private long code;
    private String info;

    public BizException(long code, String info)
    {
        super(BizExceptionUtil.genMessage(code, info));
        this.code = code;
        this.info = info;
    }

    public BizException(long code, String info, Throwable cause)
    {
        super(BizExceptionUtil.genMessage(code, info), cause);
        this.code = code;
        this.info = info;
    }

    public BizException(long code,
                        String info,
                        Throwable cause,
                        boolean enableSuppression,
                        boolean writableStackTrace)
    {
        super(BizExceptionUtil.genMessage(code, info), cause, enableSuppression, writableStackTrace);
        this.code = code;
        this.info = info;
    }
}
