package com.apple.controller;

import com.apple.domain.ResponseResult;
import com.apple.domain.dto.AddUserDto;
import com.apple.domain.vo.PageVo;
import com.apple.domain.vo.UserVo;
import com.apple.domain.vo.UserVos;
import com.apple.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("system/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/list")
    public ResponseResult<PageVo> list(Integer pageNum,Integer pageSize,String userName,String phonenumber,
                                       String status){
        return userService.list(pageNum,pageSize,userName,phonenumber,status);
    }

    /**
     * 新增用户
     * @param addUserDto
     * @return
     */
    @PostMapping
    public ResponseResult addUser(@RequestBody AddUserDto addUserDto){
        return userService.addUser(addUserDto);
    }

    /**
     * 删除用户
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public ResponseResult deleteUser(@PathVariable Long id){
        return userService.deleteUser(id);
    }

    /**
     * 查询用户角色信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ResponseResult<UserVo> getUserAndRole(@PathVariable Long id){
        return userService.getUser(id);
    }

    @PutMapping
    public ResponseResult updateUser(@RequestBody UserVos userVos){
        return userService.updateUser(userVos);
    }
}
