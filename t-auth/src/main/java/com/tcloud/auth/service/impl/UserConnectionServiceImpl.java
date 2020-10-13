package com.tcloud.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tcloud.auth.domain.UserConnection;
import com.tcloud.auth.mapper.UserConnectionMapper;
import com.tcloud.auth.service.UserConnectionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Description:
 * <br/>
 * UserConnectionServiceImpl
 * Created by laiql on 2020/10/13 17:48.
 */
@Service
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public class UserConnectionServiceImpl extends ServiceImpl<UserConnectionMapper, UserConnection> implements UserConnectionService
{

    @Override
    public UserConnection selectByCondition(String providerName, String providerUserId)
    {
        LambdaQueryWrapper<UserConnection> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserConnection::getProviderName, providerName).eq(UserConnection::getProviderUserId, providerUserId);
        return this.baseMapper.selectOne(queryWrapper);
    }

    @Override
    public List<UserConnection> selectByCondition(String username)
    {
        LambdaQueryWrapper<UserConnection> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserConnection::getUserName, username);
        return this.baseMapper.selectList(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createUserConnection(UserConnection userConnection)
    {
        this.baseMapper.insert(userConnection);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByCondition(String username, String providerName)
    {
        LambdaQueryWrapper<UserConnection> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserConnection::getUserName, username);
        queryWrapper.eq(UserConnection::getProviderName, providerName);
        this.remove(queryWrapper);
    }

}
