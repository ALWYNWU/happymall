package com.happymall.member.interceptor;

import com.common.vo.MemberRespVo;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Yilong
 * @date 2022-10-22 5:57 p.m.
 * @description
 */

@Component
public class LoginUserInterceptor implements HandlerInterceptor {

    public static ThreadLocal<MemberRespVo> loginUser = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String uri = request.getRequestURI();
        boolean match = new AntPathMatcher().match("/member/**", uri);
        if (match){
            return true;
        }

        MemberRespVo user = (MemberRespVo) request.getSession().getAttribute("loginUser");
        if (user != null){
            loginUser.set(user);
            return true;
        } else {
            request.getSession().setAttribute("msg","Please sign in");
            response.sendRedirect("http://auth.happymall.com/login.html");
            return false;
        }
    }
}
