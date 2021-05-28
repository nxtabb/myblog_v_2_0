package com.hrbeu.conf;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.support.http.StatViewServlet;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

import javax.sql.DataSource;
import java.util.HashMap;

/**
 * @Classname DruidConf
 * @Description TODO
 * @Date 2021/4/14 09:37
 * @Created by nxt
 */
@Configuration
public class DruidConf{
    private DataSource dataSource = null;
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource druidDataSource(){
        dataSource = new DruidDataSource();
        return dataSource;
    }

    //后台监控
    @Bean
    public ServletRegistrationBean<StatViewServlet> staViewServlet(){
        ServletRegistrationBean<StatViewServlet> bean = new ServletRegistrationBean<>(new StatViewServlet(), "/druid/*");
        //配置druid账号密码
        HashMap<String,String> initParameters = new HashMap<>();
        initParameters.put("loginUsername","admin");
        initParameters.put("loginPassword","123");
        //允许谁能访问
        initParameters.put("allow","");
        bean.setInitParameters(initParameters);
        return bean;
    }



}
