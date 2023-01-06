package com.apple.service.impl;

import com.apple.constants.SystemConstants;
import com.apple.domain.ResponseResult;
import com.apple.domain.entity.Menu;
import com.apple.domain.entity.RoleMenu;
import com.apple.domain.vo.MenuListVo;
import com.apple.domain.vo.MenuVos;
import com.apple.domain.vo.RoleMenuTreeVo;
import com.apple.enums.AppHttpCodeEnum;
import com.apple.mapper.MenuMapper;
import com.apple.mapper.RoleMenuMapper;
import com.apple.service.MenuService;
import com.apple.utils.BeanCopyUtils;
import com.apple.utils.SecurityUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service("menuService")
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {

    @Autowired
    private RoleMenuMapper roleMenuMapper;

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
     * 菜单树
     * @return
     */
    @Override
    public ResponseResult treeSelect() {
        //所有菜单
        List<Menu> menus = getBaseMapper().selectAllMenu();
        List<MenuVos> vos = new ArrayList<>();
        for (Menu menu : menus) {
            MenuVos menuVos = BeanCopyUtils.copyProperties(menu, MenuVos.class);
            vos.add(menuVos);
        }
        //构建tree
        List<MenuVos> menuTree = builderMenuVoTree(vos,0L);

        return ResponseResult.okResult(menuTree);
    }

    /**
     * 通过角色id查询菜单树
     * @param id
     * @return
     */
    @Override
    public ResponseResult<RoleMenuTreeVo> getRoleMenuTree(Long id) {
        LambdaQueryWrapper<RoleMenu> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(RoleMenu::getRoleId,id);
        List<RoleMenu> roleMenus = roleMenuMapper.selectList(lambdaQueryWrapper);
        //提取角色关联的菜单id
        List<Long> menuIds = roleMenus.stream()
                .map(RoleMenu::getMenuId)
                .collect(Collectors.toList());

        //所有菜单树
        //所有菜单
        List<Menu> menus = getBaseMapper().selectAllMenu();
        List<MenuVos> vos = new ArrayList<>();
        for (Menu menu : menus) {
            MenuVos menuVos = BeanCopyUtils.copyProperties(menu, MenuVos.class);
            vos.add(menuVos);
        }
        //构建tree
        List<MenuVos> menuTree = builderMenuVoTree(vos,0L);


        RoleMenuTreeVo roleMenuTreeVo = new RoleMenuTreeVo();
        roleMenuTreeVo.setCheckedKeys(menuIds);
        roleMenuTreeVo.setMenus(menuTree);
        return ResponseResult.okResult(roleMenuTreeVo);
    }

    private List<MenuVos> builderMenuVoTree(List<MenuVos> menus, long l) {
        List<MenuVos> collect = menus.stream()
                .filter(menu -> menu.getParentId().equals(l))
                .map(menu -> menu.setChildren(getMenuVoChildren(menu,menus)))
                .collect(Collectors.toList());
        return collect;
    }

    private List<MenuVos> getMenuVoChildren(MenuVos menu,List<MenuVos> menus) {
        List<MenuVos> vosList = menus.stream()
                .filter(menu1 -> menu1.getParentId().equals(menu.getId()))
                .map(m -> m.setChildren(getMenuVoChildren(m, menus)))
                .collect(Collectors.toList());
        return vosList;
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
