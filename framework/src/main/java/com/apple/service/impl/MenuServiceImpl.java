package com.apple.service.impl;

import com.apple.constants.SystemConstants;
import com.apple.domain.ResponseResult;
import com.apple.domain.entity.Menu;
import com.apple.domain.vo.MenuListVo;
import com.apple.enums.AppHttpCodeEnum;
import com.apple.mapper.MenuMapper;
import com.apple.service.MenuService;
import com.apple.utils.BeanCopyUtils;
import com.apple.utils.SecurityUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service("menuService")
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {
    @Override
    public List<String> selectPermsByUserId(Long id) {
        //如果是管理员，返回所有的权限
        if(SecurityUtils.isAdmin()){
            LambdaQueryWrapper<Menu> wrapper = new LambdaQueryWrapper<>();
            wrapper.in(Menu::getMenuType, SystemConstants.MENU,SystemConstants.BUTTON);
            wrapper.eq(Menu::getStatus,SystemConstants.STATUS_NORMAL);
            List<Menu> menus = list(wrapper);
            List<String> perms = menus.stream()
                    .map(Menu::getPerms)
                    .collect(Collectors.toList());
            return perms;
        }
        //否则返回去所具有的权限
        return getBaseMapper().selectPermsByUserId(id);
    }

    @Override
    public List<Menu> selectRouterMenuTreeByUserId(Long userId) {
        MenuMapper menuMapper = getBaseMapper();
        List<Menu> menus = null;
        //判断是否是管理员
        if(SecurityUtils.isAdmin()){
            //如果是 返回所有符合要求的Menu
            menus = menuMapper.selectAllRouterMenu();
        }else {
            //否则 当前用户所具有的Menu
            menus = menuMapper.selectRouterMenuTreeByUserId(userId);
        }
        //构建tree
        List<Menu> menuTree = builderMenuTree(menus,0L);

        return menuTree;
    }

    /**
     * 菜单列表
     * @param status 状态
     * @param menuName 菜单名称
     * @return
     */
    @Override
    public ResponseResult getList(String status, String menuName) {
        LambdaQueryWrapper<Menu> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(StringUtils.hasText(status),Menu::getStatus,status);
        lambdaQueryWrapper.like(StringUtils.hasText(menuName),Menu::getMenuName,menuName);
        lambdaQueryWrapper.orderByAsc(Menu::getParentId,Menu::getOrderNum);
        List<Menu> menus = list(lambdaQueryWrapper);
        List<MenuListVo> menuListVos = BeanCopyUtils.copyBeanList(menus, MenuListVo.class);
        return ResponseResult.okResult(menuListVos);
    }

    /**
     * 新增菜单
     * @param menu
     * @return
     */
    @Override
    public ResponseResult addMenu(Menu menu) {
        save(menu);
        return ResponseResult.okResult();
    }

    /**
     * 修改菜单
     * @param menu
     * @return
     */
    @Override
    public ResponseResult updateMenu(Menu menu) {
        Menu origin = getById(menu.getId());
        if(menu.getParentId().equals(menu.getId())){
            return ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR,
                    "修改菜单'"+origin.getMenuName()+"'失败，上级菜单不能选择自己");
        }else {
            updateById(menu);
            return ResponseResult.okResult();
        }
    }

    /**
     * 删除菜单
     * @param menuId
     * @return
     */
    @Override
    public ResponseResult deleteMenu(Long menuId) {
        Menu origin = getById(menuId);
        if(getChildren(origin,list()).size() > 0){
            return ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR,
                    "存在子菜单不允许删除");
        }else {
            getBaseMapper().deleteById(menuId);
            return ResponseResult.okResult();
        }
    }

    /**
     * 给没有父菜单的设置 children
     * @param menus
     * @param parentId
     * @return
     */
    private List<Menu> builderMenuTree(List<Menu> menus, Long parentId) {
        List<Menu> menuTree = menus.stream()
                .filter(menu -> menu.getParentId().equals(parentId))
                .map(menu -> menu.setChildren(getChildren(menu, menus)))
                .collect(Collectors.toList());
        return menuTree;
    }

    /**
     * 获取存入参数的 子Menu集合
     * @param menu
     * @param menus
     * @return
     */
    private List<Menu> getChildren(Menu menu,List<Menu> menus) {
        List<Menu> childrenList = menus.stream()
                .filter(menu1 -> menu1.getParentId().equals(menu.getId()))
                .map(m -> m.setChildren(getChildren(m,menus)))
                .collect(Collectors.toList());
        return childrenList;
    }
}
