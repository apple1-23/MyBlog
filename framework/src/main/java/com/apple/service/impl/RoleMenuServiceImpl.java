package com.apple.service.impl;

import com.apple.domain.entity.RoleMenu;
import com.apple.mapper.RoleMenuMapper;
import com.apple.service.RoleMenuService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service("roleMenuService")
public class RoleMenuServiceImpl extends ServiceImpl<RoleMenuMapper, RoleMenu> implements RoleMenuService {
}
