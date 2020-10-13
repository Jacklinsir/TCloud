package com.tcloud.common.core.domain.constant;

import java.util.regex.Pattern;

/**
 * Description: 正则常量
 * <br/>
 * RegexpConstant
 * Created by laiql on 2020/10/13 17:14.
 */
public interface RegexpConstant {

    /**
     * 简单手机号正则（这里只是简单校验是否为 11位，实际规则更复杂）
     */
    String MOBILE = "[1]\\d{10}";
    /**
     * 中文正则
     */
    Pattern CHINESE = Pattern.compile("[\u4e00-\u9fa5]");

}
