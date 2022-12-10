package com.apple.service;

import com.apple.domain.ResponseResult;
import com.apple.domain.entity.Category;
import com.baomidou.mybatisplus.extension.service.IService;

public interface CategoryService extends IService<Category> {
    ResponseResult getCategoryList();
}
