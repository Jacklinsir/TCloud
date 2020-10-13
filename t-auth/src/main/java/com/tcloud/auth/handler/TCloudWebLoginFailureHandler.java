package com.tcloud.auth.handler;

import com.tcloud.common.core.result.CommonResult;
import com.tcloud.common.core.toolkit.T;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Description:
 * <br/>
 * TCloudWebLoginFailureHandler
 * Created by laiql on 2020/10/13 17:53.
 */
@Component
public class TCloudWebLoginFailureHandler implements AuthenticationFailureHandler
{
    @Override
    public void onAuthenticationFailure(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AuthenticationException exception) throws IOException
    {
        String message;
        if (exception instanceof BadCredentialsException)
        {
            message = "用户名或密码错误！";
        }
        else if (exception instanceof LockedException)
        {
            message = "用户已被锁定！";
        }
        else
        {
            message = "认证失败，请联系网站管理员！";
        }
        T.util().http().makeFailureResponse(httpServletResponse, CommonResult.failed(message));
    }
}
