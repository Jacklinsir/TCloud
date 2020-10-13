package com.tcloud.common.security.handler;

import com.tcloud.common.core.result.CommonResult;
import com.tcloud.common.core.toolkit.T;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Description: 访问权限处理器
 * <br/>
 * FebsAccessDeniedHandler
 * Created by laiql on 2020/10/13 15:44.
 */
public class TCloudAccessDeniedHandler implements AccessDeniedHandler
{

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException
    {
        T.util().http().makeJsonResponse(response, HttpServletResponse.SC_FORBIDDEN, CommonResult.failed("没有权限访问该资源"));
    }
}
