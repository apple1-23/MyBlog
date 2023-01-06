package com.apple.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleMenuTreeVo implements Serializable {

    //菜单树
    private List<MenuVos> menus;

    //角色所关联的菜单权限id列表
    private List<Long> checkedKeys;
}
