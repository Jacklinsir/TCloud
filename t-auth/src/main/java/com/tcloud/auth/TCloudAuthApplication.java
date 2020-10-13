package com.tcloud.auth;

import com.tcloud.common.security.annotation.EnableTCloudResourceServer;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * Description: 授权服务启动
 * <br/>
 * TCloudAuthApplication
 * Created by laiql on 2020/10/13 17:06.
 */

@SpringBootApplication
@EnableRedisHttpSession
@EnableTCloudResourceServer
@MapperScan("com.tcloud.auth.mapper")
public class TCloudAuthApplication
{

    public static void main(String[] args)
    {
        new SpringApplicationBuilder(TCloudAuthApplication.class).web(WebApplicationType.SERVLET).run(args);
    }
}
