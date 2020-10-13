package com.tcloud.auth.domain;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * Description:
 * <br/>
 * BindUser
 * Created by laiql on 2020/10/13 17:59.
 */

@Data
public class BindUser implements Serializable {

    private static final long serialVersionUID = -3890998115990166651L;

    @NotBlank(message = "{required}")
    private String bindUsername;
    @NotBlank(message = "{required}")
    private String bindPassword;
}
