package com.tcloud.common.security.interceptor;

import com.tcloud.common.core.constant.CloudConstant;
import com.tcloud.common.core.result.CommonResult;
import com.tcloud.common.core.toolkit.T;
import com.tcloud.common.security.properties.TCloudSecurityProperties;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.NonNull;
import org.springframework.util.Base64Utils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author MrBird
 */
public class TCloudServerProtectInterceptor implements HandlerInterceptor
{

    private TCloudSecurityProperties properties;

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws IOException
    {
        if (!properties.getOnlyFetchByGateway())
        {
            return true;
        }
        String token = request.getHeader(CloudConstant.GATEWAY_TOKEN_HEADER);
        String gatewayToken = new String(Base64Utils.encode(CloudConstant.GATEWAY_TOKEN_VALUE.getBytes()));
        if (StringUtils.equals(gatewayToken, token))
        {
            return true;
        }
        else
        {
            T.util().http().makeJsonResponse(response, HttpServletResponse.SC_FORBIDDEN, CommonResult.failed("请通过网关获取资源"));
            return false;
        }
    }

    public void setProperties(TCloudSecurityProperties properties)
    {
        this.properties = properties;
    }
}
