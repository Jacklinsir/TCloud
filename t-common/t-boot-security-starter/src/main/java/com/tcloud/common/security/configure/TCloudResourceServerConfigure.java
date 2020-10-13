package com.tcloud.common.security.configure;

import com.tcloud.common.core.domain.constant.StringConstant;
import com.tcloud.common.security.handler.TCloudAccessDeniedHandler;
import com.tcloud.common.security.handler.TCloudAuthExceptionEntryPoint;
import com.tcloud.common.security.properties.TCloudSecurityProperties;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;


@EnableResourceServer
@EnableAutoConfiguration(exclude = UserDetailsServiceAutoConfiguration.class)
public class TCloudResourceServerConfigure extends ResourceServerConfigurerAdapter {

    private TCloudSecurityProperties properties;
    private TCloudAccessDeniedHandler accessDeniedHandler;
    private TCloudAuthExceptionEntryPoint exceptionEntryPoint;

    @Autowired
    public void setProperties(TCloudSecurityProperties properties) {
        this.properties = properties;
    }

    @Autowired
    public void setAccessDeniedHandler(TCloudAccessDeniedHandler accessDeniedHandler) {
        this.accessDeniedHandler = accessDeniedHandler;
    }

    @Autowired
    public void setExceptionEntryPoint(TCloudAuthExceptionEntryPoint exceptionEntryPoint) {
        this.exceptionEntryPoint = exceptionEntryPoint;
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        String[] anonUrls = StringUtils.splitByWholeSeparatorPreserveAllTokens(properties.getAnonUris(), StringConstant.COMMA);
        if (ArrayUtils.isEmpty(anonUrls)) {
            anonUrls = new String[]{};
        }

        http.csrf().disable()
                .requestMatchers().antMatchers(properties.getAuthUri())
                .and()
                .authorizeRequests()
                .antMatchers(anonUrls).permitAll()
                .antMatchers(properties.getAuthUri()).authenticated()
                .and()
                .httpBasic();
    }

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        resources.authenticationEntryPoint(exceptionEntryPoint)
                .accessDeniedHandler(accessDeniedHandler);
    }
}
