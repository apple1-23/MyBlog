package com.apple.service;

import com.apple.domain.ResponseResult;
import com.apple.domain.dto.AddCategoryDto;
import com.apple.domain.entity.Category;
import com.apple.domain.vo.CategoryVo;
import com.apple.domain.vo.PageVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface CategoryService extends IService<Category> {
    ResponseResult getCategoryList();

    List<CategoryVo> listAllCategory();

    ResponseResult<PageVo> list(Integer pageNum, Integer pageSize, String name, String status);

    ResponseResult addCategory(AddCategoryDto addCategoryDto);

    ResponseResult<CategoryVo> getCategory(Long id);

    ResponseResult updateCategory(CategoryVo categoryVo);

    ResponseResult deleteCategory(Long id);
}
