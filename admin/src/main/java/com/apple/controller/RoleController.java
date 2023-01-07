package com.apple.controller;

import com.apple.domain.ResponseResult;
import com.apple.domain.dto.AddMenuDto;
import com.apple.domain.dto.AddRoleDto;
import com.apple.domain.vo.PageVo;
import com.apple.domain.vo.RoleChangeVo;
import com.apple.domain.vo.RoleListVo;
import com.apple.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("system/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @GetMapping("/list")
    public ResponseResult<PageVo> list(Integer pageNum,Integer pageSize,String roleName,Integer status){
        return roleService.list(pageNum,pageSize,roleName,status);
    }

    /**
     * 更改角色状态
     * @param roleVo
     * @return
     */
    @PutMapping("/changeStatus")
    public ResponseResult changeStatus(@RequestBody RoleChangeVo roleVo){
        return roleService.changeStatus(roleVo);
    }

    /**
     * 添加角色
     * @param dto
     * @return
     */
    @PostMapping
    public ResponseResult addRole(@RequestBody AddRoleDto dto){
        return roleService.addRole(dto);
    }

    /**
     * 角色信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ResponseResult<RoleListVo> getRole(@PathVariable Long id){
        return ResponseResult.okResult(roleService.getById(id));
    }

    /**
     * 更新角色
     * @param roleDto
     * @return
     */
    @PutMapping
    public ResponseResult updateRole(@RequestBody AddRoleDto roleDto){
        return roleService.updateRole(roleDto);
    }

    /**
     * 删除角色
     * @param id
     * @return
     */
    @DeleteMapping("{id}")
    public ResponseResult deleteRole(@PathVariable Long id){
        return roleService.deleteRole(id);
    }

    /**
     * 查询全部角色
     * @return
     */
    @GetMapping("/listAllRole")
    public ResponseResult listAllRole(){
        return roleService.listAllRole();
    }

}
