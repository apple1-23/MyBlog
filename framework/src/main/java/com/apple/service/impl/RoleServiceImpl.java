package com.apple.service.impl;

import com.apple.constants.SystemConstants;
import com.apple.domain.ResponseResult;
import com.apple.domain.dto.AddMenuDto;
import com.apple.domain.dto.AddRoleDto;
import com.apple.domain.entity.Role;
import com.apple.domain.entity.RoleMenu;
import com.apple.domain.vo.PageVo;
import com.apple.domain.vo.RoleChangeVo;
import com.apple.domain.vo.RoleListVo;
import com.apple.mapper.RoleMapper;
import com.apple.mapper.RoleMenuMapper;
import com.apple.service.RoleMenuService;
import com.apple.service.RoleService;
import com.apple.utils.BeanCopyUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service("roleService")
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    @Autowired
    private RoleMenuMapper roleMenuMapper;

    @Override
    public List<String> selectRoleKeyByUserId(Long id) {
        //判断是否是管理员 如果是返回集合中只需要有admin
        if(id == 1L){
            List<String> roleKeys = new ArrayList<>();
            roleKeys.add("admin");
            return roleKeys;
        }
        //否则查询用户所具有的角色信息
        return getBaseMapper().selectRoleKeyByUserId(id);
    }

    @Override
    public ResponseResult<PageVo> list(Integer pageNum, Integer pageSize, String roleName, Integer status) {
        LambdaQueryWrapper<Role> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(StringUtils.hasText(roleName),Role::getRoleName,roleName);
        lambdaQueryWrapper.eq(Objects.nonNull(status),Role::getStatus,status);
        lambdaQueryWrapper.orderByAsc(Role::getRoleSort);
        Page<Role> page = new Page<>();
        page.setCurrent(pageNum);
        page.setSize(pageSize);
        page(page, lambdaQueryWrapper);
        PageVo pageVo = new PageVo(BeanCopyUtils.copyBeanList(page.getRecords(),RoleListVo.class),
                page.getTotal());
        return ResponseResult.okResult(pageVo);
    }

    /**
     * 更改角色状态
     * @param roleVo
     * @return
     */
    @Override
    public ResponseResult changeStatus(RoleChangeVo roleVo) {
        Role role = BeanCopyUtils.copyBean(roleVo, Role.class);
        role.setId(roleVo.getRoleId());
        updateById(role);
        return ResponseResult.okResult();
    }

    /**
     * 添加角色
     * @param dto
     * @return
     */
    @Override
    @Transactional
    public ResponseResult addRole(AddRoleDto dto) {
        Role role = BeanCopyUtils.copyBean(dto, Role.class);
        save(role);

        //添加角色-菜单关联
        for (Long menuId : dto.getMenuIds()) {
            roleMenuMapper.insert(new RoleMenu(role.getId(),menuId));
        }

        return ResponseResult.okResult();
    }

    /**
     * 更改角色和菜单关联
     * @param roleDto
     * @return
     */
    @Override
    @Transactional
    public ResponseResult updateRole(AddRoleDto roleDto) {
        Role role = BeanCopyUtils.copyBean(roleDto, Role.class);
        updateById(role);
        //删除原关联
        LambdaQueryWrapper<RoleMenu> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(RoleMenu::getRoleId,role.getId());
        roleMenuMapper.delete(lambdaQueryWrapper);
        //添加角色-菜单关联
        for (Long menuId : roleDto.getMenuIds()) {
            roleMenuMapper.insert(new RoleMenu(role.getId(),menuId));
        }

        return ResponseResult.okResult();
    }

    /**
     * 删除角色
     * @param id
     * @return
     */
    @Override
    @Transactional
    public ResponseResult deleteRole(Long id) {
        getBaseMapper().deleteById(id);
        //删除原关联
        LambdaQueryWrapper<RoleMenu> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(RoleMenu::getRoleId,id);
        roleMenuMapper.delete(lambdaQueryWrapper);

        return ResponseResult.okResult();
    }

    /**
     * 查询全部角色
     * @return
     */
    @Override
    public ResponseResult listAllRole() {
        LambdaQueryWrapper<Role> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Role::getStatus, SystemConstants.STATUS_NORMAL);
        List<Role> list = list(lambdaQueryWrapper);

        return ResponseResult.okResult(list);
    }
}
