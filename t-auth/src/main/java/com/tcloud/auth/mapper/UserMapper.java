package com.tcloud.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tcloud.common.core.domain.system.SystemUser;
import com.tcloud.common.core.domain.system.UserDataPermission;

import java.util.List;

/**
 * Description:
 * <br/>
 * UserMapper
 * Created by laiql on 2020/10/13 17:24.
 */
public interface UserMapper extends BaseMapper<SystemUser> {

    /**
     * 获取用户
     *
     * @param username 用户名
     * @return 用户
     */
    SystemUser findByName(String username);

    /**
     * 获取用户数据权限
     *
     * @param userId 用户id
     * @return 数据权限
     */
    List<UserDataPermission> findUserDataPermissions(Long userId);
}
