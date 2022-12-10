package com.apple.service;

import com.apple.domain.ResponseResult;
import com.apple.domain.entity.Link;
import com.baomidou.mybatisplus.extension.service.IService;

public interface LinkService extends IService<Link> {

    ResponseResult getAllLink();
}
