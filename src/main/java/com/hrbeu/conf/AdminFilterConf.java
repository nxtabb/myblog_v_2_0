package com.hrbeu.conf;

import com.hrbeu.filter.AdminFilter;
import com.hrbeu.filter.CharacterFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Classname AdminFilterConf
 * @Description TODO
 * @Date 2021/5/9 12:14
 * @Created by nxt
 */
@Configuration
public class AdminFilterConf {
    @Bean
    public FilterRegistrationBean adminFilterRegistration(){
        FilterRegistrationBean registration = new FilterRegistrationBean(new AdminFilter());
        registration.addUrlPatterns("/admin/*"); //
        registration.addInitParameter("excludedPage1","/admin");
        registration.addInitParameter("excludedPage2","/admin/login");
        registration.addInitParameter("excludedPage3","/admin/logout");
        registration.setName("AdminFilter");
        return registration;
    }
}
