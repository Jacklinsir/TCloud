package com.tcloud.common.core.result;

/**
 * Description: 状态码接口
 * <br/>
 * IErrorCode
 * Created by laiql on 2020/10/9 10:20.
 */
public interface IErrorCode {
    long getCode();

    String getMessage();
}
