package com.tcloud.auth.controller;

import com.tcloud.auth.domain.BindUser;
import com.tcloud.auth.domain.UserConnection;
import com.tcloud.auth.service.SocialLoginService;
import com.tcloud.common.core.domain.constant.StringConstant;
import com.tcloud.common.core.exception.TCloudException;
import com.tcloud.common.core.result.CommonResult;
import com.tcloud.common.core.toolkit.T;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthUser;
import me.zhyd.oauth.request.AuthRequest;
import me.zhyd.oauth.utils.AuthStateUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.io.IOException;
import java.util.List;

/**
 * Description:
 * <br/>
 * SocialLoginController
 * Created by laiql on 2020/10/13 17:56.
 */

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("social")
public class SocialLoginController
{

    private static final String TYPE_LOGIN = "login";
    private static final String TYPE_BIND = "bind";

    private final SocialLoginService socialLoginService;
//    @Value("${tcloud.frontUrl}")
    private String frontUrl;


    /**
     * 登录
     *
     * @param oauthType 第三方登录类型
     * @param response  response
     */
    @ResponseBody
    @GetMapping("/login/{oauthType}/{type}")
    public void renderAuth(@PathVariable String oauthType, @PathVariable String type, HttpServletResponse response) throws IOException, TCloudException, TCloudException
    {
        AuthRequest authRequest = socialLoginService.renderAuth(oauthType);
        response.sendRedirect(authRequest.authorize(oauthType + StringConstant.DOUBLE_COLON + AuthStateUtils.createState()) + "::" + type);
    }

    /**
     * 登录成功后的回调
     *
     * @param oauthType 第三方登录类型
     * @param callback  携带返回的信息
     * @return String
     */
    @GetMapping("/{oauthType}/callback")
    public String login(@PathVariable String oauthType, AuthCallback callback, String state, Model model)
    {
        CommonResult commonResult = null;
        try
        {
            String type = StringUtils.substringAfterLast(state, StringConstant.DOUBLE_COLON);
            if (StringUtils.equals(type, TYPE_BIND))
            {
                commonResult = socialLoginService.resolveBind(oauthType, callback);
            }
            else
            {
                commonResult = socialLoginService.resolveLogin(oauthType, callback);
            }
            model.addAttribute("response", commonResult);
            model.addAttribute("frontUrl", frontUrl);
            return "result";
        }
        catch (Exception e)
        {
            String errorMessage = T.util().http().containChinese(e.getMessage()) ? e.getMessage() : "第三方登录失败";
            model.addAttribute("error", e.getMessage());
            return "fail";
        }
    }

    /**
     * 绑定并登录
     *
     * @param bindUser bindUser
     * @param authUser authUser
     * @return FebsResponse
     */
    @ResponseBody
    @PostMapping("bind/login")
    public CommonResult bindLogin(@Valid BindUser bindUser, AuthUser authUser) throws TCloudException
    {
        OAuth2AccessToken oAuth2AccessToken = this.socialLoginService.bindLogin(bindUser, authUser);
        return CommonResult.success(oAuth2AccessToken);
    }

    /**
     * 注册并登录
     *
     * @param registUser registUser
     * @param authUser   authUser
     * @return FebsResponse
     */
    @ResponseBody
    @PostMapping("sign/login")
    public CommonResult signLogin(@Valid BindUser registUser, AuthUser authUser) throws TCloudException
    {
        OAuth2AccessToken oAuth2AccessToken = this.socialLoginService.signLogin(registUser, authUser);
        return CommonResult.success(oAuth2AccessToken);
    }

    /**
     * 绑定
     *
     * @param bindUser bindUser
     * @param authUser authUser
     */
    @ResponseBody
    @PostMapping("bind")
    public void bind(BindUser bindUser, AuthUser authUser) throws TCloudException
    {
        this.socialLoginService.bind(bindUser, authUser);
    }

    /**
     * 解绑
     *
     * @param bindUser  bindUser
     * @param oauthType oauthType
     */
    @ResponseBody
    @DeleteMapping("unbind")
    public void unbind(BindUser bindUser, String oauthType) throws TCloudException
    {
        this.socialLoginService.unbind(bindUser, oauthType);
    }

    /**
     * 根据用户名获取绑定关系
     *
     * @param username 用户名
     * @return FebsResponse
     */
    @ResponseBody
    @GetMapping("connections/{username}")
    public CommonResult findUserConnections(@NotBlank(message = "{required}") @PathVariable String username)
    {
        List<UserConnection> userConnections = this.socialLoginService.findUserConnections(username);
        return CommonResult.success(userConnections);
    }
}
