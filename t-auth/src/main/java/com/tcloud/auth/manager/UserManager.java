package com.tcloud.auth.manager;

import com.tcloud.auth.mapper.MenuMapper;
import com.tcloud.auth.mapper.UserMapper;
import com.tcloud.auth.mapper.UserRoleMapper;
import com.tcloud.common.core.domain.constant.StringConstant;
import com.tcloud.common.core.domain.constant.TCloudConstant;
import com.tcloud.common.core.domain.system.Menu;
import com.tcloud.common.core.domain.system.SystemUser;
import com.tcloud.common.core.domain.system.UserDataPermission;
import com.tcloud.common.core.domain.system.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Description: 查询用户角色和权限资源
 * <br/>
 * UserManager
 * Created by laiql on 2020/10/13 17:27.
 */

@Service
@RequiredArgsConstructor
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class UserManager
{

    private final UserMapper userMapper;
    private final MenuMapper menuMapper;
    private final UserRoleMapper userRoleMapper;

    /**
     * 通过用户名查询用户信息
     *
     * @param username 用户名
     * @return 用户
     */
    public SystemUser findByName(String username)
    {
        SystemUser user = userMapper.findByName(username);
        if (user != null)
        {
            List<UserDataPermission> permissions = userMapper.findUserDataPermissions(user.getUserId());
            String deptIds = permissions.stream().map(p -> String.valueOf(p.getDeptId())).collect(Collectors.joining(StringConstant.COMMA));
            user.setDeptIds(deptIds);
        }
        return user;
    }

    /**
     * 通过用户名查询用户权限串
     *
     * @param username 用户名
     * @return 权限
     */
    public String findUserPermissions(String username)
    {
        List<Menu> userPermissions = menuMapper.findUserPermissions(username);
        return userPermissions.stream().map(Menu::getPerms).collect(Collectors.joining(StringConstant.COMMA));
    }

    /**
     * 注册用户
     *
     * @param username username
     * @param password password
     * @return SystemUser SystemUser
     */
    @Transactional(rollbackFor = Exception.class)
    public SystemUser registerUser(String username, String password)
    {
        SystemUser systemUser = new SystemUser();
        systemUser.setUsername(username);
        systemUser.setPassword(password);
        systemUser.setCreateTime(new Date());
        systemUser.setStatus(SystemUser.STATUS_VALID);
        systemUser.setSex(SystemUser.SEX_UNKNOW);
        systemUser.setAvatar(SystemUser.DEFAULT_AVATAR);
        systemUser.setDescription("注册用户");
        this.userMapper.insert(systemUser);

        UserRole userRole = new UserRole();
        userRole.setUserId(systemUser.getUserId());
        // 注册用户角色 ID
        userRole.setRoleId(TCloudConstant.REGISTER_ROLE_ID);
        this.userRoleMapper.insert(userRole);
        return systemUser;
    }
}
