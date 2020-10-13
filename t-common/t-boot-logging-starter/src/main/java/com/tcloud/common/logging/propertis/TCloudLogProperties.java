package com.tcloud.common.logging.propertis;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Description: 日志配置屬性
 * <br/>
 * TCloudLogProperties
 * Created by laiql on 2020/10/13 15:13.
 */
@Data
@ConfigurationProperties(prefix = "tcloud.log")
public class TCloudLogProperties
{
    /**
     * 日志上传地址
     */
    private String logstashHost = "127.0.0.1:4560";

    /**
     * 是否开启controller层api调用的日志
     */
    private Boolean enableLogForController = false;

    /**
     * 是否开启ELK日志收集
     */
    private Boolean enableElk = false;
}
