package com.apple.controller;

import com.apple.domain.ResponseResult;
import com.apple.domain.dto.AddMenuDto;
import com.apple.domain.entity.Menu;
import com.apple.domain.vo.MenuListVo;
import com.apple.service.MenuService;
import com.apple.utils.BeanCopyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/system/menu")
public class MenuController {

    @Autowired
    private MenuService menuService;

    /**
     * 菜单列表
     * @param status
     * @param menuName
     * @return
     */
    @GetMapping("/list")
    public ResponseResult getList(String status,String menuName){
        return menuService.getList(status,menuName);
    }

    /**
     * 新增菜单
     * @return
     */
    @PostMapping
    public ResponseResult addMenu(@RequestBody AddMenuDto addMenuDto){
        Menu menu = BeanCopyUtils.copyBean(addMenuDto, Menu.class);
        return menuService.addMenu(menu);

    }

    /**
     * 根据id查询菜单
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ResponseResult<MenuListVo> getMenuById(@PathVariable Long id){
        MenuListVo menuListVo = BeanCopyUtils.copyBean(menuService.getById(id), MenuListVo.class);
        return ResponseResult.okResult(menuListVo);
    }

    /**
     * 修改菜单
     * @param menu
     * @return
     */
    @PutMapping
    public ResponseResult updateMenu(@RequestBody Menu menu){
        return menuService.updateMenu(menu);
    }

    @DeleteMapping("/{menuId}")
    public ResponseResult deleteMenu(@PathVariable Long menuId){
        return menuService.deleteMenu(menuId);
    }
}
