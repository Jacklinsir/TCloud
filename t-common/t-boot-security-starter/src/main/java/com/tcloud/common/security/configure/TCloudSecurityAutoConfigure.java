package com.tcloud.common.security.configure;

import com.tcloud.common.core.domain.constant.CloudConstant;
import com.tcloud.common.core.toolkit.T;
import com.tcloud.common.security.handler.TCloudAccessDeniedHandler;
import com.tcloud.common.security.handler.TCloudAuthExceptionEntryPoint;
import com.tcloud.common.security.properties.TCloudSecurityProperties;
import feign.RequestInterceptor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.config.annotation.method.configuration.GlobalMethodSecurityConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.expression.OAuth2MethodSecurityExpressionHandler;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.util.Base64Utils;

@EnableConfigurationProperties(TCloudSecurityProperties.class)
@ConditionalOnProperty(value = "tcloud.cloud.security.enable", havingValue = "true", matchIfMissing = true)
public class TCloudSecurityAutoConfigure extends GlobalMethodSecurityConfiguration
{
    @Bean
    @ConditionalOnMissingBean(name = "accessDeniedHandler")
    public TCloudAccessDeniedHandler accessDeniedHandler()
    {
        return new TCloudAccessDeniedHandler();
    }

    @Bean
    @ConditionalOnMissingBean(name = "authenticationEntryPoint")
    public TCloudAuthExceptionEntryPoint authenticationEntryPoint()
    {
        return new TCloudAuthExceptionEntryPoint();
    }

    @Bean
    @ConditionalOnMissingBean(value = PasswordEncoder.class)
    public PasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public TCloudSecurityInterceptorConfigure cloudSecurityInterceptorConfigure()
    {
        return new TCloudSecurityInterceptorConfigure();
    }

    @Bean
    @Primary
    @ConditionalOnMissingBean(DefaultTokenServices.class)
    public TCloudUserInfoTokenServices tCloudUserInfoTokenServices(ResourceServerProperties properties)
    {
        return new TCloudUserInfoTokenServices(properties.getUserInfoUri(), properties.getClientId());
    }

    @Bean
    public RequestInterceptor oauth2FeignRequestInterceptor()
    {
        return requestTemplate -> {
            String gatewayToken = new String(Base64Utils.encode(CloudConstant.GATEWAY_TOKEN_VALUE.getBytes()));
            requestTemplate.header(CloudConstant.GATEWAY_TOKEN_HEADER, gatewayToken);
            String authorizationToken = T.util().http().getCurrentTokenValue();
            if (StringUtils.isNotBlank(authorizationToken))
            {
                requestTemplate.header(HttpHeaders.AUTHORIZATION, CloudConstant.OAUTH2_TOKEN_TYPE + authorizationToken);
            }
        };
    }

    @Override
    protected MethodSecurityExpressionHandler createExpressionHandler()
    {
        return new OAuth2MethodSecurityExpressionHandler();
    }
}
