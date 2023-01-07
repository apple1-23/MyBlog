package com.apple.service;

import com.apple.domain.ResponseResult;
import com.apple.domain.dto.AddUserDto;
import com.apple.domain.entity.User;
import com.apple.domain.vo.PageVo;
import com.apple.domain.vo.UserVo;
import com.apple.domain.vo.UserVos;
import com.baomidou.mybatisplus.extension.service.IService;

public interface UserService extends IService<User> {
    ResponseResult userInfo();

    ResponseResult updateUserInfo(User user);

    ResponseResult register(User user);

    ResponseResult<PageVo> list(Integer pageNum, Integer pageSize,String userName ,String phonenumber, String status
                                );

    ResponseResult addUser(AddUserDto addUserDto);

    ResponseResult deleteUser(Long id);

    ResponseResult<UserVo> getUser(Long id);

    ResponseResult updateUser(UserVos userVos);
}
