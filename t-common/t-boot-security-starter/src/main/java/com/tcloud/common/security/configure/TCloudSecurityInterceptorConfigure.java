package com.tcloud.common.security.configure;

import com.tcloud.common.security.interceptor.TCloudServerProtectInterceptor;
import com.tcloud.common.security.properties.TCloudSecurityProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Description: 注册拦截器
 * <br/>
 * TCloudSecurityInterceptorConfigure
 * Created by laiql on 2020/10/13 15:51.
 */
public class TCloudSecurityInterceptorConfigure implements WebMvcConfigurer
{

    private TCloudSecurityProperties properties;

    @Autowired
    public void setProperties(TCloudSecurityProperties properties)
    {
        this.properties = properties;
    }

    @Bean
    public HandlerInterceptor cloudServerProtectInterceptor()
    {
        TCloudServerProtectInterceptor cloudServerProtectInterceptor = new TCloudServerProtectInterceptor();
        cloudServerProtectInterceptor.setProperties(properties);
        return cloudServerProtectInterceptor;
    }

    @Override
    @SuppressWarnings("all")
    public void addInterceptors(InterceptorRegistry registry)
    {
        registry.addInterceptor(cloudServerProtectInterceptor());
    }
}
