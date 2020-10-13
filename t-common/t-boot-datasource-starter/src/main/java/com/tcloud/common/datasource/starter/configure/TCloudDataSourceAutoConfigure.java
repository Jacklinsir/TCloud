package com.tcloud.common.datasource.starter.configure;

import com.baomidou.mybatisplus.core.parser.ISqlParser;
import com.baomidou.mybatisplus.extension.parsers.BlockAttackSqlParser;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import com.tcloud.common.datasource.starter.interceptor.DataPermissionInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.util.ArrayList;
import java.util.List;

/**
 * Description: 自动装配
 * <br/>
 * TCloudDataSourceAutoConfigure
 * Created by laiql on 2020/10/13 11:14.
 */
@Configuration
public class TCloudDataSourceAutoConfigure
{
    /**
     * 注册数据权限
     *
     * @return
     */
    @Bean
    @Order(-1)
    public DataPermissionInterceptor dataPermissionInterceptor()
    {
        return new DataPermissionInterceptor();
    }

    /**
     * 注册分页插件
     * @return
     */
    @Bean
    @Order(-1)
    public PaginationInterceptor paginationInterceptor()
    {
        PaginationInterceptor interceptor = new PaginationInterceptor();
        List<ISqlParser> iSqlParsers = new ArrayList<>();
        // 攻击 SQL 阻断解析器、加入解析链
        iSqlParsers.add(new BlockAttackSqlParser());
        interceptor.setSqlParserList(iSqlParsers);
        return interceptor;
    }
}
