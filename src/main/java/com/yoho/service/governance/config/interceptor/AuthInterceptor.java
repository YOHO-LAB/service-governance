package com.yoho.service.governance.config.interceptor;


import com.yoho.service.governance.mapper.AccountMapper;
import com.yoho.service.governance.model.Account;
import com.yoho.service.governance.util.CookieUtil;
import com.yoho.service.governance.util.MD5Utils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Component
public class AuthInterceptor implements HandlerInterceptor {
    protected final static Log log = LogFactory.getLog(AuthInterceptor.class);

    @Autowired
    private AccountMapper accountMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws IOException {

        HttpSession session = request.getSession();

        Cookie[] cookies = request.getCookies();
        Cookie usernameCookie = CookieUtil.getCookieByName("u_", cookies);
        Cookie passwordCookie = CookieUtil.getCookieByName("p_", cookies);
        if (usernameCookie != null && passwordCookie != null) {
            Account currentAccount = accountMapper.queryAccountByUsername(usernameCookie.getValue());
            if (currentAccount != null && passwordCookie.getValue().equals(MD5Utils.md5(currentAccount.getPassword()))) {
                session.setAttribute("account", currentAccount);
                return true;
            }
        }
        boolean isAjaxRequest = false;
        if (!StringUtils.isBlank(request.getHeader("x-requested-with")) && request.getHeader("x-requested-with").equals("XMLHttpRequest")) {
            isAjaxRequest = true;
        }
        if (isAjaxRequest) {
            response.setStatus(401);
        } else {
            response.sendRedirect("login");
        }

        return false;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
