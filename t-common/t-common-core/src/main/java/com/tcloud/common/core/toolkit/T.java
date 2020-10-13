package com.tcloud.common.core.toolkit;

public enum T
{
    CORE;

    public static T util()
    {
        return CORE;
    }

    public HttpUtil http()
    {
        return HttpUtil.INSTANCE;
    }

    public BootUtil bootUtil()
    {
        return BootUtil.INSTANCE;
    }
}
