package com.tcloud.common.security.annotation;

import com.tcloud.common.security.configure.TCloudResourceServerConfigure;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Description: 安全配置启动注解
 * <br/>
 * EnableTCloudResourceServer
 * Created by laiql on 2020/10/13 16:44.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(TCloudResourceServerConfigure.class)
public @interface EnableTCloudResourceServer
{
}
