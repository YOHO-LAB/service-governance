package com.yoho.service.governance.util;

import javax.servlet.http.Cookie;

/**
 * Created by zhiqi.liu@yoho.cn on 2017/4/12.
 */
public class CookieUtil {

    public static Cookie getCookieByName(String name, Cookie[] cookies){
        Cookie cookie = null;
        if(cookies != null && cookies.length > 0){
            for (Cookie c : cookies) {
                if(name.equals(c.getName())){
                    cookie = c;
                }
            }
        }
        return cookie;
    }
}
