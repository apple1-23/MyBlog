package com.apple.service;

import com.apple.domain.ResponseResult;
import com.apple.domain.dto.AddMenuDto;
import com.apple.domain.dto.AddRoleDto;
import com.apple.domain.entity.Role;
import com.apple.domain.vo.PageVo;
import com.apple.domain.vo.RoleChangeVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface RoleService extends IService<Role> {
    List<String> selectRoleKeyByUserId(Long id);

    ResponseResult<PageVo> list(Integer pageNum, Integer pageSize, String roleName, Integer status);

    ResponseResult changeStatus(RoleChangeVo roleVo);

    ResponseResult addRole(AddRoleDto dto);

    ResponseResult updateRole(AddRoleDto roleDto);

    ResponseResult deleteRole(Long id);

    ResponseResult listAllRole();
}
