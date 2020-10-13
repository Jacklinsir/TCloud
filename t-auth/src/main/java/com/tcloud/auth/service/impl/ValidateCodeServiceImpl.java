package com.tcloud.auth.service.impl;

import com.tcloud.auth.properties.TCloudAuthProperties;
import com.tcloud.auth.properties.TCloudValidateCodeProperties;
import com.tcloud.auth.service.ValidateCodeService;
import com.tcloud.common.core.domain.constant.ImageTypeConstant;
import com.tcloud.common.core.domain.constant.ParamsConstant;
import com.tcloud.common.core.domain.constant.TCloudConstant;
import com.tcloud.common.core.exception.ValidateCodeException;
import com.tcloud.common.redis.service.RedisService;
import com.wf.captcha.GifCaptcha;
import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.base.Captcha;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 验证码服务
 *
 * @author MrBird
 */
@Service
@RequiredArgsConstructor
public class ValidateCodeServiceImpl implements ValidateCodeService
{

    private final RedisService redisService;
    private final TCloudAuthProperties properties;

    @Override
    public void create(HttpServletRequest request, HttpServletResponse response) throws IOException, ValidateCodeException
    {
        String key = request.getParameter(ParamsConstant.VALIDATE_CODE_KEY);
        if (StringUtils.isBlank(key))
        {
            throw new ValidateCodeException("验证码key不能为空");
        }
        TCloudValidateCodeProperties code = properties.getCode();
        setHeader(response, code.getType());

        Captcha captcha = createCaptcha(code);
        redisService.set(TCloudConstant.CODE_PREFIX + key, StringUtils.lowerCase(captcha.text()), code.getTime());
        captcha.out(response.getOutputStream());
    }

    @Override
    public void check(String key, String value) throws ValidateCodeException
    {
        Object codeInRedis = redisService.get(TCloudConstant.CODE_PREFIX + key);
        if (StringUtils.isBlank(value))
        {
            throw new ValidateCodeException("请输入验证码");
        }
        if (codeInRedis == null)
        {
            throw new ValidateCodeException("验证码已过期");
        }
        if (!StringUtils.equalsIgnoreCase(value, String.valueOf(codeInRedis)))
        {
            throw new ValidateCodeException("验证码不正确");
        }
    }

    private Captcha createCaptcha(TCloudValidateCodeProperties code)
    {
        Captcha captcha = null;
        if (StringUtils.equalsIgnoreCase(code.getType(), ImageTypeConstant.GIF))
        {
            captcha = new GifCaptcha(code.getWidth(), code.getHeight(), code.getLength());
        }
        else
        {
            captcha = new SpecCaptcha(code.getWidth(), code.getHeight(), code.getLength());
        }
        captcha.setCharType(code.getCharType());
        return captcha;
    }

    private void setHeader(HttpServletResponse response, String type)
    {
        if (StringUtils.equalsIgnoreCase(type, ImageTypeConstant.GIF))
        {
            response.setContentType(MediaType.IMAGE_GIF_VALUE);
        }
        else
        {
            response.setContentType(MediaType.IMAGE_PNG_VALUE);
        }
        response.setHeader(HttpHeaders.PRAGMA, "No-cache");
        response.setHeader(HttpHeaders.CACHE_CONTROL, "No-cache");
        response.setDateHeader(HttpHeaders.EXPIRES, 0L);
    }
}
