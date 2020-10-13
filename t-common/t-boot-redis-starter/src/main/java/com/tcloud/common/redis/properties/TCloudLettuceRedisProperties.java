package com.tcloud.common.redis.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author MrBird
 */
@ConfigurationProperties(prefix = "tcloud.lettuce.redis")
public class TCloudLettuceRedisProperties
{

    /**
     * 是否开启Lettuce Redis
     */
    private Boolean enable = true;

    public Boolean getEnable()
    {
        return enable;
    }

    public void setEnable(Boolean enable)
    {
        this.enable = enable;
    }

    @Override
    public String toString()
    {
        return "TCloudLettuceRedisProperties: {" + "enable=" + enable + '}';
    }
}
