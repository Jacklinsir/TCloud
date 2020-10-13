package com.tcloud.common.security.handler;

import com.tcloud.common.core.result.CommonResult;
import com.tcloud.common.core.toolkit.T;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Description: 校验令牌入口
 * <br/>
 * TCloudAuthExceptionEntryPoint
 * Created by laiql on 2020/10/13 15:46.
 */
@Slf4j
public class TCloudAuthExceptionEntryPoint implements AuthenticationEntryPoint
{

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException
    {
        String requestUri = request.getRequestURI();
        int status = HttpServletResponse.SC_UNAUTHORIZED;
        String message = "访问令牌不合法";
        log.error("客户端访问{}请求失败: {}", requestUri, message, authException);
        T.util().http().makeJsonResponse(response, status, CommonResult.failed(message));
    }
}
