package com.tcloud.auth.service.impl;

import cn.hutool.core.util.StrUtil;
import com.tcloud.auth.domain.BindUser;
import com.tcloud.auth.domain.UserConnection;
import com.tcloud.auth.manager.UserManager;
import com.tcloud.auth.properties.TCloudAuthProperties;
import com.tcloud.auth.service.SocialLoginService;
import com.tcloud.auth.service.UserConnectionService;
import com.tcloud.common.core.domain.constant.GrantTypeConstant;
import com.tcloud.common.core.domain.constant.ParamsConstant;
import com.tcloud.common.core.domain.constant.SocialConstant;
import com.tcloud.common.core.domain.constant.StringConstant;
import com.tcloud.common.core.domain.system.SystemUser;
import com.tcloud.common.core.exception.TCloudException;
import com.tcloud.common.core.result.CommonResult;
import com.tcloud.common.core.toolkit.T;
import com.xkcoding.justauth.AuthRequestFactory;
import lombok.RequiredArgsConstructor;
import me.zhyd.oauth.config.AuthSource;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.model.AuthUser;
import me.zhyd.oauth.request.AuthRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.password.ResourceOwnerPasswordTokenGranter;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Description:
 * <br/>
 * SocialLoginServiceImpl
 * Created by laiql on 2020/10/13 17:37.
 */
@Service
@RequiredArgsConstructor
public class SocialLoginServiceImpl implements SocialLoginService
{

    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";

    private static final String NOT_BIND = "not_bind";
    private static final String SOCIAL_LOGIN_SUCCESS = "social_login_success";

    private final UserManager userManager;
    private final AuthRequestFactory factory;
    private final TCloudAuthProperties properties;
    private final PasswordEncoder passwordEncoder;
    private final UserConnectionService userConnectionService;
    private final ResourceOwnerPasswordTokenGranter granter;
    private final RedisClientDetailsService redisClientDetailsService;

    @Override
    public AuthRequest renderAuth(String oauthType) throws TCloudException
    {
        return factory.get(getAuthSource(oauthType));
    }

    @Override
    public CommonResult resolveBind(String oauthType, AuthCallback callback) throws TCloudException
    {
        AuthRequest authRequest = factory.get(getAuthSource(oauthType));
        AuthResponse<?> response = authRequest.login(resolveAuthCallback(callback));
        if (response.ok())
        {
            return CommonResult.success(response.getData());
        }
        else
        {
            throw new TCloudException(String.format("第三方登录失败，%s", response.getMsg()));
        }
    }

    @Override
    public CommonResult resolveLogin(String oauthType, AuthCallback callback) throws TCloudException
    {
        AuthRequest authRequest = factory.get(getAuthSource(oauthType));
        AuthResponse<?> response = authRequest.login(resolveAuthCallback(callback));
        if (response.ok())
        {
            AuthUser authUser = (AuthUser) response.getData();
            UserConnection userConnection = userConnectionService.selectByCondition(authUser.getSource().toString(), authUser.getUuid());
            if (userConnection == null)
            {
                return CommonResult.failed(authUser, NOT_BIND);
            }
            else
            {
                SystemUser user = userManager.findByName(userConnection.getUserName());
                if (user == null)
                {
                    throw new TCloudException("系统中未找到与第三方账号对应的账户");
                }
                OAuth2AccessToken oAuth2AccessToken = getOauth2AccessToken(user);
//                febsResponse.put(USERNAME, user.getUsername());
                return CommonResult.failed(oAuth2AccessToken, SOCIAL_LOGIN_SUCCESS);
            }
        }
        else
        {
            throw new TCloudException(String.format("第三方登录失败，%s", response.getMsg()));
        }
    }

    @Override
    public OAuth2AccessToken bindLogin(BindUser bindUser, AuthUser authUser) throws TCloudException
    {
        SystemUser systemUser = userManager.findByName(bindUser.getBindUsername());
        if (systemUser == null || !passwordEncoder.matches(bindUser.getBindPassword(), systemUser.getPassword()))
        {
            throw new TCloudException("绑定系统账号失败，用户名或密码错误！");
        }
        this.createConnection(systemUser, authUser);
        return this.getOauth2AccessToken(systemUser);
    }

