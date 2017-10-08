package com.conviction.exception.util;

public class BizExceptionUtil
{
    public static String genMessage(long code, String info)
    {
        StringBuffer infoBuffer = new StringBuffer();
        infoBuffer.append("Code: ").append(code).append(" info: ").append(info);
        return infoBuffer.toString();
    }
}
