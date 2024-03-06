package com.nowcoder.community.util;

import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;


public class CookieUtil {

    public static String getValue(HttpServletRequest request,String name)
    {
        if(request == null || name == null)
        {
            throw  new IllegalArgumentException("参数为空!");
        }

        Cookie[] cookies =  request.getCookies();

        if(cookies == null)
        {
            return null;
        }else
        {
            for(Cookie aa:cookies)
            {
                if(aa.getName().equals(name))
                {
                    return aa.getValue();
                }
            }
        }

        return null;
    }
}
