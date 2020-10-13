package com.tcloud.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tcloud.common.core.domain.system.Menu;

import java.util.List;

/**
 * Description: MenuMapper
 * <br/>
 * MenuMapper
 * Created by laiql on 2020/10/13 17:12.
 */
public interface MenuMapper extends BaseMapper<Menu>
{

    /**
     * 获取用户权限集
     *
     * @param username 用户名
     * @return 权限集合
     */
    List<Menu> findUserPermissions(String username);
}
