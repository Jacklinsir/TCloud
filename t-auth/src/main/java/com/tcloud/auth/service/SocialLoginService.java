package com.tcloud.auth.service;

import com.tcloud.auth.domain.BindUser;
import com.tcloud.auth.domain.UserConnection;
import com.tcloud.common.core.exception.TCloudException;
import com.tcloud.common.core.result.CommonResult;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthUser;
import me.zhyd.oauth.request.AuthRequest;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

import java.util.List;

/**
 * Description:
 * <br/>
 * SocialLoginService
 * Created by laiql on 2020/10/13 17:36.
 */

public interface SocialLoginService
{

    /**
     * 解析第三方登录请求
     *
     * @param oauthType 第三方平台类型
     * @return AuthRequest
     * @throws TCloudException 异常
     */
    AuthRequest renderAuth(String oauthType) throws TCloudException;

    /**
     * 处理第三方登录（绑定页面）
     *
     * @param oauthType 第三方平台类型
     * @param callback  回调
     * @return FebsResponse
     * @throws TCloudException 异常
     */
    CommonResult resolveBind(String oauthType, AuthCallback callback) throws TCloudException;

    /**
     * 处理第三方登录（登录页面）
     *
     * @param oauthType 第三方平台类型
     * @param callback  回调
     * @return FebsResponse
     * @throws TCloudException 异常
     */
    CommonResult resolveLogin(String oauthType, AuthCallback callback) throws TCloudException;

    /**
     * 绑定并登录
     *
     * @param bindUser 绑定用户
     * @param authUser 第三方平台对象
     * @return OAuth2AccessToken 令牌对象
     * @throws TCloudException 异常
     */
    OAuth2AccessToken bindLogin(BindUser bindUser, AuthUser authUser) throws TCloudException;

    /**
     * 注册并登录
     *
     * @param registUser 注册用户
     * @param authUser   第三方平台对象
     * @return OAuth2AccessToken 令牌对象
     * @throws TCloudException 异常
     */
    OAuth2AccessToken signLogin(BindUser registUser, AuthUser authUser) throws TCloudException;

    /**
     * 绑定
     *
     * @param bindUser 绑定对象
     * @param authUser 第三方平台对象
     * @throws TCloudException 异常
     */
    void bind(BindUser bindUser, AuthUser authUser) throws TCloudException;

    /**
     * 解绑
     *
     * @param bindUser  绑定对象
     * @param oauthType 第三方平台对象
     * @throws TCloudException 异常
     */
    void unbind(BindUser bindUser, String oauthType) throws TCloudException;

    /**
     * 根据用户名获取绑定关系
     *
     * @param username 用户名
     * @return 绑定关系集合
     */
    List<UserConnection> findUserConnections(String username);
}
