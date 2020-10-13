package com.tcloud.auth.controller;

import com.tcloud.auth.domain.OauthClientDetails;
import com.tcloud.auth.service.OauthClientDetailsService;
import com.tcloud.common.core.domain.QueryRequest;
import com.tcloud.common.core.exception.TCloudException;
import com.tcloud.common.core.result.CommonResult;
import com.tcloud.common.core.toolkit.T;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.Map;

/**
 * @author Yuuki
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("client")
public class OauthClientDetailsController
{

    private final OauthClientDetailsService oauthClientDetailsService;

    @GetMapping("check/{clientId}")
    public boolean checkUserName(@NotBlank(message = "{required}") @PathVariable String clientId)
    {
        OauthClientDetails client = this.oauthClientDetailsService.findById(clientId);
        return client == null;
    }

    @GetMapping("secret/{clientId}")
    @PreAuthorize("hasAuthority('client:decrypt')")
    public CommonResult getOriginClientSecret(@NotBlank(message = "{required}") @PathVariable String clientId)
    {
        OauthClientDetails client = this.oauthClientDetailsService.findById(clientId);
        String origin = client != null ? client.getOriginSecret() : StringUtils.EMPTY;
        return CommonResult.success(origin);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('client:view')")
    public CommonResult oauthCliendetailsList(QueryRequest request, OauthClientDetails oAuthClientDetails)
    {
        Map<String, Object> dataTable = T.util().http().getDataTable(this.oauthClientDetailsService.findOauthClientDetails(request, oAuthClientDetails));
        return CommonResult.success(dataTable);
    }


    @PostMapping
    @PreAuthorize("hasAuthority('client:add')")
    public void addOauthCliendetails(@Valid OauthClientDetails oAuthClientDetails) throws TCloudException
    {
        try
        {
            this.oauthClientDetailsService.createOauthClientDetails(oAuthClientDetails);
        }
        catch (Exception e)
        {
            String message = "新增客户端失败";
            log.error(message, e);
            throw new TCloudException(message);
        }
    }

    @DeleteMapping
    @PreAuthorize("hasAuthority('client:delete')")
    public void deleteOauthCliendetails(@NotBlank(message = "{required}") String clientIds) throws TCloudException
    {
        try
        {
            this.oauthClientDetailsService.deleteOauthClientDetails(clientIds);
        }
        catch (Exception e)
        {
            String message = "删除客户端失败";
            log.error(message, e);
            throw new TCloudException(message);
        }
    }

    @PutMapping
    @PreAuthorize("hasAuthority('client:update')")
    public void updateOauthCliendetails(@Valid OauthClientDetails oAuthClientDetails) throws TCloudException
    {
        try
        {
            this.oauthClientDetailsService.updateOauthClientDetails(oAuthClientDetails);
        }
        catch (Exception e)
        {
            String message = "修改客户端失败";
            log.error(message, e);
            throw new TCloudException(message);
        }
    }
}
