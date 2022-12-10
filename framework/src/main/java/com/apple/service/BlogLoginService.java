package com.apple.service;

import com.apple.domain.ResponseResult;
import com.apple.domain.entity.User;

public interface BlogLoginService {
    ResponseResult login(User user);

    ResponseResult logout();
}
