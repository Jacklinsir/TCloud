package com.tcloud.common.core.domain;

import com.tcloud.common.core.domain.system.Dept;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = true)
public class DeptTree extends Tree<Dept> {

    private Integer orderNum;
}
