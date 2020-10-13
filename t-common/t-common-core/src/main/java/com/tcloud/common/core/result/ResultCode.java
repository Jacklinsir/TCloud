package com.tcloud.common.core.result;

/**
 * Description: 枚举异常状态码和信息
 * <br/>
 * ResultCode
 * Created by laiql on 2020/10/9 10:20.
 */
public enum ResultCode implements IErrorCode
{
    SUCCESS(200, "操作成功"),
    FAILED(500, "操作失败"),
    VALIDATE_FAILED(404, "参数检验失败"),
    UNAUTHORIZED(401, "暂未登录或token已经过期"),
    FORBIDDEN(403, "没有相关权限");

    private long code;
    private String message;

    private ResultCode(long code, String message) {
        this.code = code;
        this.message = message;
    }

    public long getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
