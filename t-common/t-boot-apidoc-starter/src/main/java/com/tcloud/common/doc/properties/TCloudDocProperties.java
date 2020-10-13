package com.tcloud.common.doc.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Description: Apidoc文档属性对象
 * <br/>
 * TCloudDocProperties
 * Created by laiql on 2020/10/13 14:49.
 */
@Data
@ConfigurationProperties(prefix = "tcloud.doc")
public class TCloudDocProperties
{

    /**
     * 是否开启doc功能
     */
    private Boolean enable = true;
    /**
     * 接口扫描路径，如Controller路径
     */
    private String basePackage;
    /**
     * 文档标题
     */
    private String title;
    /**
     * 文档描述
     */
    private String description;
    /**
     * 文档描述颜色
     */
    private String descriptionColor = "#42b983";
    /**
     * 文档描述字体大小
     */
    private String descriptionFontSize = "14";
    /**
     * 服务url
     */
    private String termsOfServiceUrl;
    /**
     * 联系方式：姓名
     */
    private String name;
    /**
     * 联系方式：个人网站url
     */
    private String url;
    /**
     * 联系方式：邮箱
     */
    private String email;
    /**
     * 协议
     */
    private String license;
    /**
     * 协议地址
     */
    private String licenseUrl;
    /**
     * 版本
     */
    private String version;
}
