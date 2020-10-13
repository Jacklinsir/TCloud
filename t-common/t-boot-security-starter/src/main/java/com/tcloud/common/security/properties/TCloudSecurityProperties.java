package com.tcloud.common.security.properties;

import com.tcloud.common.core.domain.constant.EndpointConstant;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Description: 安全配置屬性
 * <br/>
 * TCloudSecurityProperties
 * Created by laiql on 2020/10/13 15:31.
 */
@Data
@ConfigurationProperties(prefix = "tcloud.cloud.security")
public class TCloudSecurityProperties
{

    /**
     * 是否开启安全配置
     */
    private Boolean enable;
    /**
     * 配置需要认证的uri，默认为所有/**
     */
    private String authUri = EndpointConstant.ALL;
    /**
     * 免认证资源路径，支持通配符
     * 多个值时使用逗号分隔
     */
    private String anonUris;
    /**
     * 是否只能通过网关获取资源
     */
    private Boolean onlyFetchByGateway = Boolean.TRUE;
}
