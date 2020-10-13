package com.tcloud.auth.properties;

import com.tcloud.common.core.domain.constant.ImageTypeConstant;
import lombok.Data;

/**
 * Description: 验证码属性
 * <br/>
 * TCloudValidateCodeProperties
 * Created by laiql on 2020/10/13 17:31.
 */

@Data
public class TCloudValidateCodeProperties
{

    /**
     * 验证码有效时间，单位秒
     */
    private Long time = 120L;
    /**
     * 验证码类型，可选值 png和 gif
     */
    private String type = ImageTypeConstant.PNG;
    /**
     * 图片宽度，px
     */
    private Integer width = 130;
    /**
     * 图片高度，px
     */
    private Integer height = 48;
    /**
     * 验证码位数
     */
    private Integer length = 4;
    /**
     * 验证码值的类型
     * 1. 数字加字母
     * 2. 纯数字
     * 3. 纯字母
     */
    private Integer charType = 2;
}