    @Override
    public OAuth2AccessToken signLogin(BindUser registUser, AuthUser authUser) throws TCloudException
    {
        SystemUser user = this.userManager.findByName(registUser.getBindUsername());
        if (user != null)
        {
            throw new TCloudException("该用户名已存在！");
        }
        String encryptPassword = passwordEncoder.encode(registUser.getBindPassword());
        SystemUser systemUser = this.userManager.registerUser(registUser.getBindUsername(), encryptPassword);
        this.createConnection(systemUser, authUser);
        return this.getOauth2AccessToken(systemUser);
    }

    @Override
    public void bind(BindUser bindUser, AuthUser authUser) throws TCloudException
    {
        String username = bindUser.getBindUsername();
        if (isCurrentUser(username))
        {
            UserConnection userConnection = userConnectionService.selectByCondition(authUser.getSource().toString(), authUser.getUuid());
            if (userConnection != null)
            {
                throw new TCloudException("绑定失败，该第三方账号已绑定" + userConnection.getUserName() + "系统账户");
            }
            SystemUser systemUser = new SystemUser();
            systemUser.setUsername(username);
            this.createConnection(systemUser, authUser);
        }
        else
        {
            throw new TCloudException("绑定失败，您无权绑定别人的账号");
        }
    }

    @Override
    public void unbind(BindUser bindUser, String oauthType) throws TCloudException
    {
        String username = bindUser.getBindUsername();
        if (isCurrentUser(username))
        {
            this.userConnectionService.deleteByCondition(username, oauthType);
        }
        else
        {
            throw new TCloudException("解绑失败，您无权解绑别人的账号");
        }
    }

    @Override
    public List<UserConnection> findUserConnections(String username)
    {
        return this.userConnectionService.selectByCondition(username);
    }

    private void createConnection(SystemUser systemUser, AuthUser authUser)
    {
        UserConnection userConnection = new UserConnection();
        userConnection.setUserName(systemUser.getUsername());
        userConnection.setProviderName(authUser.getSource().toString());
        userConnection.setProviderUserId(authUser.getUuid());
        userConnection.setProviderUserName(authUser.getUsername());
        userConnection.setImageUrl(authUser.getAvatar());
        userConnection.setNickName(authUser.getNickname());
        userConnection.setLocation(authUser.getLocation());
        this.userConnectionService.createUserConnection(userConnection);
    }

    private AuthCallback resolveAuthCallback(AuthCallback callback)
    {
        int stateLength = 3;
        String state = callback.getState();
        String[] strings = StringUtils.splitByWholeSeparatorPreserveAllTokens(state, StringConstant.DOUBLE_COLON);
        if (strings.length == stateLength)
        {
            callback.setState(strings[0] + StringConstant.DOUBLE_COLON + strings[1]);
        }
        return callback;
    }

    private AuthSource getAuthSource(String type) throws TCloudException
    {
        if (StrUtil.isNotBlank(type))
        {
            return AuthSource.valueOf(type.toUpperCase());
        }
        else
        {
            throw new TCloudException(String.format("暂不支持%s第三方登录", type));
        }
    }

    private boolean isCurrentUser(String username)
    {
        String currentUsername = T.util().http().getCurrentUsername();
        return StringUtils.equalsIgnoreCase(username, currentUsername);
    }

    private OAuth2AccessToken getOauth2AccessToken(SystemUser user) throws TCloudException
    {
        final HttpServletRequest httpServletRequest = T.util().http().getHttpServletRequest();
        httpServletRequest.setAttribute(ParamsConstant.LOGIN_TYPE, SocialConstant.SOCIAL_LOGIN);
        String socialLoginClientId = properties.getSocialLoginClientId();
        ClientDetails clientDetails = null;
        try
        {
            clientDetails = redisClientDetailsService.loadClientByClientId(socialLoginClientId);
        }
        catch (Exception e)
        {
            throw new TCloudException("获取第三方登录可用的Client失败");
        }
        if (clientDetails == null)
        {
            throw new TCloudException("未找到第三方登录可用的Client");
        }
        Map<String, String> requestParameters = new HashMap<>(5);
        requestParameters.put(ParamsConstant.GRANT_TYPE, GrantTypeConstant.PASSWORD);
        requestParameters.put(USERNAME, user.getUsername());
        requestParameters.put(PASSWORD, SocialConstant.setSocialLoginPassword());

        String grantTypes = String.join(StringConstant.COMMA, clientDetails.getAuthorizedGrantTypes());
        TokenRequest tokenRequest = new TokenRequest(requestParameters, clientDetails.getClientId(), clientDetails.getScope(), grantTypes);
        return granter.grant(GrantTypeConstant.PASSWORD, tokenRequest);
    }
}
