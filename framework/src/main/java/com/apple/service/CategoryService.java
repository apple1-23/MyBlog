package com.apple.service;

import com.apple.domain.ResponseResult;
import com.apple.domain.entity.Category;
import com.apple.domain.vo.CategoryVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface CategoryService extends IService<Category> {
    ResponseResult getCategoryList();

    List<CategoryVo> listAllCategory();
}
