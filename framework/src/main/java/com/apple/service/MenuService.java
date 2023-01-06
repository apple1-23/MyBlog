package com.apple.service;

import com.apple.domain.ResponseResult;
import com.apple.domain.entity.Menu;
import com.apple.domain.vo.RoleMenuTreeVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface MenuService extends IService<Menu> {
    List<String> selectPermsByUserId(Long id);

    List<Menu> selectRouterMenuTreeByUserId(Long userId);

    ResponseResult getList(String status, String menuName);

    ResponseResult addMenu(Menu menu);

    ResponseResult updateMenu(Menu menu);

    ResponseResult deleteMenu(Long menuId);

    ResponseResult treeSelect();

    ResponseResult<RoleMenuTreeVo> getRoleMenuTree(Long id);
}
