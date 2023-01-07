package com.apple.service.impl;

import com.apple.domain.entity.UserRole;
import com.apple.mapper.UserRoleMapper;
import com.apple.service.UserRoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service("userRoleService")
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements UserRoleService {
}
