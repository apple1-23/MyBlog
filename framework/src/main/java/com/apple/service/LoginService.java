package com.apple.service;

import com.apple.domain.ResponseResult;
import com.apple.domain.entity.User;

public interface LoginService {
    ResponseResult login(User user);

}
