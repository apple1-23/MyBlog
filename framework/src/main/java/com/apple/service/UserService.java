package com.apple.service;

import com.apple.domain.ResponseResult;
import com.apple.domain.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;

public interface UserService extends IService<User> {
    ResponseResult userInfo();

    ResponseResult updateUserInfo(User user);
}
