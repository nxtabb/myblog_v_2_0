package com.hrbeu.filter;

import com.hrbeu.pojo.User;
import org.apache.log4j.Logger;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.WebRequestInterceptor;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * @Classname AdminInterceptor
 * @Description TODO
 * @Date 2021/5/9 11:56
 * @Created by nxt
 */
public class AdminFilter implements Filter {
    private static final Logger logger = Logger.getLogger(AdminFilter.class);
    private static String excludedPage1;
    private static String excludedPage2;
    private static String excludedPage3;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        excludedPage1 = filterConfig.getInitParameter("excludedPage1");
        excludedPage2 = filterConfig.getInitParameter("excludedPage2");
        excludedPage3 = filterConfig.getInitParameter("excludedPage3");
        logger.debug("AdminFilter init");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        if(request.getRequestURI().equals(excludedPage1)||(request.getRequestURI().equals(excludedPage1+"/"))){
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        if(request.getRequestURI().equals(excludedPage2)||(request.getRequestURI().equals(excludedPage2+"/"))){
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        if(request.getRequestURI().equals(excludedPage3)||(request.getRequestURI().equals(excludedPage3+"/"))){
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        HttpSession session = request.getSession();
        User user = (User)session.getAttribute("user");
        if(user==null){
            request.setAttribute("errMsg","后台管理功能必须登录才能使用");
            request.getRequestDispatcher("/admin").forward(servletRequest, servletResponse);
            return;
        }
        if(!user.getAuthority().equals(2)){
            request.setAttribute("errMsg","需要使用管理员账号");
            request.getRequestDispatcher("/admin").forward(servletRequest, servletResponse);

        }else {
            filterChain.doFilter(servletRequest, servletResponse);

        }
    }

    @Override
    public void destroy() {
        logger.debug("AdminFilter destroy");
    }
}
