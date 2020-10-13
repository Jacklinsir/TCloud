package com.tcloud.common.datasource.starter.interceptor;

import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.baomidou.mybatisplus.extension.handlers.AbstractSqlParserHandler;
import com.tcloud.common.core.domain.CurrentUser;
import com.tcloud.common.core.toolkit.T;
import com.tcloud.common.datasource.starter.annotation.ConditionalDataPermission;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

import java.io.StringReader;
import java.sql.Connection;
import java.util.Properties;
/**
 * Description: 数据权限拦截器
 * <br/>
 * DataPermissionInterceptor
 * Created by laiql on 2020/10/13 13:59.
 */
@SuppressWarnings("all")
@Slf4j
@Intercepts({@Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})})
public class DataPermissionInterceptor extends AbstractSqlParserHandler implements Interceptor
{
    @Override
    public Object intercept(Invocation invocation) throws Throwable
    {
        StatementHandler statementHandler = PluginUtils.realTarget(invocation.getTarget());
        MetaObject metaObject = SystemMetaObject.forObject(statementHandler);
        this.sqlParser(metaObject);
        MappedStatement mappedStatement = (MappedStatement) metaObject.getValue("delegate.mappedStatement");
        BoundSql boundSql = (BoundSql) metaObject.getValue("delegate.boundSql");
        //获取参数对象
        Object parameterObject = boundSql.getParameterObject();
        // 数据权限只针对查询语句
        if (SqlCommandType.SELECT == mappedStatement.getSqlCommandType())
        {
            //获取贴有注解的方法
            ConditionalDataPermission dataPermission = getConditionalDataPermission(mappedStatement);
            if (shouldFilter(mappedStatement, dataPermission))
            {
                String id = mappedStatement.getId();
                log.info("\n 数据权限过滤 method -> {}", id);
                String originSql = boundSql.getSql();
                String dataPermissionSql = dataPermissionSql(originSql, dataPermission);
                metaObject.setValue("delegate.boundSql.sql", dataPermissionSql);
                log.info("\n originSql -> {} \n dataPermissionSql: {}", originSql, dataPermissionSql);
            }
        }
        return invocation.proceed();
    }

    private String dataPermissionSql(String originSql, ConditionalDataPermission dataPermission)
    {
        try
        {
            if (StringUtils.isBlank(dataPermission.field()))
            {
                return originSql;
            }
            CurrentUser user = T.util().http().getCurrentUser();
            if (ObjectUtils.isEmpty(user))
            {
                return originSql;
            }
            CCJSqlParserManager parserManager = new CCJSqlParserManager();
            Select select = (Select) parserManager.parse(new StringReader(originSql));
            PlainSelect plainSelect = (PlainSelect) select.getSelectBody();
            Table fromItem = (Table) plainSelect.getFromItem();
            String selectTableName = fromItem.getAlias() == null ? fromItem.getName() : fromItem.getAlias().getName();
            String dataPermissionSql = String.format("%s.%s in (%s)", selectTableName, dataPermission.field(), StringUtils.defaultIfBlank(user.getDeptIds(), "'WITHOUT PERMISSIONS'"));

            if (plainSelect.getWhere() == null)
            {
                plainSelect.setWhere(CCJSqlParserUtil.parseCondExpression(dataPermissionSql));
            }
            else
            {
                plainSelect.setWhere(new AndExpression(plainSelect.getWhere(), CCJSqlParserUtil.parseCondExpression(dataPermissionSql)));
            }
            return select.toString();
        }
        catch (Exception e)
        {
            log.warn("get data permission sql fail: {}", e.getMessage());
            return originSql;
        }
    }

    private boolean shouldFilter(MappedStatement mappedStatement, ConditionalDataPermission dataPermission)
    {
        if (dataPermission != null)
        {
            //获取id名
            String methodName = StringUtils.substringAfterLast(mappedStatement.getId(), ".");
            //获取注解前缀名
            String methodPrefix = dataPermission.methodPrefix();
            if (StringUtils.isNotBlank(methodName) && StringUtils.startsWith(methodName, methodPrefix))
            {
                return Boolean.TRUE;
            }
            String[] methods = dataPermission.methods();
            for (String method : methods)
            {
                if (StringUtils.startsWith(method, methodName))
                {
                    return Boolean.TRUE;
                }
            }
        }
        return Boolean.FALSE;
    }

    private ConditionalDataPermission getConditionalDataPermission(MappedStatement mappedStatement)
    {
        String mappedStatementId = mappedStatement.getId();
        ConditionalDataPermission conditionalDataPermission = null;
        try
        {
            String className = mappedStatementId.substring(0, mappedStatementId.lastIndexOf("."));
            final Class<?> aClass = Class.forName(className);
            if (aClass.isAnnotationPresent(ConditionalDataPermission.class))
            {
                conditionalDataPermission = aClass.getAnnotation(ConditionalDataPermission.class);
            }
        }
        catch (Exception e)
        {
            log.debug("获取数据权限注解异常: {}", e.getMessage());
        }
        return conditionalDataPermission;
    }

    @Override
    public Object plugin(Object target)
    {
        if (target instanceof StatementHandler)
        {
            return Plugin.wrap(target,this);
        }
        return target;
    }

    @Override
    public void setProperties(Properties properties)
    {
    }
}
