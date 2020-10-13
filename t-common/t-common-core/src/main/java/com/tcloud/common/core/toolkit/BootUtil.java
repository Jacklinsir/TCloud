package com.tcloud.common.core.toolkit;

import org.springframework.core.env.Environment;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public enum  BootUtil
{
    INSTANCE;

    public void printSystemUpBanner(Environment environment)
    {
        String banner = "-----------------------------------------\n" +
                "服务启动成功，时间：" +LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "\n" +
                "服务名称：" + environment.getProperty("spring.application.name") + "\n" +
                "端口号：" + environment.getProperty("server.port") + "\n" +
                "-----------------------------------------";
    }
}
