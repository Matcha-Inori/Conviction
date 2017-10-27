package com.conviction.web.session;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

public class ConvictionSession implements Serializable
{
    public static final String CONVICTION_SESSION_ID_COOKIE_KEY;

    static
    {
        CONVICTION_SESSION_ID_COOKIE_KEY = "convictionIdCookieKey";
    }

    public static ConvictionSession readConvictionSession(HttpServletRequest request)
    {
        Cookie[] cookies = request.getCookies();
        Optional<String> convictionSessionIdOptinal = Arrays.stream(cookies)
                .filter(cookie -> CONVICTION_SESSION_ID_COOKIE_KEY.equals(cookie.getName()))
                .limit(1)
                .map(cookie -> cookie.getValue())
                .findAny();
        //TODO:Session需要到Redis上面进行查找
        return null;
    }

    private transient UUID convictionSessionUUID;
    private String convictionSeesionId;

    public ConvictionSession()
    {
        //暂时先这样生成唯一ID，后面可以考虑使用分布式ID生成服务，可以自己搞一个
        convictionSessionUUID = UUID.randomUUID();
        convictionSeesionId = convictionSessionUUID.toString();
    }

    public void createConvictionSession(HttpServletResponse response)
    {

    }
}
