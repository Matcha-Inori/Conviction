package com.conviction.common.util;

public class StringUtil
{
    public static boolean isEmpty(String str)
    {
        if(null == str)
            return true;
        return str.length() == 0;
    }

    public static boolean isBlank(String str)
    {
        if(isEmpty(str))
            return true;
        str = str.trim();
        return isEmpty(str);
    }
}
