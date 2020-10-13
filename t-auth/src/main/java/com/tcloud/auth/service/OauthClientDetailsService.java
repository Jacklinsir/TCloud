package com.tcloud.auth.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tcloud.auth.domain.OauthClientDetails;
import com.tcloud.common.core.domain.QueryRequest;
import com.tcloud.common.core.exception.TCloudException;

/**
 * @author Yuuki
 */
public interface OauthClientDetailsService extends IService<OauthClientDetails> {

    /**
     * 查询（分页）
     *
     * @param request            QueryRequest
     * @param oauthClientDetails oauthClientDetails
     * @return IPage<OauthClientDetails>
     */
    IPage<OauthClientDetails> findOauthClientDetails(QueryRequest request, OauthClientDetails oauthClientDetails);

    /**
     * 根据主键查询
     *
     * @param clientId clientId
     * @return OauthClientDetails
     */
    OauthClientDetails findById(String clientId);

    /**
     * 新增
     *
     * @param oauthClientDetails oauthClientDetails
     * @throws TCloudException FebsException
     */
    void createOauthClientDetails(OauthClientDetails oauthClientDetails) throws TCloudException;

    /**
     * 修改
     *
     * @param oauthClientDetails oauthClientDetails
     */
    void updateOauthClientDetails(OauthClientDetails oauthClientDetails);

    /**
     * 删除
     *
     * @param clientIds clientIds
     */
    void deleteOauthClientDetails(String clientIds);
}
